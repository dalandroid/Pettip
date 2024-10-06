package net.pettip.app.navi.utils.service

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.pettip.app.data.network.ApiService
import net.pettip.app.data.utils.GPXWriter
import net.pettip.app.domain.entity.map.GPX_TICK_FORMAT
import net.pettip.app.domain.entity.map.Track
import net.pettip.app.navi.activity.MainActivity
import net.pettip.app.navi.R
import net.pettip.app.navi.component.LoadingState
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationService @Inject constructor(
    private val context: Context,
    private val apiService: ApiService,
    private val sharedPreferences: SharedPreferences
) : Service() {

    companion object {
        const val GPX_START = "START_FOREGROUND_GPX"
        const val GPX_STOP = "STOP_FOREGROUND_GPX"
        const val GPX_EVENT = "EVENT_FOREGROUND_GPX"

        fun loadTrackList(sharedPreferences: SharedPreferences): List<Track>? {
            val filePath = sharedPreferences.getString("GPXFileName", null)
            if (filePath.isNullOrEmpty()) {
                Log.d("FileLoad", "File path not found")
                return null
            }
            val file = File(filePath)
            if (!file.exists()) {
                Log.d("FileLoad", "File not found: ${file.absolutePath}")
                return null
            }

            val tracks = mutableListOf<Track>()

            try {
                val inputStream = FileInputStream(file)
                val factory = XmlPullParserFactory.newInstance()
                val parser = factory.newPullParser()
                parser.setInput(inputStream, "UTF-8")

                var eventType = parser.eventType
                var currentLocation: Location? = null
                var no: String = ""
                var event: Track.EVENT = Track.EVENT.NNN
                var uri: Uri = Uri.EMPTY

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    val tagName = parser.name

                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            when (tagName) {
                                "trkpt" -> {
                                    val lat = parser.getAttributeValue(null, "lat").toDouble()
                                    val lon = parser.getAttributeValue(null, "lon").toDouble()
                                    no = parser.getAttributeValue(null, "no") ?: Track.TRACK_ZERO_NUM
                                    event = try {
                                        Track.EVENT.valueOf(parser.getAttributeValue(null, "event") ?: "NNN")
                                    } catch (e: IllegalArgumentException) {
                                        Track.EVENT.NNN
                                    }
                                    currentLocation = Location("").apply {
                                        latitude = lat
                                        longitude = lon
                                    }
                                }
                                "time" -> {
                                    currentLocation?.time = Track.fromGpxTimeString(parser.nextText())
                                }
                                "speed" -> {
                                    currentLocation?.speed = parser.nextText().toFloat()
                                }
                                "bearing" -> {
                                    currentLocation?.bearing = parser.nextText().toFloat()
                                }
                                "ele" -> {
                                    currentLocation?.altitude = parser.nextText().toDouble()
                                }
                                "uri" -> {
                                    uri = Uri.parse(parser.nextText())
                                }
                            }
                        }
                        XmlPullParser.END_TAG -> {
                            if (tagName == "trkpt" && currentLocation != null) {
                                tracks.add(Track(loc = currentLocation, no = no, event = event, uri = uri))
                                currentLocation = null
                                no = Track.TRACK_ZERO_NUM
                                event = Track.EVENT.NNN
                                uri = Track.TRACK_ZERO_URI
                            }
                        }
                    }
                    eventType = parser.next()
                }
            } catch (e: Exception) {
                Log.e("FileLoad", "Error loading track list from file", e)
            }
            return tracks
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: Location? = null
    private var lastUpdateTime: Long = 0

    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())
    private val handler = Handler(Looper.getMainLooper())
    private var startTime: Long = 0
    private var previousTime: Long = 0
    private lateinit var notificationBuilder: NotificationCompat.Builder

    private val binder = LocalBinder()

    private val _trackList = MutableStateFlow<MutableList<Track>>(mutableListOf())
    val trackList: StateFlow<MutableList<Track>> = _trackList.asStateFlow()

    private val _elapsedTime = MutableStateFlow("00:00:00")
    val elapsedTime: StateFlow<String> = _elapsedTime.asStateFlow()

    private val _totalDistance = MutableStateFlow(0.0)
    val totalDistance: StateFlow<Double> = _totalDistance.asStateFlow()

    inner class LocalBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val action = intent?.action
        val event = intent?.getStringExtra("event")
        val no = intent?.getStringExtra("no")
        val uri = intent?.getStringExtra("uri")
        when (action) {
            GPX_START -> {
                if (sharedPreferences.getBoolean("ServiceRunning", false)) {
                    reloadForegroundService()
                } else {
                    startForegroundService()
                }
            }
            GPX_STOP -> {
                stopForegroundService()
            }
            GPX_EVENT -> {
                Log.d("LOG", "event : ${event}, no :${no} ")
                val trackEvent = when (event) {
                    "PEE" -> Track.EVENT.PEE
                    "POO" -> Track.EVENT.POO
                    "MRK" -> Track.EVENT.MRK
                    "IMG" -> Track.EVENT.IMG
                    else -> Track.EVENT.NNN
                }
                if (_trackList.value.isNotEmpty()) {
                    val newTrack = Track(
                        loc = _trackList.value.last().loc,
                        no = no ?: Track.TRACK_ZERO_NUM,
                        event = trackEvent,
                        uri = Uri.parse(uri ?: "")
                    )
                    _trackList.value = _trackList.value.toMutableList().apply { add(newTrack) }
                    savePathToFile(_trackList.value)
                }
            }
            else -> {
                Log.d("LOG", "Unknown action received")
            }
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::fusedLocationClient.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        sharedPreferences.edit().putBoolean("ServiceRunning", false).apply()
        handler.removeCallbacks(updateNotificationRunnable)
    }

    private fun shouldAddLocation(newLocation: Location, lastLocation: Location?, currentTime: Long): Boolean {
        if (lastLocation == null) return true
        val distance = newLocation.distanceTo(lastLocation)
        val timeDiff = currentTime - lastUpdateTime

        Log.d("DIS", "distance : ${distance}")

        return when {
            distance < 1f -> false
            distance >= 1f && distance <= 7f -> true
            distance > 7f && distance <= 15f && timeDiff >= 1000L -> true
            distance > 15f && distance <= 30f && timeDiff >= 2000L -> true
            distance > 30f && distance <= 50f && timeDiff >= 3000L -> true
            else -> false
        }
    }

    private fun savePathToFile(track: List<Track>) {
        coroutineScope.launch {
            try {
                val file = File("${gpxs(context)}/${name(track.first().loc.time)}.gpx")
                if (!file.exists()) {
                    file.parentFile?.mkdirs()
                    file.createNewFile()
                    sharedPreferences.edit().putString("GPXFileName", file.absolutePath).apply()
                }
                file.let { GPXWriter().write(tracks = track, file = file) }

                LoadingState.hide()

                Log.d("FileSave", "Path saved to file: ${file.absolutePath}")
            } catch (e: Exception) {

                LoadingState.hide()

                Log.e("FileSave", "Error saving path to file", e)
            }
        }
    }

    private fun saveEventToFile(event: Track.EVENT, track: List<Track>) {
        coroutineScope.launch {
            try {
                val file = File("${gpxs(context)}/${name(track.first().loc.time)}.gpx")
                if (!file.exists()) {
                    file.parentFile?.mkdirs()
                    file.createNewFile()
                    sharedPreferences.edit().putString("GPXFileName", file.absolutePath).apply()
                }
                file.let { GPXWriter().write(tracks = track, file = file) }

                Log.d("FileSave", "Path saved to file: ${file.absolutePath}")
            } catch (e: Exception) {

                Log.e("FileSave", "Error saving path to file", e)
            }
        }
    }

    private fun stopForegroundService() {
        Log.d("LocationService", "Stopping the foreground service...")
        stopForeground(true)
        _trackList.value = mutableListOf()
        _totalDistance.value = 0.0
        stopSelf()
        sharedPreferences.edit().putBoolean("ServiceRunning", false).apply()
        if (::fusedLocationClient.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        handler.removeCallbacks(updateNotificationRunnable)
    }

    private fun reloadForegroundService() {
        val preTrackList = loadTrackList(sharedPreferences)
        if (preTrackList != null) {
            _trackList.value = preTrackList.toMutableList()
        }
        previousTime = preTrackList?.last()?.loc?.time?.minus(preTrackList.first().loc.time) ?: 0L

        startTime = System.currentTimeMillis()
        notificationBuilder = createNotificationBuilder()
        startForeground(1, notificationBuilder.build())
        handler.post(updateNotificationRunnable)

        locationServiceStart()
    }

    private fun startForegroundService() {
        locationServiceStart()
        startTime = System.currentTimeMillis()
        notificationBuilder = createNotificationBuilder()
        startForeground(1, notificationBuilder.build())
        handler.post(updateNotificationRunnable)
    }

    private fun locationServiceStart() {
        sharedPreferences.edit().putBoolean("ServiceRunning", true).apply()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val currentTime = System.currentTimeMillis()
                    if (_trackList.value.isEmpty() || shouldAddLocation(location, lastLocation, currentTime)) {
                        lastUpdateTime = currentTime
                        val newTrack = Track(loc = location)
                        _trackList.value = _trackList.value.toMutableList().apply { add(newTrack) }
                        updateTotalDistance()
                        savePathToFile(_trackList.value)
                        LoadingState.hide()
                        Log.d("LocationService", "Location added and saved to file: $location")
                    } else {
                        LoadingState.hide()
                    }
                    lastLocation = location
                }
            }
        }

        startLocationUpdates()
    }

    private fun updateTotalDistance() {
        val trackPoints = _trackList.value
        var distance = 0.0
        if (trackPoints.size > 1) {
            for (i in 1 until trackPoints.size) {
                val start = trackPoints[i - 1].loc
                val end = trackPoints[i].loc
                distance += start.distanceTo(end)
            }
        }
        _totalDistance.value = distance
    }

    private fun createNotificationBuilder(): NotificationCompat.Builder {
        val channelId = "my_service_channel"
        val channelName = "My Service"

        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val activityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchActivityIntent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("행복한 산책중입니다")
            .setContentText("펫팁에서 위치정보를 확인중입니다.")
            .setSmallIcon(R.drawable.currentlocation)
            .setOngoing(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                R.drawable.baseline_camera_alt_24,
                "열기",
                activityPendingIntent
            )
            .setContentIntent(activityPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
    }

    private fun launchActivityIntent(): Intent {
        return Intent(this, MainActivity::class.java).apply {
            action = "GPX_OPEN"
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }

    private val updateNotificationRunnable = object : Runnable {
        override fun run() {
            val elapsedTimeValue = System.currentTimeMillis() - startTime + previousTime
            val hours = TimeUnit.MILLISECONDS.toHours(elapsedTimeValue)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTimeValue) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTimeValue) % 60

            val elapsedTimeText = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            _elapsedTime.value = elapsedTimeText
            notificationBuilder.setContentTitle("행복한 산책중입니다 - $elapsedTimeText")
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(1, notificationBuilder.build())

            handler.postDelayed(this, 1000)
        }
    }

    fun root(context: Context): String {
        return context.filesDir.path
    }

    fun gpxs(context: Context): String {
        return "${root(context)}/.GPX"
    }

    fun name(time: Long): String {
        return GPX_TICK_FORMAT.format(time)
    }
}