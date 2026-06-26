package de.htw.pflanzentracker.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Einfache Variante ohne Datenbank
// Daten gehen beim Schliessen der App verloren.
public class InMemoryPlantRepository implements PlantRepository {

    private Map<Integer, Plant> data = new LinkedHashMap<>();
    private int nextId = 1;

    public long insert(Plant plant) {
        int id = nextId++;
        data.put(id, new PlantImpl(id, plant.getName(), plant.getLocation(),
                plant.getWateringIntervalDays(), plant.getLastWateredDate(),
                plant.getLightRequirement()));
        return id;
    }

    public Plant findById(int id) {
        return data.get(id);
    }

    public List<Plant> findAll() {
        return new ArrayList<>(data.values());
    }

    public void update(Plant plant) {
        if (data.containsKey(plant.getId())) {
            data.put(plant.getId(), plant);
        }
    }

    public void delete(int id) {
        data.remove(id);
    }
}
