package org.example;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@EqualsAndHashCode
public abstract class Item {
    private String id;
    private String title;
    private ItemStatus status;

    private static int nextId = 1;

    public Item(String id, String title, ItemStatus status) {
        this.id = id;
        this.title = title;
        this.status = status;
    }

    /**
     * Allocates the next Id
     * @return the id formated ex: I-00001
     */
    public static String allocateNextItemId() {
        return String.format("I-%05d", nextId++); // Better than having the usual %04d which is ionly ex: 0001 as Users will have their own prefix "U" and Items "I"
    }

    /**
     * Moves the id gen. forward after a csv
     * @param nextExclusive the lowest value that shouldnt override current id
     */
    public static void seedItemIdSequence(int nextExclusive) {
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
        if (id == null || !id.startsWith("I-")) {
            return 0;
        }
        return Integer.parseInt(id.substring(2));
    }

    /**
     * Chekcs if the copy is borrowable (only when in store)
     * @return true if so
     */
    public boolean isBorrowableCopy() {
        return status == ItemStatus.IN_STORE;
    }
}
