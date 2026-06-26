package de.htw.pflanzentracker.model;

import java.util.List;

// Speichern und Laden der Pflanzen (CRUD)
public interface PlantRepository {
    long insert(Plant plant);
    Plant findById(int id);
    List<Plant> findAll();
    void update(Plant plant);
    void delete(int id);
}
