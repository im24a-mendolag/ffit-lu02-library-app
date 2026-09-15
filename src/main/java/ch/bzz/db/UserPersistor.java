package ch.bzz.db;

import ch.bzz.Config;
import ch.bzz.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Verantwortung: Benutzer via JPA/Hibernate in der Datenbank speichern (SRP).
 * Die Verbindungsdaten kommen aus {@link Config} (DRY).
 */
public class UserPersistor {

    private static final Logger log = LoggerFactory.getLogger(UserPersistor.class);

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("localPU", Config.getProperties());

    /**
     * Speichert einen neuen Benutzer in der Datenbank.
     */
    public void save(User user) {
        try (EntityManager em = emf.createEntityManager()) {
            try {
                em.getTransaction().begin();
                em.persist(user);
                em.getTransaction().commit();
                log.info("Benutzer {} gespeichert", user.getEmail());
            } catch (RuntimeException e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                log.error("Benutzer konnte nicht gespeichert werden", e);
            }
        }
    }
}
