package com.example;

/**
 * Represents a book with title, author, genre, and favorite status.
 */
public class Book {
    private int id;
    private String title;
    private String author;
    private String genre;
    private boolean favorite;

    /**
     * Constructs a book instance loaded from the database.
     *
     * @param id       the database ID of the book
     * @param title    the title of the book
     * @param author   the author of the book
     * @param genre    the genre of the book
     * @param favorite true if the book is marked as favorite
     */
    public Book(int id, String title, String author, String genre, boolean favorite) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.favorite = favorite;
    }

    /**
     * Constructs a new book for registration (without ID and not favorite).
     *
     * @param title  the title of the book
     * @param author the author of the book
     * @param genre  the genre of the book
     */
    public Book(String title, String author, String genre) {
        this(-1, title, author, genre, false);
    }

    /** @return the database ID of the book */
    public int getId() {
        return id;
    }

    /** @return the title of the book */
    public String getTitle() {
        return title;
    }

    /** @return the author of the book */
    public String getAuthor() {
        return author;
    }

    /** @return the genre of the book */
    public String getGenre() {
        return genre;
    }

    /** @return true if the book is marked as favorite */
    public boolean isFavorite() {
        return favorite;
    }

    /**
     * Sets the ID of the book (usually assigned by the database).
     *
     * @param id the new ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the title of the book.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the author of the book.
     *
     * @param author the new author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Sets the genre of the book.
     *
     * @param genre the new genre
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Sets the favorite flag of the book.
     *
     * @param favorite true to mark as favorite, false to unmark
     */
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
