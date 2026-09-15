package ch.bzz.db;

import ch.bzz.model.Book;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * Verantwortung: Bücher via JPA/Hibernate lesen und schreiben (SRP).
 * Erbt EntityManager-, Transaktions- und Ressourcen-Handling von
 * {@link AbstractPersistor} mit fixem generischem Typ {@link Book} (DRY).
 */
public class BookPersistor extends AbstractPersistor<Book> {

    /** Kein Limit: alle Bücher zurückgeben. */
    public static final int NO_LIMIT = 0;

    /**
     * Liest Bücher aus der Datenbank. Bei {@code limit > 0} werden höchstens
     * so viele Bücher zurückgegeben.
     */
    public List<Book> getAll(int limit) {
        return executeQuery(em -> {
            log.debug("Lese Bücher aus der Datenbank (limit={})", limit);
            TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b ORDER BY id", Book.class);
            if (limit > NO_LIMIT) {
                query.setMaxResults(limit);
            }
            return query.getResultList();
        });
    }

    /**
     * Speichert mehrere Bücher in einer Transaktion.
     *
     * @return Anzahl gespeicherter Bücher.
     */
    public int saveAll(List<Book> books) {
        executeTransaction(em -> books.forEach(em::merge));
        log.info("{} Bücher in der Datenbank gespeichert", books.size());
        return books.size();
    }
}
