package ch.bzz.db;

import ch.bzz.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Einzige Stelle, an der eine Datenbankverbindung aufgebaut wird (DRY).
 * Verantwortung: Verbindungen zur Datenbank bereitstellen (SRP).
 */
public class Database {

    private static final String URL = Config.get("DB_URL");
    private static final String USER = Config.get("DB_USER");
    private static final String PASSWORD = Config.get("DB_PASSWORD");

    private Database() {
        // Utility-Klasse, keine Instanzen.
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
