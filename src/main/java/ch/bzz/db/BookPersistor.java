package ch.bzz.db;

import ch.bzz.Config;
import ch.bzz.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Verantwortung: Bücher via JPA/Hibernate aus der Datenbank lesen und schreiben (SRP).
 * Die Verbindungsdaten kommen aus {@link Config} (DRY).
 */
public class BookPersistor {

    private static final Logger log = LoggerFactory.getLogger(BookPersistor.class);

    /** Kein Limit: alle Bücher zurückgeben. */
    public static final int NO_LIMIT = 0;

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("localPU", Config.getProperties());

    /**
     * Liest Bücher aus der Datenbank. Bei {@code limit > 0} werden höchstens
     * so viele Bücher zurückgegeben.
     */
    public List<Book> getAll(int limit) {
        try (EntityManager em = emf.createEntityManager()) {
            log.debug("Lese Bücher aus der Datenbank (limit={})", limit);
            TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b ORDER BY id", Book.class);
            if (limit > NO_LIMIT) {
                query.setMaxResults(limit);
            }
            return query.getResultList();
        }
    }

    /**
     * Speichert die Bücher in die Datenbank. Bestehende Einträge werden via
     * merge überschrieben.
     *
     * @return Anzahl gespeicherter Bücher.
     */
    public int saveAll(List<Book> books) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                books.forEach(em::merge);
                em.getTransaction().commit();
                log.info("{} Bücher in der Datenbank gespeichert", books.size());
                return books.size();
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                log.error("Bücher konnten nicht in der Datenbank gespeichert werden", e);
                return 0;
            }
        }
    }
}
