package de.htw.pflanzentracker.logic;

import de.htw.pflanzentracker.model.LightRequirement;

// Bewertet einen Lux-Wert. Ohne Android.
public interface LightEvaluator {
    LightRating evaluate(float lux, LightRequirement requirement);
}
