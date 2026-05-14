package org.example;

public class InvalidReturnException extends LibraryOperationException {
    public InvalidReturnException(String message) {
        super(message);
    }
}
