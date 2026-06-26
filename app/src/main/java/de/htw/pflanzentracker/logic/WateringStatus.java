package de.htw.pflanzentracker.logic;

// Ergebnis der Giessstatus-Berechnung
public class WateringStatus {

    public enum State { DUE_TODAY, DUE_IN_FUTURE, OVERDUE }

    private State state;
    private int days; // Tage bis faellig bzw. Tage ueberfaellig

    public WateringStatus(State state, int days) {
        this.state = state;
        this.days = days;
    }

    public State getState() { return state; }
    public int getDays() { return days; }

    public String toString() {
        return state + " (" + days + ")";
    }
}
