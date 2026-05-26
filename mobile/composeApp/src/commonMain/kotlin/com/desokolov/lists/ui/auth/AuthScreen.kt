package com.desokolov.lists.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.desokolov.lists.presentation.auth.AuthEvent

@Composable
fun AuthScreen(onEvent: (AuthEvent) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Lists")
        Button(
            onClick = { /* Google Sign-In запускается платформо-специфично */ },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Войти через Google")
        }
    }
}
