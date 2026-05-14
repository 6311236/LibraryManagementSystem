package org.example;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public abstract class User {
    private final String id;
    private String name;
    private final List<Item> borrowedItems;

    private static int nextId = 1;

    protected User(String id, String name) {
        // Reminder not to have nextId set here will put in its own method after**
        this.id = id;
        this.name = name;
        this.borrowedItems = new ArrayList<>();
    }

    /**
     * Allocates the next default identifier
     * @return the nextId
     */
    public static String allocateNextId() {
        return String.format("U-%04d", nextId++);
    }

    /**
     * Moves the id gen. forward after a csv
     * @param nextExclusive the lowest value that should not override current id
     */
    public static void seedUserIdSequence(int nextExclusive) {
        if (nextExclusive > nextId) {
            nextId = nextExclusive;
        }
    }

    /**
     * Reads numeral of ids to sequence
     * @param id the input id
     * @return the numerical suffix and if not will be 0 if not formed properly
     */
    public static int extractSequence(String id) {
        if (id == null || !id.startsWith("U-")) {
            return 0;
        }
        return Integer.parseInt(id.substring(2));
    }
}
