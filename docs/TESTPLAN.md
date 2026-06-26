# Testplan PflanzenTracker

## Automatische Tests

| Test | Art | Erwartung |
|---|---|---|
| DefaultWateringStatusCalculatorTest | Unit-Test | Status fuer heute, Zukunft und ueberfaellig stimmt |
| DefaultLightEvaluatorTest | Unit-Test | Lux-Wert wird als zu dunkel, ok oder zu hell bewertet |
| InMemoryPlantRepositoryTest | Unit-Test | Einfuegen und Loeschen funktioniert |
| SqlitePlantRepositoryTest | Android-Test | CRUD gegen lokale SQLite-Datenbank funktioniert |

## Manuelle Abnahme

| Schritt | Erwartetes Ergebnis |
|---|---|
| App starten | Pflanzenliste wird angezeigt |
| Neue Pflanze anlegen | Pflanze erscheint in der Liste |
| App schliessen und neu starten | Pflanze ist noch da, weil SQLite genutzt wird |
| Pflanze antippen | Detailansicht wird angezeigt |
| Heute gegossen klicken | Datum und Status aendern sich |
| Pflanze bearbeiten | neue Werte werden gespeichert |
| Pflanze loeschen | Pflanze verschwindet aus der Liste |
| Detailansicht auf echtem Handy | Lux-Wert wird angezeigt, falls Sensor vorhanden |

## Bekannte Grenzen

- Kein schoenes Design, nur einfache Android-Controls.
- Lichtsensor-Schwellwerte sind grobe Schaetzwerte.
- Keine Benachrichtigungen/Reminder.
- Keine Export-/Cloud-Funktion.
