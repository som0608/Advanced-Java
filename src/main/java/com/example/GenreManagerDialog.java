package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * A dialog for managing book genres.
 * Allows the user to add, delete, and save genres stored in genres.xml.
 */
public class GenreManagerDialog extends JDialog {
    private DefaultListModel<String> genreListModel;
    private JList<String> genreList;

    /**
     * Constructs the genre manager dialog.
     *
     * @param parent the parent JFrame (usually BookManagerGUI)
     */
    public GenreManagerDialog(JFrame parent) {
        super(parent, "Manage Genres", true);
        setLayout(new BorderLayout());

        // Load existing genres into the list model
        genreListModel = new DefaultListModel<>();
        List<String> genres = GenreLoader.loadGenres();
        for (String genre : genres) {
            genreListModel.addElement(genre);
        }

        // Genre list UI component
        genreList = new JList<>(genreListModel);
        JScrollPane scrollPane = new JScrollPane(genreList);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with buttons
        JPanel buttonPanel = new JPanel();

        /**
         * Adds a new genre if it is not empty and doesn't already exist.
         */
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            String newGenre = JOptionPane.showInputDialog(this, "Enter new genre:");
            if (newGenre != null && !newGenre.trim().isEmpty() && !genreListModel.contains(newGenre.trim())) {
                genreListModel.addElement(newGenre.trim());
            }
        });

        /**
         * Deletes the selected genres from the list.
         */
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            List<String> selected = genreList.getSelectedValuesList();
            for (String s : selected) {
                genreListModel.removeElement(s);
            }
        });

        /**
         * Saves the current genres to the genres.xml file.
         */
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            List<String> updatedGenres = new ArrayList<>();
            for (int i = 0; i < genreListModel.getSize(); i++) {
                updatedGenres.add(genreListModel.getElementAt(i));
            }
            GenreLoader.saveGenres(updatedGenres);
            JOptionPane.showMessageDialog(this, "Genres saved successfully.");
        });

        /**
         * Closes the dialog and reloads genres in the parent GUI.
         */
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener((ActionEvent e) -> {
            if (parent instanceof BookManagerGUI) {
                ((BookManagerGUI) parent).reloadGenres();
            }
            dispose();
        });

        // Add buttons to panel
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setSize(400, 300);
        setLocationRelativeTo(parent);
    }
}
