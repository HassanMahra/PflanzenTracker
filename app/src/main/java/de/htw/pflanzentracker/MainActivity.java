package de.htw.pflanzentracker;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import de.htw.pflanzentracker.logic.DefaultLightEvaluator;
import de.htw.pflanzentracker.logic.DefaultWateringStatusCalculator;
import de.htw.pflanzentracker.logic.LightEvaluator;
import de.htw.pflanzentracker.logic.LightRating;
import de.htw.pflanzentracker.logic.WateringStatus;
import de.htw.pflanzentracker.logic.WateringStatusCalculator;
import de.htw.pflanzentracker.model.LightRequirement;
import de.htw.pflanzentracker.model.Plant;
import de.htw.pflanzentracker.model.PlantImpl;
import de.htw.pflanzentracker.model.PlantRepository;
import de.htw.pflanzentracker.model.SqlitePlantRepository;
import de.htw.pflanzentracker.sensor.AndroidLightSensorManager;
import de.htw.pflanzentracker.sensor.LightSensorManager;

public class MainActivity extends AppCompatActivity {

    private PlantRepository repository;
    private WateringStatusCalculator wateringCalculator;
    private LightEvaluator lightEvaluator;
    private LightSensorManager lightSensorManager;

    private LinearLayout plantList;
    private TextView infoText;
    private LinearLayout detailBox;
    private TextView detailTitle;
    private TextView detailText;
    private TextView lightText;

    private Button wateredButton;
    private Button editButton;
    private Button deleteButton;

    private int selectedPlantId = -1;
    private float lastLux = -1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new SqlitePlantRepository(this);
        wateringCalculator = new DefaultWateringStatusCalculator();
        lightEvaluator = new DefaultLightEvaluator();
        lightSensorManager = new AndroidLightSensorManager(this);

        plantList = findViewById(R.id.plantList);
        infoText = findViewById(R.id.infoText);
        detailBox = findViewById(R.id.detailBox);
        detailTitle = findViewById(R.id.detailTitle);
        detailText = findViewById(R.id.detailText);
        lightText = findViewById(R.id.lightText);

