package com.example;

import org.junit.jupiter.api.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BookDAO and GenreLoader.
 * <p>
 * This class verifies the following:
 * <ul>
 *     <li>Adding, deleting, and updating books</li>
 *     <li>Searching books by title, author, and genre</li>
 *     <li>Handling favorite flags</li>
 *     <li>Loading and saving genre information from/to XML</li>
 * </ul>
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

    /** Tests setting and verifying the favorite flag on a book. */
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

    /** Tests retrieving only favorite books if such a method is available. */
    @Test
    public void testRetrieveOnlyFavorites() {
        Book a = new Book("Book A", "Author A", "Fantasy");
        Book b = new Book("Book B", "Author B", "Fantasy");

        dao.addBook(a);
        dao.addBook(b);

        List<Book> books = dao.getAllBooks();
        dao.setFavorite(books.get(0).getId(), true);

        List<Book> favs = dao.getAllBooks().stream().filter(Book::isFavorite).toList();
        assertEquals(1, favs.size());
        assertTrue(favs.get(0).isFavorite());
    }

    /** Tests invalid input handling. */
    @Test
    public void testAddInvalidBook() {
        Book invalid = new Book("", "", ""); // Empty fields
        dao.addBook(invalid);
        List<Book> result = dao.getAllBooks();

        // This depends on your validation logic; here we assume it's added anyway
        assertFalse(result.isEmpty());
    }

    // ====== GenreLoader Tests ======
    /**
     * Tests saving and reloading genres to/from XML file.
     */
    @Test
    public void testSaveAndLoadGenres() {
        List<String> testGenres = Arrays.asList("Adventure", "Science", "Fantasy");
        GenreLoader.saveGenres(testGenres);

        List<String> loaded = GenreLoader.loadGenres();
        assertEquals(testGenres, loaded);
    }

    /**
     * Tests adding a new genre and verifying it is saved.
     */
    @Test
    public void testAddGenre() {
        List<String> genres = GenreLoader.loadGenres();
        String newGenre = "Philosophy";

        List<String> updated = new ArrayList<>(genres);
        if (!updated.contains(newGenre)) {
            updated.add(newGenre);
        }

        GenreLoader.saveGenres(updated);
        List<String> result = GenreLoader.loadGenres();
        assertTrue(result.contains(newGenre));
    }

    /**
     * Tests removing a genre and confirming it is deleted from the file.
     */
    @Test
    public void testDeleteGenre() {
        List<String> genres = GenreLoader.loadGenres();
        if (genres.isEmpty()) {
            genres = new ArrayList<>(List.of("TestGenre"));
            GenreLoader.saveGenres(genres);
        }

        String toRemove = genres.get(0);
        List<String> updated = new ArrayList<>(genres);
        updated.remove(toRemove);
        GenreLoader.saveGenres(updated);

        List<String> result = GenreLoader.loadGenres();
        assertFalse(result.contains(toRemove));
    }

    /**
     * Tests if genres.xml file exists in the expected location.
     */
    @Test
    public void testGenresFileExists() {
        File file = new File("src/main/resources/genres.xml");
        assertTrue(file.exists(), "genres.xml should exist.");
    }
}
