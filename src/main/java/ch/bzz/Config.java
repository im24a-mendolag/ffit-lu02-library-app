package ch.bzz;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Einzige Stelle, an der die Konfiguration (config.properties) gelesen wird (DRY).
 * Verantwortung: Zugriff auf Konfigurationswerte bereitstellen (SRP).
 */
public class Config {

    private static final Properties PROPS = new Properties();

    // Liest die Konfiguration einmalig aus config.properties im Root-Verzeichnis.
    static {
        try (InputStream in = new FileInputStream("config.properties")) {
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Konnte config.properties nicht laden. Kopiere config.properties.template zu config.properties.", e);
        }
    }

    private Config() {
        // Utility-Klasse, keine Instanzen.
    }

    public static String get(String key) {
        return PROPS.getProperty(key);
    }

    /**
     * Gibt die Konfiguration als Map zurück, damit sie an
     * {@code Persistence.createEntityManagerFactory(unit, properties)} übergeben werden kann.
     * Enthält u.a. jakarta.persistence.jdbc.url/user/password.
     */
    public static Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();
        for (String key : PROPS.stringPropertyNames()) {
            properties.put(key, PROPS.getProperty(key));
        }
        return properties;
    }
}
