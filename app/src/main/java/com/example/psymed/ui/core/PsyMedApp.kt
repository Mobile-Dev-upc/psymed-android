package com.example.psymed.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.psymed.di.PsyMedViewModelFactory
import com.example.psymed.ui.analytics.AnalyticsViewModel
import com.example.psymed.ui.appointments.SessionsViewModel
import com.example.psymed.ui.auth.AuthViewModel
import com.example.psymed.ui.auth.LoginScreen
import com.example.psymed.ui.auth.RegisterScreen
import com.example.psymed.ui.health.HealthViewModel
import com.example.psymed.ui.medication.MedicationsViewModel
import com.example.psymed.ui.patient.PatientHomeScreen
import com.example.psymed.ui.profile.PatientProfileScreen
import com.example.psymed.ui.tasks.TasksViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.psymed.ui.professional.ProfessionalAddPatientScreen
import com.example.psymed.ui.professional.ProfessionalHomeScreen
import com.example.psymed.ui.professional.ProfessionalPatientDetailScreen
import com.example.psymed.ui.professional.ProfessionalPatientsViewModel

@Composable
fun PsyMedApp(
    authViewModel: AuthViewModel,
    factory: PsyMedViewModelFactory
) {
    val navController = rememberNavController()
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(authState.isAuthenticated, authState.account?.role) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (authState.isAuthenticated) {
            val target = if (authState.isProfessional) Routes.ProfessionalHome else Routes.PatientHome
            if (currentRoute != target) {
                navController.navigate(target) {
                    popUpTo(Routes.Login) { inclusive = true }
                }
            }
        } else {
            if (currentRoute != Routes.Login) {
                navController.navigate(Routes.Login) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Login
    ) {
        composable(Routes.Login) {
            val uiState = authState
            LoginScreen(
                uiState = uiState,
                onLogin = { username, password -> authViewModel.signIn(username, password) },
                onNavigateToRegister = { navController.navigate(Routes.Register) }
            )
        }
        composable(Routes.Register) {
            val uiState = authState
            RegisterScreen(
                uiState = uiState,
                onRegister = { request, callback ->
                    authViewModel.registerProfessional(request, callback)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Routes.PatientHome) {
            val sessionsViewModel: SessionsViewModel = viewModel(factory = factory)
            val medicationsViewModel: MedicationsViewModel = viewModel(factory = factory)
            val tasksViewModel: TasksViewModel = viewModel(factory = factory)
            val healthViewModel: HealthViewModel = viewModel(factory = factory)
            val analyticsViewModel: AnalyticsViewModel = viewModel(factory = factory)

            PatientHomeScreen(
                authState = authState,
                sessionsViewModel = sessionsViewModel,
                healthViewModel = healthViewModel,
                medicationsViewModel = medicationsViewModel,
                tasksViewModel = tasksViewModel,
                analyticsViewModel = analyticsViewModel,
                onNavigateToProfile = { navController.navigate(Routes.PatientProfile) }
            )
        }
        composable(Routes.PatientProfile) {
            PatientProfileScreen(
                authState = authState,
                onLogout = {
                    authViewModel.signOut()
                }
            )
        }

        composable(Routes.ProfessionalHome) {
            val patientsViewModel: ProfessionalPatientsViewModel = viewModel(factory = factory)
            ProfessionalHomeScreen(
                authState = authState,
                viewModel = patientsViewModel,
                onAddPatient = { navController.navigate(Routes.ProfessionalAddPatient) },
                onViewPatient = { patientId ->
                    navController.navigate("${Routes.ProfessionalPatientDetail}/$patientId")
                },
                onDeletePatient = { patientId ->
                    patientsViewModel.deletePatient(patientId) { _, _ -> }
                },
                onLogout = { authViewModel.signOut() }
            )
        }
        composable(Routes.ProfessionalAddPatient) {
            val patientsViewModel: ProfessionalPatientsViewModel = viewModel(factory = factory)
            ProfessionalAddPatientScreen(
                authState = authState,
                onCreatePatient = { request, callback ->
                    authViewModel.createPatientForProfessional(request) { success, message ->
                        if (success) {
                            authState.professionalProfile?.id?.let { id ->
                                patientsViewModel.loadPatients(id)
                            }
                        }
                        callback(success, message)
                    }
                },
                onClose = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "${Routes.ProfessionalPatientDetail}/{patientId}",
            arguments = listOf(
                navArgument("patientId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getInt("patientId") ?: return@composable
            val patientsViewModel: ProfessionalPatientsViewModel = viewModel(factory = factory)
            val sessionsViewModel: SessionsViewModel = viewModel(factory = factory)
            val medicationsViewModel: MedicationsViewModel = viewModel(factory = factory)
            val tasksViewModel: TasksViewModel = viewModel(factory = factory)
            val analyticsViewModel: AnalyticsViewModel = viewModel(factory = factory)

            ProfessionalPatientDetailScreen(
                patientId = patientId,
                authState = authState,
                viewModel = patientsViewModel,
                sessionsViewModel = sessionsViewModel,
                medicationsViewModel = medicationsViewModel,
                tasksViewModel = tasksViewModel,
                analyticsViewModel = analyticsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

