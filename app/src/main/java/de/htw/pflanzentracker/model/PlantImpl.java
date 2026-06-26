package de.htw.pflanzentracker.model;

import java.time.LocalDate;

public class PlantImpl implements Plant {

    private int id;
    private String name;
    private String location;
    private int wateringIntervalDays;
    private LocalDate lastWateredDate;
    private LightRequirement lightRequirement;

    public PlantImpl(int id, String name, String location, int wateringIntervalDays,
                     LocalDate lastWateredDate, LightRequirement lightRequirement) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.wateringIntervalDays = wateringIntervalDays;
        this.lastWateredDate = lastWateredDate;
        this.lightRequirement = lightRequirement;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public int getWateringIntervalDays() { return wateringIntervalDays; }
    public LocalDate getLastWateredDate() { return lastWateredDate; }
    public LightRequirement getLightRequirement() { return lightRequirement; }
}
