package org.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemSearch {

    private ItemSearch() {
    }

    /**
     * Matches keyword to title  and author fields for eacj type of media
     * @param item the input item
     * @param lowerKeyword the input keyword (to lowerstring to be case insensitive and not sensitive)
     * @return true if the copy shows up in the results
     */
    public static boolean matchesTitleOrAuthorFields(Item item, String lowerKeyword) {
        if (item instanceof Book book) {
            String t = book.getTitle() == null ? "" : book.getTitle().toLowerCase();
            String a = book.getAuthor() == null ? "" : book.getAuthor().toLowerCase();
            return t.contains(lowerKeyword) || a.contains(lowerKeyword);
        }
        if (item instanceof DVD dvd) {
            String t = dvd.getTitle() == null ? "" : dvd.getTitle().toLowerCase();
            String d = dvd.getDirector() == null ? "" : dvd.getDirector().toLowerCase();
            return t.contains(lowerKeyword) || d.contains(lowerKeyword);
        }
        if (item instanceof Magazine magazine) {
            String t = magazine.getTitle() == null ? "" : magazine.getTitle().toLowerCase();
            String p = magazine.getPublisher() == null ? "" : magazine.getPublisher().toLowerCase();
            return t.contains(lowerKeyword) || p.contains(lowerKeyword);
        }
        return false;
    }
}
