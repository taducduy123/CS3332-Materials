package com.example.library.services;

import com.example.library.models.Borrow;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IBorrowService {
    ObservableList<Borrow> getBorrowByReaderId(String readerId);
    ObservableList<Borrow> getAllBookBorrowed();
    void returnBook(Borrow borrow) throws Exception;
    void borrowBook(Borrow borrow);
    int getTotalBorrowByReaderId(String readerId);
    boolean isReaderLate(String readerId);
    void requestBorrow(String bookId, LocalDate returnDate) throws Exception;
    ObservableList<Borrow> getAllRequestByReaderId(String readerId);
    void approveRequest(List<String> borrowId);
    ObservableList<Borrow> getAllRequestBorrow();
    void deleteRequest(List<String> borrowId);
    List<String> getAllEmailByBorrowIds(List<String> borrowIds);
    Map<String, String> getAllEmailWithMessagesByBorrowIds(List<String> borrowIds);
    void declineRequest(List<String> borrowId);
    void updateBorrowDate(List<String> borrowId);
}
