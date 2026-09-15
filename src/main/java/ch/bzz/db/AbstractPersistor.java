package ch.bzz.db;

import ch.bzz.Config;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Gemeinsame, generische Basis aller Persistoren (DRY). Der Typ-Parameter {@code T}
 * legt fest, mit welcher Entität eine konkrete Unterklasse arbeitet
 * (z.B. {@code BookPersistor extends AbstractPersistor<Book>}).
 *
 * Kapselt die EntityManagerFactory, das Öffnen von EntityManagern sowie die
 * Transaktionsbehandlung. Implementiert {@link AutoCloseable}, damit ein Persistor
 * in einem try-with-resources verwendet und die Factory sauber geschlossen werden kann.
 */
public abstract class AbstractPersistor<T> implements AutoCloseable {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final EntityManagerFactory emf;

    protected AbstractPersistor() {
        this.emf = Persistence.createEntityManagerFactory("localPU", Config.getProperties());
    }

    /**
     * Speichert die übergebene Entität (bestehende Einträge werden via merge überschrieben).
     */
    public void save(T t) {
        executeTransaction(em -> em.merge(t));
    }

    /**
     * Führt eine lesende Abfrage in einem EntityManager aus und gibt deren Ergebnis zurück.
     */
    protected <R> R executeQuery(Function<EntityManager, R> query) {
        try (EntityManager em = emf.createEntityManager()) {
            return query.apply(em);
        }
    }

    /**
     * Führt eine schreibende Operation in einer Transaktion aus. Bei einem Fehler
     * wird ein Rollback durchgeführt, der Fehler geloggt und die Exception erneut geworfen.
     */
    protected void executeTransaction(Consumer<EntityManager> action) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                action.accept(em);
                em.getTransaction().commit();
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                log.error("Error during transaction:", e);
                throw e;
            }
        }
    }

    @Override
    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            log.info("EntityManagerFactory closed");
        }
    }
}
