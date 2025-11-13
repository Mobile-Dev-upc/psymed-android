package com.example.psymed.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.psymed.ui.components.PsyMedPrimaryButton
import com.example.psymed.ui.components.PsyMedTextButton
import com.example.psymed.ui.components.PsyMedTextField
import com.example.psymed.ui.theme.PsyMedColors
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onLogin: (String, String) -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(uiState.error) {
        val message = uiState.error
        if (!message.isNullOrBlank()) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "PSYMED",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = PsyMedColors.Primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Welcome",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = PsyMedColors.Primary,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sign in to your account",
                style = MaterialTheme.typography.bodyMedium.copy(color = PsyMedColors.TextSecondary)
            )

            Spacer(modifier = Modifier.height(32.dp))

            PsyMedTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                placeholder = "Enter your username",
                leadingIcon = Icons.Outlined.PersonOutline,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            PsyMedTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Enter your password",
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            PsyMedPrimaryButton(
                text = "Sign In",
                enabled = username.isNotBlank() && password.isNotBlank(),
                loading = uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                onLogin(username.trim(), password)
            }

            Spacer(modifier = Modifier.height(16.dp))

            PsyMedTextButton(
                text = "Don't have an account? Register",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onNavigateToRegister
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

