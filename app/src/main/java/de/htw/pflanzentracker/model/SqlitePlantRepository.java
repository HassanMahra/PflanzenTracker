package de.htw.pflanzentracker.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// SQLite-Variante fuer die lokale Offline-Speicherung.
//  reicht fuer meine App und die Tests.
public class SqlitePlantRepository implements PlantRepository {

    public static final String DB_NAME = "plants.db";
    private static final int DB_VERSION = 1;

    private final DbHelper helper;

    public SqlitePlantRepository(Context context) {
        this.helper = new DbHelper(context.getApplicationContext());
    }

    public long insert(Plant plant) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = toValues(plant);
        return db.insert("plant", null, values);
    }

    public Plant findById(int id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("plant", null, "id=?", new String[]{String.valueOf(id)},
                null, null, null);
        try {
            if (c.moveToFirst()) {
                return fromCursor(c);
            }
            return null;
        } finally {
            c.close();
        }
    }

    public List<Plant> findAll() {
        List<Plant> result = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("plant", null, null, null, null, null, "name ASC");
        try {
            while (c.moveToNext()) {
                result.add(fromCursor(c));
            }
        } finally {
            c.close();
        }
        return result;
    }

    public void update(Plant plant) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.update("plant", toValues(plant), "id=?", new String[]{String.valueOf(plant.getId())});
    }

    public void delete(int id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("plant", "id=?", new String[]{String.valueOf(id)});
    }

    private ContentValues toValues(Plant plant) {
        ContentValues values = new ContentValues();
        values.put("name", plant.getName());
        values.put("location", plant.getLocation());
        values.put("interval_days", plant.getWateringIntervalDays());
        values.put("last_watered", plant.getLastWateredDate().toString());
        values.put("light_requirement", plant.getLightRequirement().name());
        return values;
    }

    private Plant fromCursor(Cursor c) {
        int id = c.getInt(c.getColumnIndexOrThrow("id"));
        String name = c.getString(c.getColumnIndexOrThrow("name"));
        String location = c.getString(c.getColumnIndexOrThrow("location"));
        int interval = c.getInt(c.getColumnIndexOrThrow("interval_days"));
        LocalDate lastWatered = LocalDate.parse(c.getString(c.getColumnIndexOrThrow("last_watered")));
        LightRequirement requirement = LightRequirement.valueOf(
                c.getString(c.getColumnIndexOrThrow("light_requirement"))
        );
        return new PlantImpl(id, name, location, interval, lastWatered, requirement);
    }

    private static class DbHelper extends SQLiteOpenHelper {
        DbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE plant (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "location TEXT, " +
                    "interval_days INTEGER NOT NULL, " +
                    "last_watered TEXT NOT NULL, " +
                    "light_requirement TEXT NOT NULL" +
                    ")");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS plant");
            onCreate(db);
        }
    }
}
