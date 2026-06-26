# PflanzenTracker

Kleine Android-App fuer das Modul **Mobile Betriebssysteme und Netzwerke**.

Die App verwaltet Zimmerpflanzen lokal auf dem Handy. Man kann Pflanzen anlegen,
bearbeiten, loeschen und als heute gegossen markieren. In der Detailansicht wird
ueber den Lichtsensor ein Lux-Wert angezeigt und grob bewertet.

## Funktionen

- Pflanzenliste anzeigen
- Pflanze anlegen / bearbeiten / loeschen
- Giesstatus berechnen: heute faellig, in Zukunft, ueberfaellig
- lokale Speicherung mit SQLite, funktioniert offline
- Lichtsensor mit `Sensor.TYPE_LIGHT`
- einfache Lichtbewertung nach LOW / MEDIUM / HIGH

## Aufbau

- `model`: Pflanze, Repository, SQLite-Speicherung
- `logic`: Giesstatus und Lichtbewertung, ohne Android-Abhaengigkeit
- `sensor`: Android-Lichtsensor gekapselt hinter Interface
- `MainActivity`: einfache View/Controller-Schicht

## Starten

1. Projekt in Android Studio oeffnen.
2. Gradle Sync ausfuehren.
3. Auf Emulator oder Handy starten.
4. Fuer den Lux-Wert ist ein echtes Handy besser, weil Emulatoren oft keinen Lichtsensor haben.

## Tests

- Lokale Unit-Tests fuer Logik und InMemory-Repository
- Instrumented Test fuer SQLite-Repository

Nicht alles ist perfekt/produktionsreif. Die App ist bewusst einfach gehalten, damit die Architektur und die Kursanforderungen nachvollziehbar bleiben.
