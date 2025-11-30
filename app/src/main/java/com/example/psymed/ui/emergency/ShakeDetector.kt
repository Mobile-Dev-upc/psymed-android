package com.example.psymed.ui.emergency

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class ShakeDetector(
    private val context: Context,
    private val onShakeDetected: () -> Unit
) : SensorEventListener {
    
    private val sensorManager: SensorManager = 
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    // Shake detection parameters (matching Flutter app)
    private val shakeThresholdGravity = 2.7f
    private val shakeSlopTimeMS = 500L
    private var lastShakeTime = 0L
    
    private var isListening = false
    
    fun startListening() {
        if (isListening) return
        
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            isListening = true
            Log.d(TAG, "✅ Shake detector initialized and listening...")
        } ?: Log.e(TAG, "❌ Accelerometer not available")
    }
    
    fun stopListening() {
        if (!isListening) return
        
        sensorManager.unregisterListener(this)
        isListening = false
        Log.d(TAG, "Shake detector stopped")
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return
        
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        
        val gX = x / SensorManager.GRAVITY_EARTH
        val gY = y / SensorManager.GRAVITY_EARTH
        val gZ = z / SensorManager.GRAVITY_EARTH
        
        // Calculate g-force
        val gForce = Math.sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()
        
        if (gForce > shakeThresholdGravity) {
            val currentTime = System.currentTimeMillis()
            
            // Check cooldown period
            if (currentTime - lastShakeTime > shakeSlopTimeMS) {
                lastShakeTime = currentTime
                Log.d(TAG, "🔔 SHAKE DETECTED! gForce: $gForce")
                onShakeDetected()
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for shake detection
    }
    
    companion object {
        private const val TAG = "ShakeDetector"
    }
}

