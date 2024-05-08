package com.rm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.navigation.compose.rememberNavController
import com.rm.navigation.AppNavigation
import com.rm.ui.theme.ResourceManagementTheme
import com.rm.view.template.RMCustomDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InitView(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitView(viewModel: MainActivityViewModel) {
    ResourceManagementTheme {
        val navController = rememberNavController()
        val showErrorDialog = remember { viewModel.showErrorDialog }
        val showLoader = remember { viewModel.showLoader }

        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold { paddingValues ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    AppNavigation(navController, paddingValues)
                    if (showLoader.value) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(enabled = false, onClick = {})
                                .background(colorResource(id = R.color.transparent_black_background)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = colorResource(id = R.color.torinit)
                            )
                        }
                    }
                    val errorTitle = viewModel.error?.peekContent()?.name
                    val errorMessage = viewModel.error?.peekContent()?.message //viewModel.error?.peekContent()?.toErrorMessage()
                    if (showErrorDialog.value) {
                        RMCustomDialog(errorTitle, errorMessage) {
                            viewModel.hideErrorDialog()
                            viewModel.launchLoginScreen()
                        }
                    }
                }
            }
        }
    }
}
