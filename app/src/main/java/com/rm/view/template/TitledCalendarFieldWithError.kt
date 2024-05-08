package com.rm.view.template

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rm.R
import com.rm.utils.getMutableStateValue
import com.rm.utils.setMutableStateValue
import com.rm.view.common.UITextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitledCalendarFieldWithError(
    title: Int,
    uiTextField: UITextField,
    hint: String,
    onCalendarClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed: Boolean by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed){
        if (isPressed) {
            // Click action
            onCalendarClick()
        }
    }

    Text(
        text = stringResource(id = title),
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.gray_9),
            letterSpacing = 2.sp
        )
    )

    SpaceTop(top = 12.dp)
    OutlinedTextField(
        value = getMutableStateValue(state = uiTextField.state),
        onValueChange = { setMutableStateValue(state = uiTextField.state, value = it) },
        placeholder = { Text(text = hint) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = false,
                onClick = { }),
        interactionSource = interactionSource,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        textStyle = TextStyle(color = colorResource(id = R.color.gray_9), fontSize = 16.sp),
        singleLine = true,
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_calendar_24),
                contentDescription = stringResource(id = R.string.content_desc_calendar)
            )
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = colorResource(id = R.color.black),
            unfocusedBorderColor = colorResource(id = R.color.black)
        ),
        isError = uiTextField.showError
    )

    if (uiTextField.showError) {
        SpaceTop(top = 12.dp)
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = uiTextField.errorMessage),
            color = MaterialTheme.colorScheme.error
        )
        SpaceTop(top = 20.dp)
    } else {
        SpaceTop(top = 20.dp)
    }
}