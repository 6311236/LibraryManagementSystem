package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class LibraryCsvSupport {

    private static final String[] ITEM_HEADER = {
            "KIND", "id", "title", "status", "isbn", "author", "genre",
            "director", "durationMinutes", "issueNumber", "publisher"
    };

    private LibraryCsvSupport() {
    }

    /**
     * Reads a list of users from a csv file from path
     * @param path the input path to the user csv file
     * @return a list of users parsed from the file
     * @throws IOException if the file cant be read
     */
    public static List<User> readUsers(Path path) throws IOException {
        List<User> users = new ArrayList<>();
        if (!Files.exists(path)) {
            return users;
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean headerConsumed = false;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                if (!headerConsumed) {
                    headerConsumed = true;
                    if (line.toUpperCase().startsWith("KIND")) {
                        continue;
                    }
                }
                users.add(parseUserLine(line));
            }
        }
        int maxSeq = users.stream().mapToInt(u -> User.extractSequence(u.getId())).max().orElse(0);
        User.seedUserIdSequence(maxSeq + 1);
        return users;
    }

    /**
     * Reads a list of items from a csv file from path
     * @param path the input path to the item csv file
     * @return a list of items parsed from the file
     * @throws IOException if the file cant be read
     */
    public static List<Item> readItems(Path path) throws IOException {
        List<Item> items = new ArrayList<>();
        if (!Files.exists(path)) {
            return items;
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean headerConsumed = false;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }
                if (!headerConsumed) {
                    headerConsumed = true;
                    if (line.toUpperCase().startsWith("KIND")) {
                        continue;
                    }
                }
                items.add(parseItemLine(line));
            }
        }
        int maxSeq = items.stream().mapToInt(i -> Item.extractSequence(i.getId())).max().orElse(0);
        Item.seedItemIdSequence(maxSeq + 1);
        return items;
    }

    /**
     * Writes a list of users to a csv file from a given path
     * @param path the inp[ut path where the csv file will be written
     * @param users the input list of users to write
     * @throws IOException if the file cannot be written
     */
    public static void writeUsers(Path path, List<User> users) throws IOException {
        Files.createDirectories(path.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("KIND,id,name");
            writer.newLine();
            for (User user : users) {
                writer.write(describeUserKind(user));
                writer.write(',');
                writer.write(escape(user.getId()));
                writer.write(',');
                writer.write(escape(user.getName()));
                writer.newLine();
            }
        }
    }

    /**
     * Writes a list of items to a csv file from a given path
     * @param path the input path where the csv file will be written
     * @param items the input list of items to write
     * @throws IOException if the file cannot be written
     */
    public static void writeItems(Path path, List<Item> items) throws IOException {
        Files.createDirectories(path.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(String.join(",", ITEM_HEADER));
            writer.newLine();
            for (Item item : items) {
                writer.write(serializeItem(item));
                writer.newLine();
            }
        }
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

    /**
     * Returns a string label describing what kind of user this is
     * @param user the user to check
     * @return a string representing the users type
     */
    private static String describeUserKind(User user) {
        if (user instanceof Student) {
            return "STUDENT";
        }
        if (user instanceof Teacher) {
            return "TEACHER";
        }
        if (user instanceof Admin) {
            return "ADMIN";
        }
        return "USER";
    }

    /**
     * Converts an Item into a single CSV row string
     * @param item the item to serialize (must be a book, dvd, or magazine)
     * @return a comma separated string for the item
     */
    private static String serializeItem(Item item) {
        String[] row = new String[ITEM_HEADER.length];
        Arrays.fill(row, "");
        row[1] = escape(item.getId());
        row[2] = escape(item.getTitle());
        row[3] = item.getStatus().name();
        if (item instanceof Book book) {
            row[0] = "BOOK";
            row[4] = escape(book.getIsbn());
            row[5] = escape(book.getAuthor());
            row[6] = escape(book.getGenre());
        } else if (item instanceof DVD dvd) {
            row[0] = "DVD";
            row[7] = escape(dvd.getDirector());
            row[8] = Integer.toString(dvd.getDurationMinutes());
        } else if (item instanceof Magazine magazine) {
            row[0] = "MAGAZINE";
            row[9] = Integer.toString(magazine.getIssueNumber());
            row[10] = escape(magazine.getPublisher());
        } else {
            throw new IllegalArgumentException("Unsupported item type: " + item.getClass());
        }
        return String.join(",", row);
    }

    /**
     * Escapes a string value so its safe to use in a CSV file
     * @param value the string to escape
     * @return the escaped string or if not "" if null
     */
    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        boolean needsQuotes = value.contains(",") || value.contains("\"") || value.contains("\n");
        String sanitized = value.replace("\"", "\"\"");
        if (needsQuotes) {
            return "\"" + sanitized + "\"";
        }
        return sanitized;
    }
}
