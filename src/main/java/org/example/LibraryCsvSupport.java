package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class LibraryCsvSupport {

    private static final String[] ITEM_HEADER = {
            "KIND", "id", "title", "status", "isbn", "author", "genre",
            "director", "durationMinutes", "issueNumber", "publisher"
    };

    private LibraryCsvSupport() {
    }

    /**
     * Takes a CSV line and returns the correct User object
     * @param line a csv line from users.csv
     * @return the User object
     */
    private static User parseUserLine(String line) {
        String[] cols = line.split(",", -1);
        if (cols.length < 3) {
            throw new IllegalArgumentException("Malformed user row: " + line);
        }
        String kind = cols[0].trim().toUpperCase();
        String id = cols[1].trim();
        String name = cols[2].trim();
        return switch (kind) {
            case "STUDENT" -> new Student(id, name);
            case "TEACHER" -> new Teacher(id, name);
            case "ADMIN" -> new Admin(id, name);
            default -> throw new IllegalArgumentException("Unknown user kind: " + kind);
        };
    }

    /**
     * Takes a csv line and returns the correct Item object
     * @param line a CSV line from items.csv
     * @return the Item object
     */
    private static Item parseItemLine(String line) {
        String[] cols = line.split(",", -1);
        if (cols.length < ITEM_HEADER.length) {
            throw new IllegalArgumentException("Malformed item row: " + line);
        }
        String kind = cols[0].trim().toUpperCase();
        String id = cols[1].trim();
        String title = cols[2].trim();
        ItemStatus status = ItemStatus.valueOf(cols[3].trim().toUpperCase());
        return switch (kind) {
            case "BOOK" -> {
                String isbn = cols[4].trim();
                if (!Validation.isValidISBN(isbn)) {
                    throw new IllegalArgumentException("Invalid ISBN in row: " + line);
                }
                yield new Book(id, title, isbn, cols[5].trim(), cols[6].trim(), status);
            }
            case "DVD" -> new DVD(id, title, cols[7].trim(), parseIntSafe(cols[8], "duration"), status);
            case "MAGAZINE" -> new Magazine(id, title, parseIntSafe(cols[9], "issue"), cols[10].trim(), status);
            default -> throw new IllegalArgumentException("Unknown item kind: " + kind);
        };
    }

    /**
     * Converts a String to an int
     * @param text the input string to convert
     * @param field the field name included in error message
     * @return the parsed int
     */
    private static int parseIntSafe(String text, String field) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid integer for " + field + ": " + text, ex);
        }
    }
}
