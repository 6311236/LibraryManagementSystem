package org.example;

public class ItemUnavailableException extends LibraryOperationException {
    public ItemUnavailableException(String message) {
        super(message);
    }
}
