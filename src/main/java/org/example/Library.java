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


    public void registerItem(Item item) {
        items.add(item);
        itemsById.put(item.getId(), item);
        rememberGenreIfPresent(item);
    }

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

    public Optional<User> findUser(String id) {
        return Optional.ofNullable(usersById.get(id));
    }

    public Optional<Item> findItem(String id) {
        return Optional.ofNullable(itemsById.get(id));
    }

    public void enqueueIsbnHold(String isbn) {
        if (!Validation.isValidISBN(isbn)) {
            throw new IllegalArgumentException("ISBN must contain exactly 13 digits.");
        }
        isbnHoldRequests.add(isbn);
    }

    public String pollNextIsbnHold() {
        return isbnHoldRequests.poll();
    }

    public Item peekLatestReturn() {
        return recentReturns.isEmpty() ? null : recentReturns.peek();
    }

    public Set<String> getKnownGenres() {
        return Set.copyOf(cachedGenres);
    }

    private void rememberGenreIfPresent(Item item) {
        if (item instanceof Book book) {
            cachedGenres.add(book.getGenre());
        }
    }

    public void borrow(User user, Item item) {
        if (!item.isBorrowableCopy()) {
            throw new ItemUnavailableException("Copy " + item.getId() + " is not available for loan.");
        }
        if (!user.canBorrow(item)) {
            throw new BorrowLimitExceededException("Borrowing limits block user " + user.getId() + ".");
        }
        user.registerBorrow(item);
        item.setStatus(ItemStatus.BORROWED);
    }

    public void returnItem(User user, Item item) {
        if (!user.getBorrowedItems().contains(item)) {
            throw new InvalidReturnException("User " + user.getId() + " is not holding copy " + item.getId() + ".");
        }
        user.unregisterBorrow(item);
        item.setStatus(ItemStatus.IN_STORE);
        recentReturns.push(item);
    }

    public void markLost(Item item) {
        item.setStatus(ItemStatus.LOST);
    }

    public List<Item> searchItemsRecursive(String keyword) {
        return ItemSearch.searchRecursive(items, keyword);
    }

    public List<Item> searchItemsWithStream(String keyword) {
        return ItemSearch.searchWithStream(items, keyword);
    }


}
