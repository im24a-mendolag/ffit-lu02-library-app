package ch.bzz.db;

import ch.bzz.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Verantwortung: Bücher aus der Datenbank lesen und in die Datenbank schreiben (SRP).
 * Nutzt {@link Database} für die Verbindung (DRY).
 */
public class BookPersistor {

    /**
     * Liest alle Bücher aus der Datenbank.
     */
    public List<Book> getAllBooks() {
        String sql = "SELECT id, isbn, title, author, publication_year FROM books";
        List<Book> books = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String isbn = rs.getString("isbn");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int year = rs.getInt("publication_year");
                books.add(new Book(id, isbn, title, author, year));
            }
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
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
            return stmt.executeBatch().length;
        } catch (SQLException e) {
            System.out.println("Datenbankfehler: " + e.getMessage());
            return 0;
        }
    }
}
