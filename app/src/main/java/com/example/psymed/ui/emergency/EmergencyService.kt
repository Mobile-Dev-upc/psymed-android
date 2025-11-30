package com.example.psymed.ui.emergency

import android.util.Log
import com.example.psymed.data.api.PsyMedApi
import com.example.psymed.data.model.ProfessionalProfileDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EmergencyService(
    private val api: PsyMedApi
) {
    // SendGrid API Key - REPLACE WITH YOUR ACTUAL API KEY
    private val sendGridApiKey = "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"
    private val sendGridFromEmail = "psymedmovilemovile@gmail.com"
    
    suspend fun sendEmergencyAlert(
        patientId: Int,
        professionalId: Int,
        patientName: String
    ): EmergencyAlertResult = withContext(Dispatchers.IO) {
        // Validate SendGrid API key
        if (sendGridApiKey.isEmpty()) {
            return@withContext EmergencyAlertResult.Error("SendGrid API key not configured.")
        }
        
        try {
            // Step 1: Get professional's email from backend
            Log.d(TAG, "Getting professional profile for ID: $professionalId")
            val professionalProfile: ProfessionalProfileDto = try {
                api.getProfessionalProfileById(professionalId)
            } catch (e: Exception) {
                Log.e(TAG, "Error getting professional profile: ${e.message}")
                return@withContext EmergencyAlertResult.Error(
                    "Could not get professional email address. Please ensure the endpoint GET /api/v1/professional-profiles/{id} exists in your backend."
                )
            }
            
            val professionalEmail = professionalProfile.email
            val professionalName = professionalProfile.fullName
            
            if (professionalEmail.isNullOrEmpty()) {
                return@withContext EmergencyAlertResult.Error("Professional email not found")
            }
            
            val timestamp = Date()
            val formattedDate = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(timestamp)
            
            Log.d(TAG, "Sending emergency email to: $professionalEmail")
            Log.d(TAG, "Sending email via SendGrid API...")
            Log.d(TAG, "SendGrid URL: https://api.sendgrid.com/v3/mail/send")
            
            // Step 2: Send email via SendGrid API
            // Based on SendGrid v3 Mail Send API: https://www.twilio.com/docs/sendgrid/api-reference/mail-send/mail-send
            val sendGridRequest = createSendGridRequest(
                professionalEmail = professionalEmail,
                professionalName = professionalName ?: "Professional",
                patientId = patientId,
                patientName = patientName,
                formattedDate = formattedDate
            )
            
            val client = OkHttpClient()
            val requestBody = sendGridRequest.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://api.sendgrid.com/v3/mail/send")
                .addHeader("Authorization", "Bearer $sendGridApiKey")
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build()
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            
            Log.d(TAG, "SendGrid response status: ${response.code}")
            Log.d(TAG, "SendGrid response body: $responseBody")
            
            when (response.code) {
                202 -> {
                    // SendGrid returns 202 Accepted for successful sends
                    EmergencyAlertResult.Success(
                        "Emergency alert email sent successfully to $professionalName"
                    )
                }
                401 -> {
                    // Parse error response from SendGrid
                    val errorMessage = try {
                        if (responseBody.isNotEmpty()) {
                            val errorJson = org.json.JSONObject(responseBody)
                            val errorsArray = errorJson.optJSONArray("errors")
                            if (errorsArray != null && errorsArray.length() > 0) {
                                val firstError = errorsArray.getJSONObject(0)
                                firstError.optString("message", "SendGrid API key is invalid")
                            } else {
                                "SendGrid API key is invalid"
                            }
                        } else {
                            "SendGrid API key is invalid"
                        }
                    } catch (e: Exception) {
                        "SendGrid API key is invalid"
                    }
                    EmergencyAlertResult.Error("SendGrid authentication failed: $errorMessage. Please check your API key.")
                }
                else -> {
                    // Parse error response from SendGrid
                    val errorMessage = try {
                        if (responseBody.isNotEmpty()) {
                            val errorJson = org.json.JSONObject(responseBody)
                            val errorsArray = errorJson.optJSONArray("errors")
                            if (errorsArray != null && errorsArray.length() > 0) {
                                val firstError = errorsArray.getJSONObject(0)
                                firstError.optString("message", "Failed to send email")
                            } else {
                                "Failed to send email via SendGrid"
                            }
                        } else {
                            "Failed to send email via SendGrid"
                        }
                    } catch (e: Exception) {
                        "Failed to send email via SendGrid: ${response.message}"
                    }
                    EmergencyAlertResult.Error("Error sending email: $errorMessage")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending emergency alert: ${e.message}", e)
            val errorMessage = when {
                e.message?.contains("SocketException") == true ||
                e.message?.contains("Failed host lookup") == true -> {
                    "No se puede conectar al servidor. Verifica tu conexión a internet."
                }
                else -> {
                    "Error: ${e.message ?: "Unknown error"}"
                }
            }
            EmergencyAlertResult.Error(errorMessage)
        }
    }
    
    private fun createSendGridRequest(
        professionalEmail: String,
        professionalName: String,
        patientId: Int,
        patientName: String,
        formattedDate: String
    ): JSONObject {
        val request = JSONObject()
        
        // Personalizations
        val personalization = JSONObject()
        val toArray = org.json.JSONArray()
        val toObject = JSONObject()
        toObject.put("email", professionalEmail)
        toObject.put("name", professionalName)
        toArray.put(toObject)
        personalization.put("to", toArray)
        personalization.put("subject", "🚨 EMERGENCY ALERT: $patientName needs immediate attention")
        
        val personalizationsArray = org.json.JSONArray()
        personalizationsArray.put(personalization)
        request.put("personalizations", personalizationsArray)
        
        // From
        val fromObject = JSONObject()
        fromObject.put("email", sendGridFromEmail)
        fromObject.put("name", "PsyMed Emergency System")
        request.put("from", fromObject)
        
        // Reply to
        val replyToObject = JSONObject()
        replyToObject.put("email", sendGridFromEmail)
        replyToObject.put("name", "PsyMed Support")
        request.put("reply_to", replyToObject)
        
        request.put("subject", "🚨 EMERGENCY ALERT: $patientName needs immediate attention")
        
        // Content
        val contentArray = org.json.JSONArray()
        
        // Plain text content
        val textContent = JSONObject()
        textContent.put("type", "text/plain")
        textContent.put("value", """
            EMERGENCY ALERT

            Patient Name: $patientName
            Patient ID: #$patientId
            Time: $formattedDate

            ⚠️ This is an automated emergency alert.
            The patient has triggered an emergency alert by shaking their phone in the PsyMed mobile app.
            Please contact the patient immediately.

            This alert was automatically generated by the PsyMed emergency system.
            If you believe this is an error, please contact the patient directly.
        """.trimIndent())
        contentArray.put(textContent)
        
        // HTML content
        val htmlContent = JSONObject()
        htmlContent.put("type", "text/html")
        htmlContent.put("value", """
            <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <div style="background-color: #dc3545; color: white; padding: 20px; border-radius: 8px 8px 0 0; text-align: center;">
                            <h1 style="margin: 0; font-size: 24px;">🚨 EMERGENCY ALERT</h1>
                        </div>
                        <div style="background-color: #f8f9fa; padding: 30px; border: 1px solid #dee2e6; border-top: none; border-radius: 0 0 8px 8px;">
                            <h2 style="color: #dc3545; margin-top: 0;">Patient Emergency Alert</h2>
                            <p><strong>Patient Name:</strong> $patientName</p>
                            <p><strong>Patient ID:</strong> #$patientId</p>
                            <p><strong>Time:</strong> $formattedDate</p>
                            <hr style="border: none; border-top: 1px solid #dee2e6; margin: 20px 0;">
                            <p style="background-color: #fff3cd; padding: 15px; border-left: 4px solid #ffc107; border-radius: 4px;">
                                <strong>⚠️ This is an automated emergency alert.</strong><br>
                                The patient has triggered an emergency alert by shaking their phone in the PsyMed mobile app.
                                Please contact the patient immediately.
                            </p>
                            <hr style="border: none; border-top: 1px solid #dee2e6; margin: 20px 0;">
                            <p style="font-size: 12px; color: #6c757d; margin-top: 30px;">
                                This alert was automatically generated by the PsyMed emergency system.<br>
                                If you believe this is an error, please contact the patient directly.
                            </p>
                        </div>
                    </div>
                </body>
            </html>
        """.trimIndent())
        contentArray.put(htmlContent)
        
        request.put("content", contentArray)
        
        return request
    }
    
    companion object {
        private const val TAG = "EmergencyService"
    }
}

sealed class EmergencyAlertResult {
    data class Success(val message: String) : EmergencyAlertResult()
    data class Error(val message: String) : EmergencyAlertResult()
}

