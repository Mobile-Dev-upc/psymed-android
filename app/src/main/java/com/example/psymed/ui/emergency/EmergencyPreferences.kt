package com.example.psymed.ui.emergency

import android.content.Context
import android.content.SharedPreferences

class EmergencyPreferences(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "emergency_prefs"
        private const val KEY_SHAKE_ENABLED = "emergency_shake_enabled"
        private const val KEY_LAST_EMERGENCY_TIMESTAMP = "last_emergency_timestamp"
    }
    
    fun isShakeEnabled(): Boolean {
        return prefs.getBoolean(KEY_SHAKE_ENABLED, true) // Default to enabled
    }
    
    fun setShakeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SHAKE_ENABLED, enabled).apply()
    }
    
    fun getLastEmergencyTimestamp(): Long? {
        val timestamp = prefs.getLong(KEY_LAST_EMERGENCY_TIMESTAMP, -1)
        return if (timestamp == -1L) null else timestamp
    }
    
    fun setLastEmergencyTimestamp(timestamp: Long) {
        prefs.edit().putLong(KEY_LAST_EMERGENCY_TIMESTAMP, timestamp).apply()
    }
    
    fun canSendEmergencyAlert(cooldownMinutes: Int = 5): Boolean {
        val lastTimestamp = getLastEmergencyTimestamp() ?: return true
        val now = System.currentTimeMillis()
        val difference = now - lastTimestamp
        return difference >= cooldownMinutes * 60 * 1000
    }
}

