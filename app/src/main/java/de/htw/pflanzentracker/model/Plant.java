package de.htw.pflanzentracker.model;

import java.time.LocalDate;

// Datenobjekt einer Pflanze
public interface Plant {
    int getId();
    String getName();
    String getLocation();
    int getWateringIntervalDays();
    LocalDate getLastWateredDate();
    LightRequirement getLightRequirement();
}
