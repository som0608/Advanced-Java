package com.example;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model for managing and displaying {@link Book} data in a JTable.
 * <p>
 * This model provides editable columns and synchronizes changes with the database
 * through the provided {@link BookDAO}.
 */
public class BookTableModel extends AbstractTableModel {

    private final String[] columnNames = {"Title", "Author", "Genre", "Favorite"};
    private List<Book> books;
    private final BookDAO dao;

    /**
     * Constructs a BookTableModel with an initial list of books and a DAO.
     *
     * @param books Initial list of {@link Book} objects to be displayed.
     * @param dao   DAO used to persist updates to the database.
     */
    public BookTableModel(List<Book> books, BookDAO dao) {
        this.books = new ArrayList<>(books);
        this.dao = dao;
    }

    /**
     * Updates the internal list of books and notifies the table to refresh its data.
     *
     * @param books New list of books.
     */
    public void setBooks(List<Book> books) {
        this.books = new ArrayList<>(books);
        fireTableDataChanged();
    }

    /**
     * Returns the {@link Book} object at the specified row.
     *
     * @param rowIndex The row index.
     * @return The corresponding {@link Book}.
     */
    public Book getBookAt(int rowIndex) {
        return books.get(rowIndex);
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return Number of books in the list.
     */
    @Override
    public int getRowCount() {
        return books == null ? 0 : books.size();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return Number of columns (always 4).
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Returns the name of the column at the specified index.
     *
     * @param column The column index.
     * @return The column name.
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * Returns the value at the specified row and column.
     *
     * @param rowIndex    Row index.
     * @param columnIndex Column index.
     * @return The value for the cell.
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book book = books.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> book.getTitle();
            case 1 -> book.getAuthor();
            case 2 -> book.getGenre();
            case 3 -> book.isFavorite();
            default -> null;
        };
    }

    /**
     * Specifies that all cells in the table are editable.
     *
     * @param rowIndex    The row index.
     * @param columnIndex The column index.
     * @return true, since all cells are editable.
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    /**
     * Updates the value in the specified cell and synchronizes the change with the database.
     *
     * @param aValue      The new value.
     * @param rowIndex    The row index.
     * @param columnIndex The column index.
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Book book = books.get(rowIndex);

        switch (columnIndex) {
            case 0 -> book.setTitle(aValue.toString());
            case 1 -> book.setAuthor(aValue.toString());
            case 2 -> book.setGenre(aValue.toString());
            case 3 -> book.setFavorite((Boolean) aValue);
        }

        dao.updateBook(book);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    /**
     * Returns the class of data in each column, needed for proper rendering (e.g., checkbox for Boolean).
     *
     * @param columnIndex Column index.
     * @return Class of the column.
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return (columnIndex == 3) ? Boolean.class : String.class;
    }
}
