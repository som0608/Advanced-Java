package com.example;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GUI for managing a collection of books.
 * Supports adding, deleting, filtering, and genre management.
 */
public class BookManagerGUI extends JFrame {
    private JTextField titleField;
    private JTextField authorField;
    private JTextField searchField;
    private JComboBox<String> genreInputBox;
    private JComboBox<String> genreFilterBox;
    private JCheckBox favoriteFilter;
    private JButton resetButton;
    private JButton manageGenresButton;

    private BookTableModel tableModel;
    private JTable bookTable;

    private BookDAO dao;
    private List<Book> allBooks;

    /**
     * Constructs the main GUI for the Book Manager application.
     */
    public BookManagerGUI() {
        super("Book Manager");
        dao = new BookDAO();

        // Load genres from XML
        List<String> genres = GenreLoader.loadGenres();
        genres.add(0, "-");

        // Top panel for filtering
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchBooks(); }
            public void removeUpdate(DocumentEvent e) { searchBooks(); }
            public void changedUpdate(DocumentEvent e) { searchBooks(); }
        });

        genreFilterBox = new JComboBox<>(genres.toArray(new String[0]));
        genreFilterBox.addActionListener(e -> searchBooks());

        favoriteFilter = new JCheckBox("Favorites only");
        favoriteFilter.addActionListener(e -> searchBooks());

        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            searchField.setText("");
            genreFilterBox.setSelectedIndex(0);
            favoriteFilter.setSelected(false);
            searchBooks();
        });

        manageGenresButton = new JButton("Manage Genres");
        manageGenresButton.addActionListener(e -> {
            new GenreManagerDialog(this).setVisible(true);
            reloadGenres();
            refreshBookList();
        });

        searchPanel.add(new JLabel("Keyword:"));
        searchPanel.add(searchField);
        searchPanel.add(genreFilterBox);
        searchPanel.add(favoriteFilter);
        searchPanel.add(resetButton);
        searchPanel.add(manageGenresButton);

        // Left panel for input
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        titleField = new JTextField(15);
        authorField = new JTextField(15);
        genreInputBox = new JComboBox<>(genres.subList(1, genres.size()).toArray(new String[0]));

        JButton addButton = new JButton("Add Book");
        addButton.addActionListener(this::addBook);

        inputPanel.add(new JLabel("Title:"));
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Author:"));
        inputPanel.add(authorField);
        inputPanel.add(new JLabel("Genre:"));
        inputPanel.add(genreInputBox);
        inputPanel.add(new JLabel());
        inputPanel.add(addButton);

        // Table for books
        allBooks = dao.getAllBooks();
        tableModel = new BookTableModel(allBooks, dao);
        bookTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(bookTable);

        // Bottom delete button
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(this::deleteSelectedBook);

        // Layout
        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.WEST);
        add(tableScrollPane, BorderLayout.CENTER);
        add(deleteButton, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Adds a new book to the list and database.
     *
     * @param e the action event from the button
     */
    private void addBook(ActionEvent e) {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String genre = (String) genreInputBox.getSelectedItem();
        if (!title.isEmpty() && !author.isEmpty()) {
            dao.addBook(new Book(title, author, genre));
            refreshBookList();
            titleField.setText("");
            authorField.setText("");
            genreInputBox.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(this, "Title and Author are required.");
        }
    }

    /**
     * Deletes the selected book from the table and database.
     *
     * @param e the action event from the button
     */
    private void deleteSelectedBook(ActionEvent e) {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow >= 0) {
            Book book = tableModel.getBookAt(selectedRow);
            dao.deleteBook(book.getId());
            refreshBookList();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.");
        }
    }

    /**
     * Filters the book list based on keyword, genre, and favorite flag.
     */
    private void searchBooks() {
        String keyword = searchField.getText().trim().toLowerCase();
        String genre = (String) genreFilterBox.getSelectedItem();
        if (genre == null) return;

        boolean onlyFav = favoriteFilter.isSelected();

        List<Book> filtered = allBooks.stream()
            .filter(b -> b.getTitle().toLowerCase().contains(keyword)
                      || b.getAuthor().toLowerCase().contains(keyword)
                      || b.getGenre().toLowerCase().contains(keyword))
            .filter(b -> genre.equals("-") || b.getGenre().equals(genre))
            .filter(b -> !onlyFav || b.isFavorite())
            .collect(Collectors.toList());

        tableModel.setBooks(filtered);
    }

    /**
     * Reloads all books from the database and updates the table.
     */
    private void refreshBookList() {
        allBooks = dao.getAllBooks();
        searchBooks();
    }

    /**
     * Reloads genres from the XML file and updates the combo boxes.
     */
    public void reloadGenres() {
        List<String> genres = GenreLoader.loadGenres();

        genreFilterBox.removeAllItems();
        genreFilterBox.addItem("-");
        for (String genre : genres) {
            genreFilterBox.addItem(genre);
        }

        genreInputBox.removeAllItems();
        for (String genre : genres) {
            genreInputBox.addItem(genre);
        }

        genreFilterBox.revalidate();
        genreInputBox.revalidate();
    }
}
