package com.example.psymed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.psymed.data.ServiceLocator
import com.example.psymed.di.PsyMedViewModelFactory
import com.example.psymed.ui.auth.AuthViewModel
import com.example.psymed.ui.core.PsyMedApp
import com.example.psymed.ui.emergency.ShakeDetectionWrapper
import com.example.psymed.ui.theme.PsymedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PsymedTheme {
                val context = LocalContext.current
                val factory = rememberViewModelFactory(context)
                val authViewModel: AuthViewModel = viewModel(factory = factory)
                val authState = authViewModel.uiState.collectAsStateWithLifecycle().value
                
                ShakeDetectionWrapper(
                    authState = authState,
                    factory = factory
                ) {
                    PsyMedApp(authViewModel = authViewModel, factory = factory)
                }
            }
        }
    }
}

@Composable
private fun rememberViewModelFactory(context: android.content.Context): PsyMedViewModelFactory {
    return remember {
        PsyMedViewModelFactory(
            repositories = ServiceLocator.repositories,
            tokenUpdater = ServiceLocator::updateToken,
            context = context
        )
    }
}