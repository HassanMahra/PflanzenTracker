package de.htw.pflanzentracker.logic;

import java.time.LocalDate;

// Berechnet den Giessstatus.
public interface WateringStatusCalculator {
    WateringStatus calculate(LocalDate lastWatered, int intervalDays, LocalDate today);
}
