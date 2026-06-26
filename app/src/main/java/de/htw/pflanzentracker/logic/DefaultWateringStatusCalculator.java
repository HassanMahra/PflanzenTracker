package de.htw.pflanzentracker.logic;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DefaultWateringStatusCalculator implements WateringStatusCalculator {

    public WateringStatus calculate(LocalDate lastWatered, int intervalDays, LocalDate today) {
        LocalDate due = lastWatered.plusDays(intervalDays);

        if (today.isEqual(due)) {
            return new WateringStatus(WateringStatus.State.DUE_TODAY, 0);
        } else if (today.isBefore(due)) {
            int daysLeft = (int) ChronoUnit.DAYS.between(today, due);
            return new WateringStatus(WateringStatus.State.DUE_IN_FUTURE, daysLeft);
        } else {
            int daysOver = (int) ChronoUnit.DAYS.between(due, today);
            return new WateringStatus(WateringStatus.State.OVERDUE, daysOver);
        }
    }
}
