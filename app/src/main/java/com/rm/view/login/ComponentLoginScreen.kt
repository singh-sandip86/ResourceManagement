package com.rm.view.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rm.MainActivity
import com.rm.MainActivityViewModel
import com.rm.R
import com.rm.navigation.Route
import com.rm.utils.getMutableStateValue
import com.rm.view.template.OutlinedTextFieldWithError
import com.rm.view.template.SpaceTop
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ComponentLoginScreen(
    navigateToDashboard: (route: String) -> Unit,
    vm: LoginScreenViewModel = hiltViewModel(),
    mainActivityVM: MainActivityViewModel = viewModel(LocalContext.current as MainActivity)
) {
    LaunchedEffect(key1 = true, block = {
        vm.login.collect {
            if (it) {
                mainActivityVM.getMasterData()
            }
        }
    })

    LaunchedEffect(key1 = true, block = {
        mainActivityVM.projectNameSuccess.collect{
            if (it) {
                navigateToDashboard.invoke(Route.AuthNav.Dashboard.createRoute())
            }
        }
    })

    LaunchedEffect(key1 = true, block = {
        vm.combineAndValidate().collectLatest {
            if (it) {
                Log.d("Validate", "Form has error: $it")
            }
        }
    })

    InitView(vm, mainActivityVM)
}

@Composable
private fun InitView(
    vm: LoginScreenViewModel,
    mainActivityVM: MainActivityViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp)
    ) {
        SpaceTop(40.dp)
        Image(
            painter = painterResource(id = R.drawable.torinit_logo2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(200.dp)
        )

        SpaceTop(top = 10.dp)
        Text(
            text = "Proceed with your",
            style = TextStyle(
                fontSize = 24.sp,
                letterSpacing = 0.5.sp,
                color = Color.Black
            )
        )
        SpaceTop(8.dp)
        Text(
            text = "Login",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp,
                color = Color.Black
            )
        )

        SpaceTop(80.dp)
        OutlinedTextFieldWithError(
            uiTextField = vm.uiLogin.username,
            label = R.string.username,
            hint = R.string.username_hint
        )

        SpaceTop(12.dp)
        OutlinedTextFieldWithError(
            uiTextField = vm.uiLogin.password,
            label = R.string.password,
            hint = R.string.password_hint,
            isPassword = true
        )

        SpaceTop(30.dp)
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                vm.login(mainActivityVM)
            },
            enabled = getMutableStateValue(state = vm.uiLogin.enableLoginButton)
        ) {
            Text(text = stringResource(id = R.string.login))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ComponentLoginScreen({})
}
