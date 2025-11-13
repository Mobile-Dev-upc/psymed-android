package com.example.psymed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.psymed.data.ServiceLocator
import com.example.psymed.di.PsyMedViewModelFactory
import com.example.psymed.ui.auth.AuthViewModel
import com.example.psymed.ui.core.PsyMedApp
import com.example.psymed.ui.theme.PsymedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PsymedTheme {
                val factory = rememberViewModelFactory()
                val authViewModel: AuthViewModel = viewModel(factory = factory)
                PsyMedApp(authViewModel = authViewModel, factory = factory)
            }
        }
    }
}

@Composable
private fun rememberViewModelFactory(): PsyMedViewModelFactory {
    return remember {
        PsyMedViewModelFactory(
            repositories = ServiceLocator.repositories,
            tokenUpdater = ServiceLocator::updateToken
        )
    }
}