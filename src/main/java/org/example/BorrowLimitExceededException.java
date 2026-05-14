package org.example;

public class BorrowLimitExceededException extends LibraryOperationException {
    public BorrowLimitExceededException(String message) {
        super(message);
    }
}
