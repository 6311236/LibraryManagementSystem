package org.example;

public final class Validation {

    /**
     * Validates ISBN as exactly 13 decimal digits
     * @param isbn the input ISBN string
     * @return true if valid
     */
    public static boolean isValidISBN(String isbn) {
        if (isbn == null) {
            return false;
        }
        return isbn.matches("\\d{13}");
    }

    private Validation() {
    }
}
