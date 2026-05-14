package org.example;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
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
        return String.format("U-%04d", nextId++); // User variant of the nextId with "U" prefix
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

    /**
     * Gets list of active borrowed items
     * @return the same list not modified
     */
    public List<Item> getBorrowedItems() {
        return Collections.unmodifiableList(borrowedItems); // Only want to view it so not gonna modify it
    }

    /**
     * Counts how many borrowed books user has
     * @return the number of boroweed books
     */
    public long countBorrowedBooks() {
        return borrowedItems.stream().filter(Book.class::isInstance).count();
    }

    /**
     * used to apply the limits that different types of users can borrow at a time
     * @param candidate the item that the user wants to borrow
     * @return will return true if < to the limit (and so allow the borrowing)
     */
    public boolean canBorrow(Item candidate) {
        if (this instanceof Student) {
            if (candidate instanceof Book) {
                return countBorrowedBooks() < Constants.MAX_BOOKS_STUDENT;
            }
            return true;
        }
        if (this instanceof Teacher) {
            return borrowedItems.size() < Constants.MAX_ITEMS_TEACHER;
        }
        if (this instanceof Admin) {
            return true;
        }
        return false;
    }

    /**
     * adds the input item to borrowedItems
     * @param item the input item
     */
    void registerBorrow(Item item) {
        borrowedItems.add(item);
    }

    /**
     * removes the input item from borrowedItems
     * @param item the input item
     */
    void unregisterBorrow(Item item) {
        borrowedItems.remove(item);
    }
}
