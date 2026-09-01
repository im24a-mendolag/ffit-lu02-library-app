package ch.bzz.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Verantwortung: eine textbasierte Datei zeilenweise lesen und pro Zeile in
 * Spalten (getrennt durch ein Trennzeichen) zerlegen (SRP).
 *
 * Kennt keine Bücher – dadurch für beliebige TSV/CSV-Dateien wiederverwendbar (DRY).
 */
public class DelimitedFileReader {

    private final String delimiter;

    public DelimitedFileReader(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Liest die Datei und gibt die Zeilen als Spalten-Arrays zurück.
     * Leere Zeilen werden übersprungen.
     *
     * @param filePath   Pfad zur Datei.
     * @param skipHeader wenn true, wird die erste (nicht-leere) Zeile als Kopfzeile übersprungen.
     */
    public List<String[]> readRows(String filePath, boolean skipHeader) throws IOException {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (firstLine) {
                    firstLine = false;
                    if (skipHeader) {
                        continue;
                    }
                }
                rows.add(line.split(delimiter));
            }
        }

        return rows;
    }
}
