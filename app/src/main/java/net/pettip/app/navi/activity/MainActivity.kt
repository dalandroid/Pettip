package net.pettip.app.navi.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.pettip.app.navi.component.GlobalLoadingScreen
import net.pettip.app.navi.screen.MyApp
import net.pettip.app.navi.ui.theme.PettipTheme
import net.pettip.app.navi.utils.service.LocationService

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    private var locationService: LocationService? = null
    private var isServiceBound = false
    private val locationServiceState = mutableStateOf<LocationService?>(null)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            isServiceBound = true
            locationServiceState.value = locationService
            Log.d("LOG", "Service Connected : ${locationService.hashCode()}")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
            locationServiceState.value = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){

            }
        }

        splashScreen.setKeepOnScreenCondition{false}

        Intent(this, LocationService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        enableEdgeToEdge()
        setContent {

            navController = rememberNavController()

            PettipTheme {
                Surface {
                    MyApp(
                        navController = navController,
                        locationServiceState = locationServiceState
                    )
                    GlobalLoadingScreen()
                }
            }
        }
    }
}