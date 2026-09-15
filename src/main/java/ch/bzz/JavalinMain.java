package ch.bzz;

import ch.bzz.db.BookPersistor;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST-API für die Library App (Anforderung 4).
 * Stellt die Logik von listBooks unter GET /books bereit.
 */
public class JavalinMain {

    private static final Logger log = LoggerFactory.getLogger(JavalinMain.class);

    private static final int PORT = 7070;

    private static final BookPersistor bookPersistor = new BookPersistor();

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(PORT);
        log.info("Javalin gestartet auf Port {}", PORT);

        // GET /books?limit=<n> -> Bücherliste als JSON
        app.get("/books", ctx -> {
            int limit = parseLimit(ctx.queryParam("limit"));
            ctx.json(bookPersistor.getAll(limit));
        });
    }

    /**
     * Wandelt den optionalen Query-Parameter limit in eine Zahl um.
     * Fehlt der Parameter oder ist er ungültig, werden alle Bücher zurückgegeben.
     */
    private static int parseLimit(String limitParam) {
        if (limitParam == null || limitParam.isBlank()) {
            return BookPersistor.NO_LIMIT;
        }
        try {
            return Integer.parseInt(limitParam.trim());
        } catch (NumberFormatException e) {
            log.warn("Ungültiges Limit im Query-Param: '{}'. Es werden alle Bücher zurückgegeben.", limitParam);
            return BookPersistor.NO_LIMIT;
        }
    }
}
