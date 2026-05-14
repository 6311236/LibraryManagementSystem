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
}
