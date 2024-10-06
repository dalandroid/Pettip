package net.pettip.app.domain.entity.map

/**
 * @Project     : carepet-android
 * @FileName    : _GPX.kt
 * @Date        : 2023. 09. 13.
 * @author      : isyuun@care-biz.co.kr
 * @description :
 */
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

const val GPX_VERSION = "1.1"
const val GPX_CREATOR = "PetTip"
const val GPX_NAMESPACE = "http://www.topografix.com/GPX/1/1"
const val GPX_XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance"

val GPX_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
val GPX_TICK_FORMAT = SimpleDateFormat("yyyyMMdd.HHmmss", Locale.getDefault())
val GPX_DECIMAL_FORMAT_1 = DecimalFormat("0.0")
val GPX_DECIMAL_FORMAT_3 = DecimalFormat("0.000")
val GPX_DECIMAL_FORMAT_7 = DecimalFormat("0.0000000")

const val TAG_GPX = "gpx"
const val TAG_VERSION = "version"
const val TAG_CREATOR = "creator"
const val TAG_METADATA = "metadata"
const val TAG_TRACK = "trk"
const val TAG_SEGMENT = "trkseg"
const val TAG_TRACK_POINT = "trkpt"
const val TAG_LAT = "lat"
const val TAG_LON = "lon"
const val TAG_ELEVATION = "ele"
const val TAG_TIME = "time"
const val TAG_SYM = "sym"
const val TAG_WAY_POINT = "wpt"
const val TAG_ROUTE = "rte"
const val TAG_ROUTE_POINT = "rtept"
const val TAG_NAME = "name"
const val TAG_DESC = "desc"
const val TAG_CMT = "cmt"
const val TAG_SRC = "src"
const val TAG_LINK = "link"
const val TAG_NUMBER = "number"
const val TAG_TYPE = "type"
const val TAG_TEXT = "text"
const val TAG_AUTHOR = "author"
const val TAG_COPYRIGHT = "copyright"
const val TAG_KEYWORDS = "keywords"
const val TAG_BOUNDS = "bounds"
const val TAG_MIN_LAT = "minlat"
const val TAG_MIN_LON = "minlon"
const val TAG_MAX_LAT = "maxlat"
const val TAG_MAX_LON = "maxlon"
const val TAG_HREF = "href"
const val TAG_YEAR = "year"
const val TAG_LICENSE = "license"
const val TAG_EMAIL = "email"
const val TAG_ID = "id"
const val TAG_DOMAIN = "domain"

// extensions-related tags
const val TAG_EXTENSIONS = "extensions"
const val TAG_SPEED = "speed"
const val TAG_BEARING = "bearing"

val namespace: String? = null

const val TAG_URI = "uri"

open class _GPX {
    companion object {
        @JvmStatic
        fun calculateDuration(tracks: List<Track>): String {
            if (tracks.isEmpty()) return "N/A"

            val startTime = tracks.first().time
            val endTime = tracks.last().time
            val durationInMillis = endTime - startTime
            val seconds = durationInMillis / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60)
        }

        @JvmStatic
        fun calculateTotalDistance(tracks: List<Track>): String {
            if (tracks.isEmpty()) return "N/A"

            var totalDistance = 0.0
            for (i in 1 until tracks.size) {
                val prevLocation = tracks[i - 1]
                val currentLocation = tracks[i]
                val distance = prevLocation.location.distanceTo(currentLocation.location)
                totalDistance += distance
            }
            return String.format("%.2f km", totalDistance / 1000)
        }

        @JvmStatic
        fun calculateMaxAltitudeGap(tracks: List<Track>): String {
            if (tracks.isEmpty()) return "N/A"

            var maxAltitudeGap = 0.0
            for (i in 1 until tracks.size) {
                val prevLocation = tracks[i - 1]
                val currentLocation = tracks[i]
                val altitudeGap = Math.abs(currentLocation.altitude - prevLocation.altitude)
                if (altitudeGap > maxAltitudeGap) {
                    maxAltitudeGap = altitudeGap
                }
            }
            return String.format("%.2f m", maxAltitudeGap)
        }

        @JvmStatic
        fun calculateMaxSpeed(tracks: List<Track>): String {
            if (tracks.isEmpty()) return "N/A"

            var maxSpeed = 0.0f
            for (location in tracks) {
                if (location.speed > maxSpeed) {
                    maxSpeed = location.speed
                }
            }
            return String.format("%.2f m/s", maxSpeed)
        }

        @JvmStatic
        fun calculateAvgSpeed(tracks: List<Track>): String {
            if (tracks.isEmpty()) return "N/A"

            var totalSpeed = 0.0f
            for (location in tracks) {
                totalSpeed += location.speed
            }
            val avgSpeed = totalSpeed / tracks.size
            return String.format("%.2f m/s", avgSpeed)
        }
    }
}
