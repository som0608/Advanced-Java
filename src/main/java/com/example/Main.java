package com.example;

/**
 * Entry point of the Book Manager application.
 * <p>
 * Launches the GUI using the Swing event dispatch thread.
 */
public class Main {

    /**
     * Main method to launch the Book Manager GUI.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            BookManagerGUI gui = new BookManagerGUI();
            gui.setVisible(true);
        }); 
    }
}
