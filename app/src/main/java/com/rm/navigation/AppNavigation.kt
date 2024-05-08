package com.rm.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rm.preferences.SharedPrefsManager
import com.rm.utils.empty
import com.rm.view.dashboard.ComponentDashboardScreen
import com.rm.view.login.ComponentLoginScreen
import com.rm.view.splash.ComponentSplashScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues,
    username: String = String.empty()
) {
    NavHost(navController = navController, startDestination = Route.AuthNav.Splash.route) {
        composable(route = Route.AuthNav.Splash.route) {
            ComponentSplashScreen(navigateToLogin = {
                navController.navigate(it) {
                    // clear stack till Splash screen
                    popUpTo(Route.AuthNav.Splash.route) {
                        inclusive = true
                    }
                }
            }, navigateToHome = {
                navController.navigate(it) {
                    // clear stack till Splash screen
                    popUpTo(Route.AuthNav.Splash.route) {
                        inclusive = true
                    }
                }
            })
        }
        composable(route = Route.AuthNav.Login.route) {
            ComponentLoginScreen(navigateToDashboard = {
                navController.navigate(it) {
                    // clear stack till Login screen as Splash is already cleared
                    popUpTo(Route.AuthNav.Login.route) {
                        inclusive = true
                    }
                }
            })
        }
        composable(route = Route.AuthNav.Dashboard.route) {
            it.arguments?.apply {
                ComponentDashboardScreen(logout = {
                    // Clear the current session
                    SharedPrefsManager.clearSession()

                    // Navigate to splash screen and clear all the back stack
                    navController.navigate(Route.AuthNav.Splash.route) {
                        navController.backQueue.clear()
                    }
                })
            }
        }
    }
}