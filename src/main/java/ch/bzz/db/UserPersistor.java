package ch.bzz.db;

import ch.bzz.model.User;

/**
 * Verantwortung: Benutzer via JPA/Hibernate speichern (SRP).
 * Die Methode {@code save(User)} wird generisch von {@link AbstractPersistor} geerbt (DRY).
 */
public class UserPersistor extends AbstractPersistor<User> {
    // save(User) wird von AbstractPersistor<User> bereitgestellt.
}
