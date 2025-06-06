package com.example;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

/**
 * Utility class for loading book genres from an XML file.
 * <p>
 * This class expects a file named {@code genres.xml} located in the {@code resources} folder,
 * containing genre entries in the following format:
 *
 * <pre>{@code
 * <genres>
 *     <genre>Fiction</genre>
 *     <genre>Science</genre>
 *     <genre>Biography</genre>
 * </genres>
 * }</pre>
 */
public class GenreLoader {

    /**
     * Loads genres from the {@code genres.xml} file in the classpath.
     *
     * @return a list of genre names, or an empty list if the file is not found or an error occurs
     */
    public static List<String> loadGenres() {
        List<String> genres = new ArrayList<>();

        try {
            InputStream is = GenreLoader.class.getClassLoader().getResourceAsStream("genres.xml");
            if (is == null) {
                System.err.println("genres.xml not found");
                return genres;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            NodeList genreNodes = doc.getElementsByTagName("genre");
            for (int i = 0; i < genreNodes.getLength(); i++) {
                Element genreElement = (Element) genreNodes.item(i);
                genres.add(genreElement.getTextContent());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return genres;
    }
}
