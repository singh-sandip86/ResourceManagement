package com.rm.navigation

import com.rm.utils.Constants

sealed class Route(val route: String) {
    object AuthNav {
        object Splash : Route(Constants.SPLASH_SCREEN)

        object Login : Route(Constants.LOGIN_SCREEN) {
            fun createRoute() = Constants.LOGIN_SCREEN
        }

        object Dashboard : Route(Constants.DASHBOARD_SCREEN) {
            fun createRoute() = Constants.DASHBOARD_SCREEN
        }
    }

    object HomeNav {
        object Project : Route(Constants.PROJECT_SCREEN)
        object ProjectDetails : Route(Constants.PROJECT_DETAIL_SCREEN)
        object AddProject : Route(Constants.ADD_PROJECT_SCREEN)
        object Resource : Route(Constants.RESOURCE_SCREEN)
        object ResourceSearch : Route(Constants.RESOURCE__SEARCH_SCREEN)
        object ResourceDetails : Route(Constants.RESOURCE_DETAIL_SCREEN)
        object AddResource : Route(Constants.ADD_RESOURCE_SCREEN)
    }
}
