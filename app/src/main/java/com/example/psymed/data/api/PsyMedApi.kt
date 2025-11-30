package com.example.psymed.data.api

import com.example.psymed.data.model.AuthResponseDto
import com.example.psymed.data.model.BiologicalFunctionDto
import com.example.psymed.data.model.BiologicalFunctionRequestDto
import com.example.psymed.data.model.MedicationDto
import com.example.psymed.data.model.MedicationRequestDto
import com.example.psymed.data.model.MedicationUpdateRequestDto
import com.example.psymed.data.model.MoodStateDto
import com.example.psymed.data.model.MoodStateRequestDto
import com.example.psymed.data.model.PatientProfileDto
import com.example.psymed.data.model.PatientProfileRequestDto
import com.example.psymed.data.model.PatientSummaryDto
import com.example.psymed.data.model.ProfessionalProfileDto
import com.example.psymed.data.model.ProfessionalProfileRequestDto
import com.example.psymed.data.model.SessionCreateRequestDto
import com.example.psymed.data.model.SessionDto
import com.example.psymed.data.model.SessionUpdateRequestDto
import com.example.psymed.data.model.SignInRequestDto
import com.example.psymed.data.model.SignUpRequestDto
import com.example.psymed.data.model.TaskDto
import com.example.psymed.data.model.TaskRequestDto
import com.example.psymed.data.model.UpdatePatientProfileRequestDto
import com.example.psymed.data.model.UserResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PsyMedApi {

    // Authentication
    @POST("authentication/sign-in")
    suspend fun signIn(@Body request: SignInRequestDto): AuthResponseDto

    @POST("authentication/sign-up")
    suspend fun signUp(@Body request: SignUpRequestDto): UserResponseDto

    @GET("accounts/{accountId}")
    suspend fun getAccount(@Path("accountId") accountId: Int): UserResponseDto

    // Professional Profiles
    @POST("professional-profiles")
    suspend fun createProfessionalProfile(@Body request: ProfessionalProfileRequestDto): ProfessionalProfileDto

    @GET("professional-profiles/account/{accountId}")
    suspend fun getProfessionalProfileByAccount(@Path("accountId") accountId: Int): ProfessionalProfileDto

    @GET("professional-profiles/{professionalId}")
    suspend fun getProfessionalProfileById(@Path("professionalId") professionalId: Int): ProfessionalProfileDto

    // Patient Profiles
    @POST("patient-profiles")
    suspend fun createPatientProfile(@Body request: PatientProfileRequestDto): PatientProfileDto

    @GET("patient-profiles/account/{accountId}")
    suspend fun getPatientProfileByAccount(@Path("accountId") accountId: Int): PatientProfileDto

    @GET("patient-profiles/{patientId}")
    suspend fun getPatientProfileById(@Path("patientId") patientId: Int): PatientProfileDto

    @PUT("patient-profiles/{patientId}")
    suspend fun updatePatientProfile(
        @Path("patientId") patientId: Int,
        @Body request: UpdatePatientProfileRequestDto
    ): PatientProfileDto

    @DELETE("patient-profiles/{patientId}")
    suspend fun deletePatientProfile(@Path("patientId") patientId: Int)

    @GET("patient-profiles/professional/{professionalId}")
    suspend fun getPatientsByProfessional(@Path("professionalId") professionalId: Int): List<PatientSummaryDto>

    // Sessions
    @GET("patients/{patientId}/sessions")
    suspend fun getSessionsByPatient(@Path("patientId") patientId: Int): List<SessionDto>

    @POST("professionals/{professionalId}/patients/{patientId}/sessions")
    suspend fun createSession(
        @Path("professionalId") professionalId: Int,
        @Path("patientId") patientId: Int,
        @Body request: SessionCreateRequestDto
    ): SessionDto

    @PUT("professionals/{professionalId}/patients/{patientId}/sessions/{sessionId}")
    suspend fun updateSession(
        @Path("professionalId") professionalId: Int,
        @Path("patientId") patientId: Int,
        @Path("sessionId") sessionId: Int,
        @Body request: SessionUpdateRequestDto
    ): SessionDto

    @DELETE("professionals/{professionalId}/patients/{patientId}/sessions/{sessionId}")
    suspend fun deleteSession(
        @Path("professionalId") professionalId: Int,
        @Path("patientId") patientId: Int,
        @Path("sessionId") sessionId: Int
    )

    // Tasks
    @GET("patients/{patientId}/tasks")
    suspend fun getTasksByPatient(@Path("patientId") patientId: Int): List<TaskDto>

    @GET("sessions/{sessionId}/tasks")
    suspend fun getTasksBySession(@Path("sessionId") sessionId: Int): List<TaskDto>

    @POST("sessions/{sessionId}/tasks")
    suspend fun createTask(
        @Path("sessionId") sessionId: Int,
        @Body request: TaskRequestDto
    ): TaskDto

    @PUT("sessions/{sessionId}/tasks/{taskId}")
    suspend fun updateTask(
        @Path("sessionId") sessionId: Int,
        @Path("taskId") taskId: Int,
        @Body request: TaskRequestDto
    ): TaskDto

    @DELETE("sessions/{sessionId}/tasks/{taskId}")
    suspend fun deleteTask(
        @Path("sessionId") sessionId: Int,
        @Path("taskId") taskId: Int
    )

    @POST("sessions/{sessionId}/tasks/{taskId}/complete")
    suspend fun markTaskComplete(
        @Path("sessionId") sessionId: Int,
        @Path("taskId") taskId: Int
    )

    @POST("sessions/{sessionId}/tasks/{taskId}/incomplete")
    suspend fun markTaskIncomplete(
        @Path("sessionId") sessionId: Int,
        @Path("taskId") taskId: Int
    )

    // Medications
    @GET("pills/patient/{patientId}")
    suspend fun getMedicationsByPatient(@Path("patientId") patientId: Int): List<MedicationDto>

    @POST("pills")
    suspend fun createMedication(@Body request: MedicationRequestDto): MedicationDto

    @PUT("pills/{medicationId}")
    suspend fun updateMedication(
        @Path("medicationId") medicationId: Int,
        @Body request: MedicationUpdateRequestDto
    ): MedicationDto

    @DELETE("pills/{medicationId}")
    suspend fun deleteMedication(@Path("medicationId") medicationId: Int)

    // Mood & Biological Functions
    @GET("patients/{patientId}/mood-states")
    suspend fun getMoodStates(@Path("patientId") patientId: Int): List<MoodStateDto>

    @POST("patients/{patientId}/mood-states")
    suspend fun createMoodState(
        @Path("patientId") patientId: Int,
        @Body request: MoodStateRequestDto
    ): MoodStateDto

    @GET("patients/{patientId}/biological-functions")
    suspend fun getBiologicalFunctions(@Path("patientId") patientId: Int): List<BiologicalFunctionDto>

    @POST("patients/{patientId}/biological-functions")
    suspend fun createBiologicalFunction(
        @Path("patientId") patientId: Int,
        @Body request: BiologicalFunctionRequestDto
    ): BiologicalFunctionDto
}

