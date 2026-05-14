package org.example;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class Book extends Item{
    private String isbn;
    private String author;
    private String genre;

    public Book(String id, String title, String isbn, String author, String genre, ItemStatus status) {
        super(id, title, status);
        this.isbn = isbn;
        this.author = author;
        this.genre = genre;
    }

    /**
     * Validates if the ISBN is correct before accepting new value (will throw exception when invalid)
     * @param isbn the input isbn
     */
    public void setIsbnValidated(String isbn) {
        if (!Validation.isValidISBN(isbn)) {
            throw new IllegalArgumentException("ISBN must be 13 digits.");
        }
        this.isbn = isbn;
    }
}
