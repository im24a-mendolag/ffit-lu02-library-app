package ch.bzz;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
}
