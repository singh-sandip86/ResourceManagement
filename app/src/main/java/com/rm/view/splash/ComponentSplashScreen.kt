package com.rm.view.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rm.MainActivity
import com.rm.MainActivityViewModel
import com.rm.R
import com.rm.navigation.Route
import com.rm.preferences.SharedPrefsManager
import kotlinx.coroutines.delay

@Composable
fun ComponentSplashScreen(
    navigateToLogin: (route: String) -> Unit,
    navigateToHome: (route: String) -> Unit,
    mainViewModel: MainActivityViewModel = viewModel(LocalContext.current as MainActivity)
) {
    val launchLoginScreen = remember { mainViewModel.launchLoginScreen }

    LaunchedEffect(key1 = Unit) {
        delay(2000L)
        if (SharedPrefsManager.isUserLoggedIn) {
            mainViewModel.getMasterData()
        } else {
            navigateToLogin.invoke(Route.AuthNav.Login.createRoute())
        }
    }

    LaunchedEffect(key1 = true, block = {
        mainViewModel.projectNameSuccess.collect{
            if (it) {
                navigateToHome.invoke(Route.AuthNav.Dashboard.createRoute())
            }
        }
    })

    if (launchLoginScreen.value) {
        navigateToLogin.invoke(Route.AuthNav.Login.createRoute())
        mainViewModel.setLaunchLoginScreenFalse()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.torinit)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.torinit_logo),
            contentDescription = "Profile Image",
            modifier = Modifier
                .width(200.dp)
                .height(200.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ComponentSplashScreen({}, {})
}
