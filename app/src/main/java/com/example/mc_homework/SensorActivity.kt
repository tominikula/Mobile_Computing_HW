package com.example.mc_homework

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class SensorActivity(private val context: Context) : SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var gyroSensor: Sensor? = null

    fun init() {
        Log.i("sensor", "initializing")

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        gyroSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val y = event!!.values[1]
        if(y > 0){
            val myApp = MyApp()
            myApp.showNotification(context, "Gyro working: spinning $y")
        }
    }



    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}