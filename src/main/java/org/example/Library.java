package org.example;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * registers user
     * @param user the user to regisyer
     */
    public void registerUser(User user) {
        users.add(user);
        usersById.put(user.getId(), user);
    }

    // registerItem but first need to remember gwnnre before implementing this here

    /**
     *registers item
     * @param item the item to register
     */
    public void registerItem(Item item) {
        items.add(item);
        itemsById.put(item.getId(), item);
        rememberGenreIfPresent(item);
    }

    /**
     * adds a book to the catalog
     * @param title the book title
     * @param isbn the 13 digit isbn of the book
     * @param author the author name of the book
     * @param genre the genre label of the book
     * @return the created instance
     */
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

    /**
     * adds a dvd copy to the catalog
     * @param title the dvd title
     * @param director the name of the dir
     * @param durationMinutes the time of dvd
     * @return
     */
    public DVD addDvdCopy(String title, String director, int durationMinutes) {
        DVD dvd = new DVD(Item.allocateNextItemId(), title, director, durationMinutes, ItemStatus.IN_STORE);
        items.add(dvd);
        itemsById.put(dvd.getId(), dvd);
        return dvd;
    }

    /**
     * adds a magazine copy to catalog
     * @param title the title of the magazine
     * @param issueNumber the index of it
     * @param publisher the label of the publisher
     * @return the instance created
     */
    public Magazine addMagazineCopy(String title, int issueNumber, String publisher) {
        Magazine magazine = new Magazine(Item.allocateNextItemId(), title, issueNumber, publisher,
                ItemStatus.IN_STORE);
        items.add(magazine);
        itemsById.put(magazine.getId(), magazine);
        return magazine;
    }

    /**
     * finds user based on hos id
     * @param id the id of the user
     * @return
     */
    public Optional<User> findUser(String id) {
        return Optional.ofNullable(usersById.get(id));
    }

    /**
     * find item by id
     * @param id the id to find the item
     * @return the item
     */
    public Optional<Item> findItem(String id) {
        return Optional.ofNullable(itemsById.get(id));
    }

    /**
     *
     * @param isbn
     */
    public void enqueueIsbnHold(String isbn) {
        if (!Validation.isValidISBN(isbn)) {
            throw new IllegalArgumentException("ISBN must contain exactly 13 digits.");
        }
        isbnHoldRequests.add(isbn);
    }

    /**
     * removes longest waiting ISBN request
     * @return the isbn or null when its empty
     */
    public String pollNextIsbnHold() {
        return isbnHoldRequests.poll();
    }

    /**
     * peeks the most recent returned copy without removing
     * @return latest returned copy and null if none
     */
    public Item peekLatestReturn() {
        return recentReturns.isEmpty() ? null : recentReturns.peek();
    }

    /**
     * gets the genre currently shown in catalog
     * @return view of the gnere
     */
    public Set<String> getKnownGenres() {
        return Set.copyOf(cachedGenres);
    }

    private void rememberGenreIfPresent(Item item) {
        if (item instanceof Book book) {
            cachedGenres.add(book.getGenre());
        }
    }

    /**
     * tries to borrow copy to user while keeping in mind the borrowing rules
     * @param user the user wanting to borrow a copy
     * @param item the copy
     */
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

    /**
     * returns a borrowed copy to the shelf
     * @param user the user returning the copy
     * @param item the copy being returned
     */
    public void returnItem(User user, Item item) {
        if (!user.getBorrowedItems().contains(item)) {
            throw new InvalidReturnException("User " + user.getId() + " is not holding copy " + item.getId() + ".");
        }
        user.unregisterBorrow(item);
        item.setStatus(ItemStatus.IN_STORE);
        recentReturns.push(item);
    }

    /**
     * marks a copy as lost so it cant be borrowed
     * @param item the copy
     */
    public void markLost(Item item) {
        item.setStatus(ItemStatus.LOST);
    }

    /**
     * searches items recursively without any duplicated
     * @param keyword the input keyword to search
     * @return matched copies found
     */
    public List<Item> searchItemsRecursive(String keyword) {
        return ItemSearch.searchRecursive(items, keyword);
    }

    /**
     * search items with stream without any duplicates
     * @param keyword input keyword to search
     * @return matched copies found
     */
    public List<Item> searchItemsWithStream(String keyword) {
        return ItemSearch.searchWithStream(items, keyword);
    }

    /**
     * sort users
     * @param comparator the ordering strategy
     */
    public void sortUsers(Comparator<User> comparator) {
        users.sort(comparator);
    }

    /**
     * sort items
     * @param comparator the ordering startegy
     */
    public void sortItems(Comparator<Item> comparator) {
        items.sort(comparator);
    }

    /**
     * reload the users and items from the default csv. resources
     * @throws IOException
     */
    public void loadFromCsvDefaults() throws IOException {
        Path userPath = Paths.get(Constants.USERS_CSV_PATH);
        Path itemPath = Paths.get(Constants.ITEMS_CSV_PATH);
        users.clear();
        items.clear();
        usersById.clear();
        itemsById.clear();
        cachedGenres.clear();
        recentReturns.clear();
        isbnHoldRequests.clear();
        users.addAll(LibraryCsvSupport.readUsers(userPath));
        items.addAll(LibraryCsvSupport.readItems(itemPath));
        users.forEach(u -> usersById.put(u.getId(), u));
        items.forEach(i -> {
            itemsById.put(i.getId(), i);
            rememberGenreIfPresent(i);
        });
    }

    /**
     * @throws IOException if file cannot be written
     */
    public void backupToCsvDefaults() throws IOException {
        Path userPath = Paths.get(Constants.USERS_CSV_PATH);
        Path itemPath = Paths.get(Constants.ITEMS_CSV_PATH);
        LibraryCsvSupport.writeUsers(userPath, users);
        LibraryCsvSupport.writeItems(itemPath, items);
    }

    @Override
    public String generateInventoryReport() {
        Map<ItemStatus, List<Item>> grouped = items.stream()
                .collect(Collectors.groupingBy(Item::getStatus, () -> new EnumMap<>(ItemStatus.class),
                        Collectors.toList()));
        StringBuilder builder = new StringBuilder();
        for (ItemStatus status : ItemStatus.values()) {
            builder.append("== ").append(status).append(" ==").append(System.lineSeparator());
            List<Item> bucket = grouped.getOrDefault(status, List.of());
            if (bucket.isEmpty()) {
                builder.append("(none)").append(System.lineSeparator());
                continue;
            }
            for (Item item : bucket) {
                builder.append(item).append(System.lineSeparator());
            }
        }
        return builder.toString();
    }
}
