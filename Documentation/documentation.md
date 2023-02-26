# Dokumentation
Diese Dokumentation erläutert den Image Meta Manager gemäss der Aufgabenstellung für das OOP2-Abschlussprojekt.

Die Applikation Image Meta Manager dient dazu Bilder und deren Metadaten zu verwalten. 

## Benutzung/Bedienung
Nach dem Starten der Applikation werden falls noch keine Alben vorhanden sind, zwei Beispielalben mit Beispielbildern erstellt.
Damit die Daten persistieren, werden die Daten in JSON-Dateien unter folgenden Pfad: \<Benutzerprofil>/image-meta-manager gespeichert.

### Bibliothek-View
Die Startseite ist die Bibliothek wo alle vorhanden Alben ersichtlich sind.  
In der Bibliothek können neue Alben erstellt werden oder vorhanden Alben bearbeitet werden.  
Durch Klicken auf ein Album wechselt man zum Album-View.

### Album-View
In einem Album werden die verschieden Bilder die Teil eines Albums sind angezeigt.
Zusätzlich werden die folgenden drei Exif/Tiff-Tags zu jedem Bild aufgelistet:
- DateTimeOriginal: das Datum, an welchem das Bild aufgenommen wurde
- Make: Kamerahersteller und
- Model: Kameramodell (Name oder Nummer)

Durch den `Add`-Button kann man ein neues Bild hinzufügen.
Nach dem Hinzufügen wird das Album-View aktualisiert, um alle Bilder anzuzeigen und der Pfad woher das Bild geladen wurde, wird persistend gespeichert. Damit wird das nächste Mal wenn ein Bild hinzugefügt wird automatisch der zuletzt verwendete Pfad zum Hinzufügen eines Bilds verwendet.

Durch Klicken auf die Bilder kann man diese auswählen und anschliessend über den `Delete`-Button löschen oder über den `Edit`-Button bearbeiten (wechsel zum Edit-View).

Über den `Library`-Button kommt man zurück in die Bibliothek (Library).

### Edit-View
Im Edit-View wird das Bild grösser dargestellt und es werden die folgenden Exif/Tiff-Tags angezeigt:
- DateTimeOriginal: das Datum, an welchem das Bild aufgenommen wurde
- Make: Kamerahersteller und
- Model: Kameramodell (Name oder Nummer)
- Software: Name und Nummer des/der Software-Pakt/e das/die verwendet wurden um das Bild aufzunehmen.
- ExifImageWidth: Die Bildbreite in Pixel.
- ExifImageLength: Die Bildhöhe in Pixel.

Über den `Album`-Button kommt man zurück in das Album.