        wateredButton = findViewById(R.id.wateredButton);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> showPlantDialog(null));

        wateredButton.setOnClickListener(v -> markSelectedWatered());
        editButton.setOnClickListener(v -> {
            Plant plant = repository.findById(selectedPlantId);
            if (plant != null) showPlantDialog(plant);
        });
        deleteButton.setOnClickListener(v -> deleteSelected());

        showNoSelection();
        refreshPlantList();
    }

    protected void onPause() {
        super.onPause();
        lightSensorManager.stop();
    }

    protected void onResume() {
        super.onResume();
        if (selectedPlantId != -1) startLightMeasurement();
    }

    private void refreshPlantList() {
        plantList.removeAllViews();
        List<Plant> plants = repository.findAll();

        if (plants.isEmpty()) {
            infoText.setText("Noch keine Pflanzen gespeichert. Mit + kannst du eine anlegen.");
            return;
        }
        infoText.setText(plants.size() + " Pflanze gespeichert. Antippen für die Details.");

        for (Plant plant : plants) {
            Button row = new Button(this);
            row.setAllCaps(false);
            row.setText(makeRowText(plant));
            row.setOnClickListener(v -> selectPlant(plant.getId()));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 8, 0, 8);
            plantList.addView(row, params);
        }
    }

    private String makeRowText(Plant plant) {
        WateringStatus status = wateringCalculator.calculate(
                plant.getLastWateredDate(), plant.getWateringIntervalDays(), LocalDate.now());
        return plant.getName() + " - " + plant.getLocation() + "\n" + statusText(status);
    }

    private void selectPlant(int id) {
        selectedPlantId = id;
        Plant plant = repository.findById(id);
        if (plant == null) {
            showNoSelection();
            return;
        }

        detailBox.setVisibility(View.VISIBLE);
        detailTitle.setText(plant.getName());
        updateDetailText(plant);
        startLightMeasurement();
    }

    private void updateDetailText(Plant plant) {
        WateringStatus status = wateringCalculator.calculate(
                plant.getLastWateredDate(), plant.getWateringIntervalDays(), LocalDate.now());

        String light = lightRequirementText(plant.getLightRequirement());
        detailText.setText("Standort: " + plant.getLocation() +
                "\nGiessintervall: alle " + plant.getWateringIntervalDays() + " Tage" +
                "\nZuletzt gegossen: " + plant.getLastWateredDate() +
                "\nStatus: " + statusText(status) +
                "\nLichtbedarf: " + light);

        updateLightText(plant);
    }

    private void startLightMeasurement() {
        if (!lightSensorManager.isAvailable()) {
            lightText.setText("Lichtsensor: auf diesem Gerät nicht verfuegbar");
            return;
        }

        lightSensorManager.stop();
        lightSensorManager.start(lux -> runOnUiThread(() -> {
            lastLux = lux;
            Plant plant = repository.findById(selectedPlantId);
            if (plant != null) updateLightText(plant);
        }));
    }

    private void updateLightText(Plant plant) {
        if (lastLux < 0) {
            lightText.setText("Lichtsensor: warte auf Messwert ...");
            return;
        }
        LightRating rating = lightEvaluator.evaluate(lastLux, plant.getLightRequirement());
        lightText.setText("Aktuell: " + Math.round(lastLux) + " Lux - " + lightRatingText(rating));
    }

    private void showNoSelection() {
        selectedPlantId = -1;
        detailBox.setVisibility(View.GONE);
        lightSensorManager.stop();
    }

    private void markSelectedWatered() {
        Plant p = repository.findById(selectedPlantId);
        if (p == null) return;

        Plant changed = new PlantImpl(p.getId(), p.getName(), p.getLocation(),
                p.getWateringIntervalDays(), LocalDate.now(), p.getLightRequirement());
        repository.update(changed);
        Toast.makeText(this, "Als heute gegossen gespeichert", Toast.LENGTH_SHORT).show();
        refreshPlantList();
        selectPlant(changed.getId());
    }

    private void deleteSelected() {
        Plant p = repository.findById(selectedPlantId);
        if (p == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Pflanze loeschen?")
                .setMessage(p.getName() + " wirklich entfernen?")
                .setPositiveButton("Loeschen", (d, w) -> {
                    repository.delete(p.getId());
                    showNoSelection();
                    refreshPlantList();
                })
                .setNegativeButton("Abbrechen", null)
                .show();
    }

    private void showPlantDialog(Plant existing) {
        boolean editMode = existing != null;

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        form.setPadding(padding, padding, padding, 0);

        EditText nameInput = new EditText(this);
        nameInput.setHint("Name, z.B. Monstera");
        form.addView(nameInput);

        EditText locationInput = new EditText(this);
        locationInput.setHint("Standort, z.B. Fensterbank");
        form.addView(locationInput);

        EditText intervalInput = new EditText(this);
        intervalInput.setHint("Giessintervall in Tagen");
        intervalInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        form.addView(intervalInput);

        EditText dateInput = new EditText(this);
        dateInput.setHint("Zuletzt gegossen: 2026-06-26");
        dateInput.setInputType(InputType.TYPE_CLASS_DATETIME);
        form.addView(dateInput);

        Spinner lightSpinner = new Spinner(this);
        String[] lightValues = {"LOW - Schatten", "MEDIUM - Halbschatten", "HIGH - Sonne"};
        lightSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, lightValues));
        form.addView(lightSpinner);

        if (editMode) {
            nameInput.setText(existing.getName());
            locationInput.setText(existing.getLocation());
            intervalInput.setText(String.valueOf(existing.getWateringIntervalDays()));
            dateInput.setText(existing.getLastWateredDate().toString());
            lightSpinner.setSelection(existing.getLightRequirement().ordinal());
        } else {
            dateInput.setText(LocalDate.now().toString());
            intervalInput.setText("7");
        }

        new AlertDialog.Builder(this)
                .setTitle(editMode ? "Pflanze bearbeiten" : "Neue Pflanze")
                .setView(form)
                .setPositiveButton("Speichern", null)
                .setNegativeButton("Abbrechen", null)
                .create();

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(editMode ? "Pflanze bearbeiten" : "Neue Pflanze")
                .setView(form)
                .setPositiveButton("Speichern", null)
                .setNegativeButton("Abbrechen", null)
                .create();

        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try {
                Plant plant = readPlantFromForm(existing, nameInput, locationInput,
                        intervalInput, dateInput, lightSpinner);
                if (editMode) {
                    repository.update(plant);
                } else {
                    long id = repository.insert(plant);
                    selectedPlantId = (int) id;
                }
                dialog.dismiss();
                refreshPlantList();
                if (editMode) selectPlant(plant.getId());
                else selectPlant(selectedPlantId);
            } catch (IllegalArgumentException ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));

        dialog.show();
    }

    private Plant readPlantFromForm(Plant existing, EditText nameInput, EditText locationInput,
                                   EditText intervalInput, EditText dateInput, Spinner lightSpinner) {
        String name = nameInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();

        if (name.length() == 0) throw new IllegalArgumentException("Name fehlt");
        if (location.length() == 0) location = "ohne Standort";

        int interval;
        try {
            interval = Integer.parseInt(intervalInput.getText().toString().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Giessintervall ist keine Zahl");
        }
        if (interval <= 0) throw new IllegalArgumentException("Intervall muss groesser 0 sein");

        LocalDate lastWatered;
        try {
            lastWatered = LocalDate.parse(dateInput.getText().toString().trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Datum bitte als JJJJ-MM-TT eingeben");
        }

        LightRequirement req = LightRequirement.values()[lightSpinner.getSelectedItemPosition()];
        int id = existing == null ? 0 : existing.getId();
        return new PlantImpl(id, name, location, interval, lastWatered, req);
    }

    private String statusText(WateringStatus status) {
        switch (status.getState()) {
            case DUE_TODAY:
                return "heute giessen";
            case DUE_IN_FUTURE:
                return "in " + status.getDays() + " Tag(en) giessen";
            case OVERDUE:
                return "seit " + status.getDays() + " Tag(en) ueberfaellig";
            default:
                return "unbekannt";
        }
    }

    private String lightRequirementText(LightRequirement req) {
        switch (req) {
            case LOW: return "Schatten / wenig Licht";
            case MEDIUM: return "Halbschatten";
            case HIGH: return "sonnig / viel Licht";
            default: return req.name();
        }
    }

    private String lightRatingText(LightRating rating) {
        switch (rating) {
            case TOO_DARK: return "zu dunkel";
            case OK: return "okay";
            case TOO_BRIGHT: return "zu hell";
            default: return rating.name();
        }
    }
}
