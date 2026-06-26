package de.htw.pflanzentracker.sensor;

// Kapselt den Lichtsensor (Sensor.TYPE_LIGHT)
public interface LightSensorManager {
    boolean isAvailable();
    void start(LuxListener listener);
    void stop();

    interface LuxListener {
        void onLuxChanged(float lux);
    }
}
