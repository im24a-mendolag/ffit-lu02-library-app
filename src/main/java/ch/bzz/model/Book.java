package ch.bzz.model;

/**
 * Ein Buch, wie es in der Datenbank und in der TSV-Datei abgebildet wird.
 */
public class Book {

    private final int id;
    private final String isbn;
    private final String title;
    private final String author;
    private final int publicationYear;

    public Book(int id, String isbn, String title, String author, int publicationYear) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
    }

    public int getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    @Override
    public String toString() {
        return id + ": " + title + " – " + author + " (" + publicationYear + "), ISBN " + isbn;
    }
}
