package ch.bzz;

import ch.bzz.db.BookPersistor;
import ch.bzz.io.BookImporter;
import ch.bzz.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Verantwortung: Konsolen-Ein-/Ausgabe und Verdrahten der Fachklassen (SRP).
 */
public class LibraryAppMain {

    /**
     * Ein Befehl. run(args) gibt zurück, ob das Programm weiterlaufen soll
     * (true = weiter, false = beenden). args enthält den Teil der Eingabe
     * nach dem Befehlsnamen (z.B. den Dateipfad bei importBooks).
     */
    interface Command {
        boolean run(String args);
    }

    private static final Logger log = LoggerFactory.getLogger(LibraryAppMain.class);

    // Name -> Befehl. LinkedHashMap behält die Einfügereihenfolge.
    private static final Map<String, Command> COMMANDS = new LinkedHashMap<>();

    private static final BookPersistor bookPersistor = new BookPersistor();
    private static final BookImporter bookImporter = new BookImporter();

    public static void main(String[] args) {
        registerCommands();
        log.info("Library-Applikation gestartet");

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();

                // Befehlsnamen und Argumente trennen (erstes Wort = Name, Rest = Argumente).
                int spaceIndex = input.indexOf(' ');
                String name = spaceIndex == -1 ? input : input.substring(0, spaceIndex);
                String commandArgs = spaceIndex == -1 ? "" : input.substring(spaceIndex + 1).trim();

                Command command = COMMANDS.get(name);
                if (command == null) {
                    System.out.println("'" + input + "' was not recognized as a commad. Write 'help' for a list of all possible commands.");
                } else {
                    running = command.run(commandArgs);
                }
            }
        }
    }

    // Neue Befehle einfach hier mit einer weiteren put(...)-Zeile ergänzen.
    private static void registerCommands() {
        COMMANDS.put("help", args -> {
            System.out.println("Verfügbare Befehle:");
            for (String name : COMMANDS.keySet()) {
                System.out.println("  " + name);
            }
            return true;
        });

        COMMANDS.put("quit", args -> {
            System.out.println("quitted");
            return false;
        });

        COMMANDS.put("listBooks", args -> {
            listBooks(args);
            return true;
        });

        COMMANDS.put("importBooks", args -> {
            if (args.isEmpty()) {
                System.out.println("Verwendung: importBooks <FILE_PATH>");
            } else {
                importBooks(args);
            }
            return true;
        });
    }

    /**
     * Gibt die Bücher aus. Optional kann ein Limit als Argument übergeben werden
     * (z.B. "listBooks 10"). Ist das Argument keine gültige Zahl, wird der Vorfall
     * geloggt und alle Bücher werden ausgegeben – die Applikation läuft weiter.
     */
    private static void listBooks(String args) {
        int limit = BookPersistor.NO_LIMIT;
        if (!args.isEmpty()) {
            try {
                limit = Integer.parseInt(args.trim());
            } catch (NumberFormatException e) {
                log.warn("Ungültiges Limit für listBooks: '{}'. Es werden alle Bücher ausgegeben.", args);
            }
        }

        for (Book book : bookPersistor.getBooks(limit)) {
            System.out.println(book);
        }
    }

    private static void importBooks(String filePath) {
        List<Book> books = bookImporter.importFromFile(filePath);
        if (books.isEmpty()) {
            System.out.println("Keine Bücher zum Importieren gefunden.");
            return;
        }
        int saved = bookPersistor.saveBooks(books);
        System.out.println(saved + " Bücher importiert.");
    }
}
