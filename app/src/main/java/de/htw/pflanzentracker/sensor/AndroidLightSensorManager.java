package de.htw.pflanzentracker.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AndroidLightSensorManager implements LightSensorManager, SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private LuxListener listener;

    public AndroidLightSensorManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }
    }

    public boolean isAvailable() {
        return lightSensor != null;
    }

    public void start(LuxListener listener) {
        this.listener = listener;
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stop() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        listener = null;
    }

    // kommt vom SensorEventListener
    public void onSensorChanged(SensorEvent event) {
        if (listener != null) {
            listener.onLuxChanged(event.values[0]); // values[0] = Helligkeit in Lux
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // brauche ich nicht
    }
}
