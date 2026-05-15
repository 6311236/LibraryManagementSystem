package org.example;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Library implements Reportable {

    private final List<User> users;
    private final List<Item> items;
    private final Map<String, User> usersById;
    private final Map<String, Item> itemsById;
    private final Stack<Item> recentReturns;
    private final Queue<String> isbnHoldRequests;
    private final Set<String> cachedGenres;

    public Library() {
        this.users = new ArrayList<>();
        this.items = new ArrayList<>();
        this.usersById = new HashMap<>();
        this.itemsById = new HashMap<>();
        this.recentReturns = new Stack<>();
        this.isbnHoldRequests = new LinkedList<>();
        this.cachedGenres = new HashSet<>();
    }

    public void registerUser(User user) {
        users.add(user);
        usersById.put(user.getId(), user);
    }

    // registerItem but first need to remember gwnnre before implementing this here

    public Book addBookCopy(String title, String isbn, String author, String genre) {
        if (!Validation.isValidISBN(isbn)) {
            throw new IllegalArgumentException("ISBN must contain exactly 13 digits.");
        }
        Book book = new Book(Item.allocateNextItemId(), title, isbn, author, genre, ItemStatus.IN_STORE);
        items.add(book);
        itemsById.put(book.getId(), book);
        cachedGenres.add(genre);
        return book;
    }

    public DVD addDvdCopy(String title, String director, int durationMinutes) {
        DVD dvd = new DVD(Item.allocateNextItemId(), title, director, durationMinutes, ItemStatus.IN_STORE);
        items.add(dvd);
        itemsById.put(dvd.getId(), dvd);
        return dvd;
    }

    public Magazine addMagazineCopy(String title, int issueNumber, String publisher) {
        Magazine magazine = new Magazine(Item.allocateNextItemId(), title, issueNumber, publisher,
                ItemStatus.IN_STORE);
        items.add(magazine);
        itemsById.put(magazine.getId(), magazine);
        return magazine;
    }
}
