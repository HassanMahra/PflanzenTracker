package de.htw.pflanzentracker.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import java.time.LocalDate;

public class InMemoryPlantRepositoryTest {

    @Test
    public void insertAndDelete() {
        PlantRepository repo = new InMemoryPlantRepository();
        long id = repo.insert(new PlantImpl(0, "Testpflanze", "Regal", 7,
                LocalDate.of(2026, 1, 1), LightRequirement.MEDIUM));

        assertEquals("Testpflanze", repo.findById((int) id).getName());

        repo.delete((int) id);
        assertNull(repo.findById((int) id));
    }
}
