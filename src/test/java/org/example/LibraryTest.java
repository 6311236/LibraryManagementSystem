package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    private Library library;
    private Student student;
    private Teacher teacher;
    private Book bookOne;
    private Book bookTwo;
    private Book bookThree;
    private Book bookFour;
    private Book bookFive;
    private Book bookSix;
    private DVD dvd;

    @BeforeEach
    void setUp() {
        library = new Library();
        student = Student.createWithGeneratedId("Casey");
        teacher = Teacher.createWithGeneratedId("Morgan");
        library.registerUser(student);
        library.registerUser(teacher);
        bookOne = library.addBookCopy("Alpha", "9780000000001", "Ann Author", "Fiction");
        bookTwo = library.addBookCopy("Beta", "9780000000002", "Ann Author", "Fiction");
        bookThree = library.addBookCopy("Gamma", "9780000000003", "Ann Author", "Fiction");
        bookFour = library.addBookCopy("Delta", "9780000000004", "Bob Author", "SciFi");
        bookFive = library.addBookCopy("Epsilon", "9780000000005", "Bob Author", "SciFi");
        bookSix = library.addBookCopy("Zeta", "9780000000006", "Cara Author", "Drama");
        dvd = library.addDvdCopy("Ocean Tales", "Dana Director", 96);
    }

    @Test
    void studentMayBorrowSixthItemIfItIsNotABook() {
        library.borrow(student, bookOne);
        library.borrow(student, bookTwo);
        library.borrow(student, bookThree);
        library.borrow(student, bookFour);
        library.borrow(student, bookFive);
        library.borrow(student, dvd);
        Assertions.assertEquals(5, student.countBorrowedBooks());
        Assertions.assertEquals(6, student.getBorrowedItems().size());
    }

    @Test
    void studentCannotBorrowSixthBook() {
        library.borrow(student, bookOne);
        library.borrow(student, bookTwo);
        library.borrow(student, bookThree);
        library.borrow(student, bookFour);
        library.borrow(student, bookFive);
        Assertions.assertThrows(BorrowLimitExceededException.class, () -> library.borrow(student, bookSix));
    }

    @Test
    void teacherCannotExceedTenItems() {
        for (int i = 0; i < 10; i++) {
            String isbn = String.valueOf(9780000000000L + i);
            Book extra = library.addBookCopy("Title" + i, isbn, "Author", "Genre");
            library.borrow(teacher, extra);
        }
        Assertions.assertThrows(BorrowLimitExceededException.class, () -> library.borrow(teacher, bookOne));
    }

    @Test
    void recursiveSearchCollapsesDuplicateIsbn() {
        Library lib = new Library();
        lib.addBookCopy("First Title", "9785000000001", "Unique Author", "Genre");
        lib.addBookCopy("Second Title", "9785000000001", "Unique Author", "Genre");
        List<Item> hits = ItemSearch.searchRecursive(lib.getItems(), "unique");
        Assertions.assertEquals(1, hits.size());
    }

    @Test
    void recursiveSearchMatchesStreamSearch() {
        Library lib = new Library();
        lib.addBookCopy("Alpha", "9786000000001", "Jamie Writer", "Fiction");
        lib.addDvdCopy("Alpha Nights", "Jamie Writer", 110);
        List<Item> recursive = ItemSearch.searchRecursive(lib.getItems(), "jamie");
        List<Item> streamed = ItemSearch.searchWithStream(lib.getItems(), "jamie");
        Assertions.assertEquals(recursive.size(), streamed.size());
        Assertions.assertEquals(2, recursive.size());
    }

    @Test
    void returnsAreTrackedOnStack() {
        library.borrow(student, bookOne);
        library.returnItem(student, bookOne);
        Assertions.assertEquals(bookOne, library.peekLatestReturn());
    }

    @Test
    void isbnHoldQueuePreservesFifoOrder() {
        Library lib = new Library();
        lib.enqueueIsbnHold("9789000000001");
        lib.enqueueIsbnHold("9789000000002");
        Assertions.assertEquals("9789000000001", lib.pollNextIsbnHold());
        Assertions.assertEquals("9789000000002", lib.pollNextIsbnHold());
    }

    @Test
    void knownGenresSetTracksBooks() {
        Assertions.assertTrue(library.getKnownGenres().contains("Fiction"));
    }

}