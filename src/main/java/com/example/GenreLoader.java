package com.example;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for loading and saving genres from/to genres.xml.
 */
public class GenreLoader {
    private static final String GENRE_FILE = "src/main/resources/genres.xml";

    /**
     * Loads genres from genres.xml file.
     *
     * @return list of genre names
     */
    public static List<String> loadGenres() {
        List<String> genres = new ArrayList<>();
        try {
            File file = new File(GENRE_FILE);
            if (!file.exists()) return genres;

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            NodeList nodes = doc.getElementsByTagName("genre");
            for (int i = 0; i < nodes.getLength(); i++) {
                genres.add(nodes.item(i).getTextContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return genres;
    }

    /**
     * Saves the given list of genres into genres.xml.
     *
     * @param genres list of genres to save
     */
    public static void saveGenres(List<String> genres) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("genres");
            doc.appendChild(root);

            for (String genre : genres) {
                Element genreElement = doc.createElement("genre");
                genreElement.setTextContent(genre);
                root.appendChild(genreElement);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            File file = new File(GENRE_FILE);
            transformer.transform(new DOMSource(doc), new StreamResult(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
