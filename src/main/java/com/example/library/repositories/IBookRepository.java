package com.example.library.repositories;

import com.example.library.models.Book;
import javafx.collections.ObservableList;

public interface IBookRepository {
    void save(Book book);
    void delete(Book book);
    void saveCategory(String category);
    void saveAuthor(String author);
    String getCategoryIdByName(String categoryName);
    ObservableList<Book> getAllBook();
    ObservableList<String> getAllCategoryName();
    String getBookIdByName(String bookName);
    void increaseQuantity(String bookId);
    void decreaseQuantity(String bookId);
    ObservableList<String> getAllBookId();
    String getBookNameById(String bookId);
    int getTotalBook();
    String getBookId();
    int getBookQuantity(String bookId);
    boolean isExistBook(String bookId);
}
