package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A desktop GUI application for managing a personal book collection.
 * Supports adding, deleting, searching, and marking books as favorites.
 */
public class BookManagerGUI extends JFrame {
    private JTextField titleField;
    private JTextField authorField;
    private JTextField searchField;
    private JComboBox<String> genreInputBox;
    private JComboBox<String> genreFilterBox;
    private JCheckBox favoriteFilter;

    private DefaultListModel<String> listModel;
    private JList<String> bookList;
    private List<Book> currentBooks;

    private BookDAO dao;

    /**
     * Constructs the Book Manager GUI and initializes components and layout.
     */
    public BookManagerGUI() {
        super("Book Manager");
        dao = new BookDAO();

        // --- Load genres ---
        List<String> genres = GenreLoader.loadGenres();
        genres.add(0, "-"); // "-" means "all genres"

        // --- Search Panel ---
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        genreFilterBox = new JComboBox<>(genres.toArray(new String[0]));
        favoriteFilter = new JCheckBox("Show only favorites");

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(this::searchBooks);
        favoriteFilter.addActionListener(e -> searchBooks(null)); // re-search on checkbox toggle

        searchPanel.add(new JLabel("Keyword:"));
        searchPanel.add(searchField);
        searchPanel.add(genreFilterBox);
        searchPanel.add(searchButton);
        searchPanel.add(favoriteFilter);

        // --- Input Panel ---
        titleField = new JTextField(20);
        authorField = new JTextField(20);
        genreInputBox = new JComboBox<>(genres.subList(1, genres.size()).toArray(new String[0]));

        JButton addButton = new JButton("Add Book");
        addButton.addActionListener(this::addBook);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Author:"));
        inputPanel.add(authorField);
        inputPanel.add(new JLabel("Genre:"));
        inputPanel.add(genreInputBox);
        inputPanel.add(new JLabel(""));
        inputPanel.add(addButton);

        // --- Book List and Controls ---
        listModel = new DefaultListModel<>();
        bookList = new JList<>(listModel);
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton deleteButton = new JButton("Delete Selected Book");
        deleteButton.addActionListener(this::deleteSelectedBook);

        JButton favoriteButton = new JButton("Mark/Unmark as Favorite");
        favoriteButton.addActionListener(this::markAsFavorite);

        JPanel controlPanel = new JPanel(new GridLayout(2, 1));
        controlPanel.add(favoriteButton);
        controlPanel.add(deleteButton);

        // --- Layout setup ---
        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.PAGE_START);
        add(inputPanel, BorderLayout.WEST);
        add(new JScrollPane(bookList), BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        refreshBookList();
    }

    /**
     * Handles the Add Book button click. Validates input and adds the book to the database.
     */
    private void addBook(ActionEvent e) {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String genre = (String) genreInputBox.getSelectedItem();

        if (!title.isEmpty() && !author.isEmpty()) {
            Book book = new Book(title, author, genre);
            dao.addBook(book);
            refreshBookList();

            titleField.setText("");
            authorField.setText("");
            genreInputBox.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(this, "Title and Author are required.");
        }
    }

    /**
     * Deletes the currently selected book from the list and database.
     */
    private void deleteSelectedBook(ActionEvent e) {
        int index = bookList.getSelectedIndex();
        if (index >= 0) {
            int bookId = currentBooks.get(index).getId();
            dao.deleteBook(bookId);
            refreshBookList();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.");
        }
    }

    /**
     * Toggles the favorite status of the selected book.
     */
    private void markAsFavorite(ActionEvent e) {
        int index = bookList.getSelectedIndex();
        if (index >= 0) {
            Book selectedBook = currentBooks.get(index);
            dao.setFavorite(selectedBook.getId(), !selectedBook.isFavorite());
            refreshBookList();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to mark/unmark as favorite.");
        }
    }

    /**
     * Performs a search based on the keyword, genre filter, and favorite checkbox.
     */
    private void searchBooks(ActionEvent e) {
        String keyword = searchField.getText().trim().toLowerCase();
        String selectedGenre = (String) genreFilterBox.getSelectedItem();
        boolean onlyFavorites = favoriteFilter.isSelected();

        currentBooks = dao.getAllBooks().stream()
            .filter(b -> b.getTitle().toLowerCase().contains(keyword)
                      || b.getAuthor().toLowerCase().contains(keyword)
                      || b.getGenre().toLowerCase().contains(keyword))
            .filter(b -> selectedGenre.equals("-") || b.getGenre().equals(selectedGenre))
            .filter(b -> !onlyFavorites || b.isFavorite())
            .collect(Collectors.toList());

        updateBookListDisplay();
    }

    /**
     * Reloads the list of books from the database and updates the display.
     */
    private void refreshBookList() {
        currentBooks = dao.getAllBooks();
        updateBookListDisplay();
    }

    /**
     * Updates the GUI list display with the current book list.
     * Adds a star to favorite books and displays all relevant info.
     */
    private void updateBookListDisplay() {
        listModel.clear();
        for (int i = 0; i < currentBooks.size(); i++) {
            Book b = currentBooks.get(i);
            String title = b.isFavorite() ? "⭐ " + b.getTitle() : b.getTitle();
            listModel.addElement((i + 1) + ". " + title + " by " + b.getAuthor() + " (" + b.getGenre() + ")");
        }
    }
}
