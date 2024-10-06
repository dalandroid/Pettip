package net.pettip.app.navi.screen

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import net.pettip.app.navi.screen.login.view.IntroScreen
import net.pettip.app.navi.screen.login.view.LoginScreen
import net.pettip.app.navi.screen.main.view.HomeScreen
import net.pettip.app.navi.screen.main.view.MainScreen
import net.pettip.app.navi.utils.service.LocationService

/**
 * @Project     : PetTip-Android
 * @FileName    : MyApp
 * @Date        : 2024-08-07
 * @author      : CareBiz
 * @description : net.pettip.app.navi.screen
 * @see net.pettip.app.navi.screen.MyApp
 */
@Composable
fun MyApp(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    locationServiceState: MutableState<LocationService?>
){
    val locationService by locationServiceState

    NavHost(
        navController = navController,
        startDestination = Screen.MainScreen.route,
        modifier = modifier.background(Color.White)
    ) {
        composable(
            route = Screen.Intro.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://pettip.net"
                    action = Intent.ACTION_VIEW
                }
            )
        ){
            IntroScreen(
                onNext = {
                    navController.navigate(Screen.LoginScreen.route){
                        popUpTo(Screen.Intro.route){inclusive = true}
                    }
                }
            )
        }

        composable(Screen.LoginScreen.route){
            LoginScreen(
                navMain = {
                    navController.navigate(Screen.MainScreen.route){
                        popUpTo(Screen.LoginScreen.route){inclusive = true}
                    }
                }
            )
        }

        composable(Screen.MainScreen.route){
            MainScreen(
                navController = navController
            )
        }
    }
}

sealed class Screen(val route: String) {
    data object Intro : Screen("intro")
    data object MainScreen : Screen("mainScreen")
    data object LoginScreen : Screen("loginScreen")
    //data object HomeScreen : Screen("homeScreen")
    //data object WalkScreen : Screen("walkScreen")
    //data object TipScreen : Screen("tipScreen")
    //data object MyScreen : Screen("myScreen")
}