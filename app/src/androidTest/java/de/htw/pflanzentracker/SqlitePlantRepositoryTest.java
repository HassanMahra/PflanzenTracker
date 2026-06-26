package de.htw.pflanzentracker;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.util.List;

import de.htw.pflanzentracker.model.LightRequirement;
import de.htw.pflanzentracker.model.Plant;
import de.htw.pflanzentracker.model.PlantImpl;
import de.htw.pflanzentracker.model.PlantRepository;
import de.htw.pflanzentracker.model.SqlitePlantRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class SqlitePlantRepositoryTest {

    private Context context;
    private PlantRepository repository;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.deleteDatabase(SqlitePlantRepository.DB_NAME);
        repository = new SqlitePlantRepository(context);
    }

    @Test
    public void insertAndFindById() {
        Plant p = new PlantImpl(0, "Monstera", "Fenster", 7,
                LocalDate.of(2026, 1, 1), LightRequirement.MEDIUM);

        long id = repository.insert(p);
        Plant loaded = repository.findById((int) id);

        assertNotNull(loaded);
        assertEquals("Monstera", loaded.getName());
        assertEquals(7, loaded.getWateringIntervalDays());
    }

    @Test
    public void findAllReturnsAllPlants() {
        repository.insert(new PlantImpl(0, "Aloe", "Tisch", 10,
                LocalDate.of(2026, 1, 1), LightRequirement.HIGH));
        repository.insert(new PlantImpl(0, "Efeu", "Bad", 5,
                LocalDate.of(2026, 1, 2), LightRequirement.LOW));

        List<Plant> all = repository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    public void updateChangesData() {
        long id = repository.insert(new PlantImpl(0, "Aloe", "Tisch", 10,
                LocalDate.of(2026, 1, 1), LightRequirement.HIGH));

        repository.update(new PlantImpl((int) id, "Aloe Vera", "Fenster", 14,
                LocalDate.of(2026, 1, 3), LightRequirement.HIGH));

        Plant loaded = repository.findById((int) id);
        assertEquals("Aloe Vera", loaded.getName());
        assertEquals(14, loaded.getWateringIntervalDays());
    }

    @Test
    public void deleteRemovesPlant() {
        long id = repository.insert(new PlantImpl(0, "Test", "Ort", 3,
                LocalDate.of(2026, 1, 1), LightRequirement.LOW));

        repository.delete((int) id);

        assertNull(repository.findById((int) id));
    }
}
