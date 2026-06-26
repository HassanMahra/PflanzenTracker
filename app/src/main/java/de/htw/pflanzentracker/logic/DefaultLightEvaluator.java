package de.htw.pflanzentracker.logic;

import de.htw.pflanzentracker.model.LightRequirement;

public class DefaultLightEvaluator implements LightEvaluator {

    // Schwellwerte in Lux muss ich noch praktisch testen / kalibrieren
    public LightRating evaluate(float lux, LightRequirement requirement) {
        float min;
        float max;
        switch (requirement) {
            case LOW:    min = 50;   max = 2000;  break;
            case MEDIUM: min = 500;  max = 10000; break;
            case HIGH:   min = 2000; max = 50000; break;
            default:     min = 0;    max = Float.MAX_VALUE;
        }
        if (lux < min) return LightRating.TOO_DARK;
        if (lux > max) return LightRating.TOO_BRIGHT;
        return LightRating.OK;
    }
}
