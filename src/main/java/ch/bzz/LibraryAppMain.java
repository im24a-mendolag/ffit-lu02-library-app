package ch.bzz;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class LibraryAppMain {

    /**
     * Ein Befehl. run() gibt zurück, ob das Programm weiterlaufen soll
     * (true = weiter, false = beenden).
     */
    interface Command {
        boolean run();
    }

    // Name -> Befehl. LinkedHashMap behält die Einfügereihenfolge.
    private static final Map<String, Command> COMMANDS = new LinkedHashMap<>();

    public static void main(String[] args) {
        registerCommands();

        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();

                Command command = COMMANDS.get(input);
                if (command == null) {
                    System.out.println("'" + input + "' was not recognized as a commad. Write 'help' for a list of all possible commands.");
                } else {
                    running = command.run();
                }
            }
        }
    }

    // Neue Befehle einfach hier mit einer weiteren put(...)-Zeile ergänzen.
    private static void registerCommands() {
        COMMANDS.put("help", () -> {
            System.out.println("Verfügbare Befehle:");
            for (String name : COMMANDS.keySet()) {
                System.out.println("  " + name);
            }
            return true;
        });

        COMMANDS.put("quit", () -> {
            System.out.println("quitted");
            return false;
        });

        COMMANDS.put("d", () -> {
            System.out.println("test");
            return true;
        });
    }
}
