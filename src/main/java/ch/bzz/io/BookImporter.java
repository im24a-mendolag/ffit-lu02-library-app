package ch.bzz.io;

import ch.bzz.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Verantwortung: aus einer TSV-Datei Buch-Objekte erzeugen (SRP).
 * Delegiert das reine Lesen an {@link DelimitedFileReader}.
 *
 * Spalten (positionsbasiert): id, isbn, title, author, publication_year.
 */
public class BookImporter {

    private static final Logger log = LoggerFactory.getLogger(BookImporter.class);

    private static final String TSV_DELIMITER = "\t";
    private static final int EXPECTED_COLUMNS = 5;

    private final DelimitedFileReader reader = new DelimitedFileReader(TSV_DELIMITER);

    /**
     * Liest die Bücher aus einer TSV-Datei. Die erste Zeile wird als Kopfzeile übersprungen.
     */
    public List<Book> importFromFile(String filePath) {
        List<Book> books = new ArrayList<>();

        try {
            for (String[] fields : reader.readRows(filePath, true)) {
                Book book = toBook(fields);
                if (book != null) {
                    books.add(book);
                }
            }
            log.info("{} Bücher aus '{}' gelesen", books.size(), filePath);
        } catch (IOException e) {
            // Fehlende/unlesbare Importdatei ist tolerierbar: Applikation läuft weiter.
            log.warn("Importdatei '{}' konnte nicht gelesen werden", filePath, e);
        }

        return books;
    }

    /**
     * Wandelt eine Zeile (Spalten-Array) in ein Buch um oder gibt null zurück,
     * wenn die Zeile ungültig ist.
     */
    private Book toBook(String[] fields) {
        if (fields.length < EXPECTED_COLUMNS) {
            log.warn("Zeile übersprungen (zu wenige Spalten): {}", String.join(TSV_DELIMITER, fields));
            return null;
        }

        try {
            int id = Integer.parseInt(fields[0].trim());
            String isbn = fields[1].trim();
            String title = fields[2].trim();
            String author = fields[3].trim();
            int year = Integer.parseInt(fields[4].trim());
            return new Book(id, isbn, title, author, year);
        } catch (NumberFormatException e) {
            log.warn("Zeile übersprungen (ungültige Zahl): {}", String.join(TSV_DELIMITER, fields));
            return null;
        }
    }
}
