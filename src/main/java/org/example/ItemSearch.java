package org.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemSearch {

    private ItemSearch() {
    }

    /**
     * recursive search and seen key from item, keyword, index matches and ealready seen keys
     * @param items the input items
     * @param lowerKeyword the input keyword
     * @param index the input index
     * @param matches the input matchs
     * @param seenKeys the input already seen keys
     */
    private static void recurse(List<Item> items, String lowerKeyword, int index, List<Item> matches, Set<String> seenKeys) {
        if (index >= items.size()) {
            return;
        }
        Item current = items.get(index);
        if (matchesTitleOrAuthorFields(current, lowerKeyword)) {
            String key = deduplicationKey(current);
            if (!seenKeys.contains(key)) {
                seenKeys.add(key);
                matches.add(current);
            }
        }
        recurse(items, lowerKeyword, index + 1, matches, seenKeys);
    }

    /**
     * search with stream method from input item and keyword input
     * @param items the input items
     * @param keyword the input keyword
     * @return the ordered matches
     */
    public static List<Item> searchWithStream(List<Item> items, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String q = keyword.trim().toLowerCase();
        Set<String> seenKeys = new HashSet<>();
        return items.stream()
                .filter(item -> matchesTitleOrAuthorFields(item, q))
                .filter(item -> seenKeys.add(deduplicationKey(item)))
                .collect(Collectors.toList());
    }

    /**
     * makes duplicates key for muyltiple copies to show up with one search
     * @param item the input item copy
     * @return duplicated
     */
    public static String deduplicationKey(Item item) {
        if (item instanceof Book book) {
            return "BOOK:" + book.getIsbn();
        }
        if (item instanceof DVD dvd) {
            return "DVD:" + dvd.getTitle() + "|" + dvd.getDirector() + "|" + dvd.getDurationMinutes();
        }
        if (item instanceof Magazine magazine) {
            return "MAG:" + magazine.getTitle() + "|" + magazine.getIssueNumber() + "|" + magazine.getPublisher();
        }
        return "ITEM:" + item.getId();
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
