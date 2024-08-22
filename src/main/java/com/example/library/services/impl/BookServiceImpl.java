package com.example.library.services.impl;

import com.example.library.models.Author;
import com.example.library.models.Book;
import com.example.library.repositories.impl.AuthorRepositoryImpl;
import com.example.library.repositories.impl.BookRepositoryImpl;
import com.example.library.repositories.IAuthorRepository;
import com.example.library.repositories.IBookRepository;
import com.example.library.services.IBookService;
import javafx.collections.ObservableList;

import java.util.UUID;

public class BookServiceImpl implements IBookService {

    private final IBookRepository bookRepository;
    private final IAuthorRepository authorRepository;

    public BookServiceImpl() {
        this.bookRepository = new BookRepositoryImpl();
        this.authorRepository = new AuthorRepositoryImpl();
    }

    @Override
    public ObservableList<Book> getAllBook() {
        return bookRepository.getAllBook();
    }


    @Override
    public ObservableList<String> getAllCategoryName() {
        return bookRepository.getAllCategoryName();
    }

    @Override
    public ObservableList<String> getAllBookId() {
        return bookRepository.getAllBookId();
    }

    @Override
    public ObservableList<Author> getAllAuthors() {
        return authorRepository.getAllAuthor();
    }

    @Override
    public String getBookNameById(String bookId) {
        return bookRepository.getBookNameById(bookId);
    }

    @Override
    public void saveBook(Book book) throws Exception {
        boolean isExist = bookRepository.isExistBook(book.getBookId());

        if (isExist) {
            throw new Exception("This book already existed");
        }

        setBook(book);
        bookRepository.save(book);

    }

    @Override
    public void deleteBook(Book book) {
        bookRepository.delete(book);
    }

    @Override
    public void updateBook(Book book) {
        setBook(book);
        bookRepository.save(book);

    }

    @Override
    public String getBookId() {
        return "B" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }

    @Override
    public boolean isQuantityEnough(String bookId) {
        System.out.println(bookRepository.getBookQuantity(bookId));
        return bookRepository.getBookQuantity(bookId) > 0;
    }

    private void setBook(Book book) {
        String categoryId = bookRepository.getCategoryIdByName(book.getCategory());
        String authorId = authorRepository.getAuthorIdByName(book.getAuthor());

        if (categoryId == null) {
            bookRepository.saveCategory(book.getCategory());
            categoryId = bookRepository.getCategoryIdByName(book.getCategory());
        }

        if (authorId == null) {
            bookRepository.saveAuthor(book.getAuthor());
            authorId = authorRepository.getAuthorIdByName(book.getAuthor());
        }

        book.setCategory(categoryId);
        book.setAuthor(authorId);


    }

}
