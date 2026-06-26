package de.htw.pflanzentracker.logic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.time.LocalDate;

public class DefaultWateringStatusCalculatorTest {

    private WateringStatusCalculator calc = new DefaultWateringStatusCalculator();

    @Test
    public void dueToday() {
        // 1.1. + 7 Tage = 8.1.
        WateringStatus s = calc.calculate(LocalDate.of(2024, 1, 1), 7, LocalDate.of(2024, 1, 8));
        assertEquals(WateringStatus.State.DUE_TODAY, s.getState());
        assertEquals(0, s.getDays());
    }

    @Test
    public void dueInFuture() {
        WateringStatus s = calc.calculate(LocalDate.of(2024, 1, 1), 7, LocalDate.of(2024, 1, 5));
        assertEquals(WateringStatus.State.DUE_IN_FUTURE, s.getState());
        assertEquals(3, s.getDays());
    }

    @Test
    public void overdue() {
        WateringStatus s = calc.calculate(LocalDate.of(2024, 1, 1), 7, LocalDate.of(2024, 1, 10));
        assertEquals(WateringStatus.State.OVERDUE, s.getState());
        assertEquals(2, s.getDays());
    }
}
