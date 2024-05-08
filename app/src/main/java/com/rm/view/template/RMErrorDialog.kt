package com.rm.view.template

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rm.R

@Composable
fun RMCustomDialog(
    errorTitle: String?,
    errorMessage: String?,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = { }) {
        RMCustomDialogUI(
            errorTitle = errorTitle,
            errorMessage = errorMessage
        ) {
            onDismissRequest.invoke()
        }
    }
}

//Layout
@Composable
fun RMCustomDialogUI(
    modifier: Modifier = Modifier,
    errorTitle: String?,
    errorMessage: String?,
    onDismissRequest: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
    ) {
        Column(
            modifier.background(Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = errorTitle ?: stringResource(id = R.string.something_went_wrong),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = errorMessage ?: stringResource(id = R.string.GENERIC_TEMP_ERROR_MESSAGE),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(onClick = {
                    onDismissRequest.invoke()
                }) {
                    Text(
                        text = stringResource(id = R.string.lbl_ok),
                        fontWeight = FontWeight.ExtraBold,
//                        color = Color.Black,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
            }
        }
    }
}