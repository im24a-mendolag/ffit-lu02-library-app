package ch.bzz.db;

import ch.bzz.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Verantwortung: Bücher aus der Datenbank lesen und in die Datenbank schreiben (SRP).
 * Nutzt {@link Database} für die Verbindung (DRY).
 */
public class BookPersistor {

    private static final Logger log = LoggerFactory.getLogger(BookPersistor.class);

    /** Kein Limit: alle Bücher zurückgeben. */
    public static final int NO_LIMIT = 0;

    /**
     * Liest alle Bücher aus der Datenbank.
     */
    public List<Book> getAllBooks() {
        return getBooks(NO_LIMIT);
    }

    /**
     * Liest Bücher aus der Datenbank. Bei {@code limit > 0} werden höchstens
     * so viele Bücher zurückgegeben.
     */
    public List<Book> getBooks(int limit) {
        String sql = "SELECT id, isbn, title, author, publication_year FROM books ORDER BY id";
        if (limit > NO_LIMIT) {
            sql += " LIMIT ?";
        }
        List<Book> books = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (limit > NO_LIMIT) {
                stmt.setInt(1, limit);
            }
            log.debug("Lese Bücher aus der Datenbank (limit={})", limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String isbn = rs.getString("isbn");
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    int year = rs.getInt("publication_year");
                    books.add(new Book(id, isbn, title, author, year));
                }
            }
        } catch (SQLException e) {
            log.error("Bücher konnten nicht aus der Datenbank gelesen werden", e);
        }

        return books;
    }

    /**
     * Speichert die Bücher in die Datenbank. Bestehende Einträge mit derselben id
     * werden überschrieben (Upsert), damit auch Korrekturen eingelesen werden können.
     *
     * @return Anzahl gespeicherter Bücher.
     */
    public int saveBooks(List<Book> books) {
        String sql = "INSERT INTO books (id, isbn, title, author, publication_year) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "ON CONFLICT (id) DO UPDATE SET "
                + "isbn = EXCLUDED.isbn, "
                + "title = EXCLUDED.title, "
                + "author = EXCLUDED.author, "
                + "publication_year = EXCLUDED.publication_year";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Book book : books) {
                stmt.setInt(1, book.getId());
                stmt.setString(2, book.getIsbn());
                stmt.setString(3, book.getTitle());
                stmt.setString(4, book.getAuthor());
                stmt.setInt(5, book.getPublicationYear());
                stmt.addBatch();
            }
            int saved = stmt.executeBatch().length;
            log.info("{} Bücher in der Datenbank gespeichert", saved);
            return saved;
        } catch (SQLException e) {
            log.error("Bücher konnten nicht in der Datenbank gespeichert werden", e);
            return 0;
        }
    }
}
