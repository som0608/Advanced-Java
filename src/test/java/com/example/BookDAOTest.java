package com.example;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BookDAO class.
 * This class verifies adding, deleting, setting favorites, and searching books.
 */
public class BookDAOTest {

    private BookDAO dao;

    /** Initializes DAO before each test. */
    @BeforeEach
    public void setUp() {
        dao = new BookDAO();
    }

    /** Cleans up the database after each test. */
    @AfterEach
    public void cleanUp() {
        List<Book> allBooks = dao.getAllBooks();
        for (Book b : allBooks) {
            dao.deleteBook(b.getId());
        }
    }

    /** Tests whether a book can be added and retrieved correctly. */
    @Test
    public void testAddAndGetBook() {
        Book book = new Book("Epic Tales", "Alice Smith", "Fantasy");
        dao.addBook(book);

        List<Book> books = dao.getAllBooks();
        boolean exists = books.stream().anyMatch(b ->
            b.getTitle().equals("Epic Tales") &&
            b.getAuthor().equals("Alice Smith") &&
            b.getGenre().equals("Fantasy")
        );
        assertTrue(exists);
    }

    /** Tests whether a book can be deleted properly. */
    @Test
    public void testDeleteBook() {
        Book book = new Book("Love in Rome", "Jane Doe", "Romance");
        dao.addBook(book);

        Book toDelete = dao.getAllBooks().stream()
            .filter(b -> b.getTitle().equals("Love in Rome"))
            .findFirst()
            .orElse(null);

        assertNotNull(toDelete);
        dao.deleteBook(toDelete.getId());

        List<Book> updated = dao.getAllBooks();
        boolean stillExists = updated.stream().anyMatch(b -> b.getId() == toDelete.getId());
        assertFalse(stillExists);
    }

    /** Tests whether the favorite status can be toggled. */
    @Test
    public void testSetFavorite() {
        Book book = new Book("Mystery Nights", "Bob Brown", "Mystery");
        dao.addBook(book);

        Book target = dao.getAllBooks().stream()
            .filter(b -> b.getTitle().equals("Mystery Nights"))
            .findFirst()
            .orElse(null);

        assertNotNull(target);

        dao.setFavorite(target.getId(), true);
        Book updated = dao.getAllBooks().stream()
            .filter(b -> b.getId() == target.getId())
            .findFirst()
            .orElse(null);

        assertNotNull(updated);
        assertTrue(updated.isFavorite());
    }

    /** Tests search by keyword in title, author, or genre. */
    @Test
    public void testSearchBooksByKeyword() {
        dao.addBook(new Book("Romantic Journey", "Emma Blake", "Romance"));
        dao.addBook(new Book("The Mystery Code", "Alan Poe", "Mystery"));

        List<Book> result = dao.searchBooks("Mystery");
        assertTrue(result.stream().anyMatch(b -> b.getTitle().equals("The Mystery Code")));
    }

    /** Tests search by author's name. */
    @Test
    public void testSearchBooksByAuthor() {
        dao.addBook(new Book("Biography of Tesla", "Nikola Tesla", "Biography"));
        dao.addBook(new Book("Unknown Facts", "Sam Unknown", "Non-fiction"));

        List<Book> result = dao.searchBooks("Tesla");
        assertTrue(result.stream().anyMatch(b -> b.getAuthor().equals("Nikola Tesla")));
    }

    /** Tests search by genre. */
    @Test
    public void testSearchBooksByGenre() {
        dao.addBook(new Book("Unique Fiction Story", "Tom Teller", "Fiction"));
        dao.addBook(new Book("Hard Facts", "Lisa Logic", "Non-fiction"));

        List<Book> result = dao.searchBooks("Fiction");

        boolean found = result.stream().anyMatch(b ->
            b.getTitle().equals("Unique Fiction Story") && b.getGenre().equals("Fiction")
        );

        assertTrue(found);
    }
}
