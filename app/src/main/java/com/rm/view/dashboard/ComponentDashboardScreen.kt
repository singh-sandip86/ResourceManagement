package com.rm.view.dashboard

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rm.R
import com.rm.data.dashbord.BottomNavItem
import com.rm.navigation.HomeNavigation
import com.rm.navigation.Route
import com.rm.preferences.SharedPrefsManager
import com.rm.utils.empty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentDashboardScreen(
    logout: () -> Unit
) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    val navItems = remember {
        listOf(
            BottomNavItem(
                title = "Projects",
                route = Route.HomeNav.Project.route,
                icon = Icons.Rounded.Home,
                header = "Hello ${SharedPrefsManager.userName}"
            ),
            BottomNavItem(
                title = "Resources",
                route = Route.HomeNav.Resource.route,
                icon = Icons.Rounded.Settings
            )
        )
    }
    // Show topBar, drawerContent & bottomBar for only these screens
    val topBottomBarDrawer = remember {
        listOf(
            Route.HomeNav.Project.route,
            Route.HomeNav.Resource.route
        )
    }
    val backStackEntry = navController.currentBackStackEntryAsState()
    val current = backStackEntry.value?.destination?.route

    Scaffold(
        topBar = {
            if (topBottomBarDrawer.contains(current)) {
                val title = navItems.find { it.route == current }?.header
                TopBar(title, onSearchBarClick = {
                    navController.navigate(route = Route.HomeNav.ResourceSearch.route)
                })
            }
        },
        bottomBar = {
            if (topBottomBarDrawer.contains(current)) {
                BottomBar(navItems, backStackEntry, navController)
            }
        },
        containerColor = colorResource(id = R.color.white)
    ) { paddingValues ->
        HomeNavigation(navController, paddingValues)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(title: String?, onSearchBarClick: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colorResource(id = R.color.torinit),
            titleContentColor = colorResource(id = R.color.white)
        ),
        title = { Text(text = title ?: String.empty()) },
        actions = {
            IconButton(onClick = { onSearchBarClick() }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    modifier = Modifier,
                    contentDescription = "Search Back",
                    tint = colorResource(id = R.color.white)
                )
            }
        }
    )
}

@Composable
private fun BottomBar(
    navItems: List<BottomNavItem>,
    backStackEntry: State<NavBackStackEntry?>,
    navController: NavHostController
) {
    NavigationBar() {
        navItems.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        // To avoid calling same destination multiple times
                        launchSingleTop = true
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = "${item.title} Icon"
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ComponentDashboardScreen() {}
}
