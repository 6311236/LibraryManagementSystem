package org.example;

import java.util.Comparator;

public final class ItemSortStrategies {

    private ItemSortStrategies() {
    }

    /**
     * orders items by title and then by id
     * @return the comparator instance
     */
    public static Comparator<Item> byTitleThenId() {
        return Comparator.comparing((Item i) -> i.getTitle().toLowerCase())
                .thenComparing(Item::getId);
    }

    /**
     * orders item by the status and then title
     * @return the comparator instance
     */
    public static Comparator<Item> byStatusThenTitle() {
        return Comparator.comparing(Item::getStatus)
                .thenComparing((Item i) -> i.getTitle().toLowerCase());
    }
}
