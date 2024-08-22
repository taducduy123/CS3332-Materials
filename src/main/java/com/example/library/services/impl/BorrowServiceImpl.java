package com.example.library.services.impl;

import com.example.library.models.Borrow;
import com.example.library.repositories.*;
import com.example.library.repositories.impl.BookRepositoryImpl;
import com.example.library.repositories.impl.BorrowRepositoryImpl;
import com.example.library.repositories.impl.ReaderRepositoryImpl;
import com.example.library.services.IBorrowService;
import com.example.library.utils.UserContext;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class BorrowServiceImpl implements IBorrowService {
    private final IBorrowRepository borrowRepository;
    private final IBookRepository bookRepository;
    private final IReaderRepository readerRepository;

    public BorrowServiceImpl() {
        borrowRepository = new BorrowRepositoryImpl();
        bookRepository = new BookRepositoryImpl();
        readerRepository = new ReaderRepositoryImpl();
    }

    @Override
    public ObservableList<Borrow> getBorrowByReaderId(String readerId) {
        ObservableList<Borrow> borrows = borrowRepository.getBorrowByReaderId(readerId);


        return borrows.filtered(
                borrow -> borrow.getStatus() == null ||
                        borrow.getStatus().isEmpty() ||
                        borrow.getStatus().isBlank());

    }

    @Override
    public ObservableList<Borrow> getAllBookBorrowed() {
        ObservableList<Borrow> borrows = borrowRepository.getAllBookBorrowed();
        return borrows.filtered(
                borrow -> borrow.getStatus() == null ||
                        borrow.getStatus().isEmpty() ||
                        borrow.getStatus().isBlank());
    }

    @Override
    public void returnBook(Borrow borrow) throws Exception {
        boolean isBorrow = borrowRepository.isAlreadyRequest(borrow.getReaderId(), borrow.getBookId());

        if (!isBorrow) {
            throw new Exception("This book is not borrowed by this reader");
        }

        borrowRepository.returnBook(borrow);

        bookRepository.increaseQuantity(borrow.getBookId());
    }

    @Override
    public void borrowBook(Borrow borrow) {
        if (borrow.getBorrowId() != null) {
            String bookId = bookRepository.getBookIdByName(borrow.getBookName());
            String readerId = readerRepository.getReaderIdByName(borrow.getReaderName());

            borrow.setBookName(bookId);
            borrow.setReaderName(readerId);


        }

        borrowRepository.save(borrow);
        bookRepository.decreaseQuantity(borrow.getBookName());
    }

    @Override
    public int getTotalBorrowByReaderId(String readerId) {
        return borrowRepository.getTotalBorrowByReaderId(readerId);
    }

    @Override
    public boolean isReaderLate(String readerId) {
        return borrowRepository.isReaderLate(readerId);
    }

    @Override
    public void requestBorrow(String bookId, LocalDate returnDate) throws Exception {
        boolean isBorrowed = borrowRepository.isAlreadyRequest(UserContext.getInstance().getReaderId(), bookId);
        if (isBorrowed) {
            throw new Exception("You have already requested this book");
        }
        borrowRepository.requestBorrow(bookId, returnDate);
    }

    @Override
    public ObservableList<Borrow> getAllRequestByReaderId(String readerId) {
        ObservableList<Borrow> borrows = borrowRepository.getAllBookBorrowed();
        return borrows.filtered(borrow ->
                "REQUEST".equals(borrow.getStatus()) &&
                        readerId.equals(borrow.getReaderId())
        );
    }

    public ObservableList<Borrow> getAllRequestBorrow() {
        ObservableList<Borrow> borrows = borrowRepository.getAllBookBorrowed();
        return borrows.filtered(borrow ->
                "REQUEST".equals(borrow.getStatus())
        );
    }

    @Override
    public void deleteRequest(List<String> borrowId) {
        borrowRepository.deleteRequest(borrowId);
    }

    @Override
    public List<String> getAllEmailByBorrowIds(List<String> borrowIds) {
        return borrowRepository.getAllEmailByBorrowIds(borrowIds);
    }

    @Override
    public Map<String, String> getAllEmailWithMessagesByBorrowIds(List<String> borrowIds) {
        return borrowRepository.getAllEmailWithMessagesByBorrowIds(borrowIds);
    }

    @Override
    public void declineRequest(List<String> borrowId) {
        borrowRepository.declineRequest(borrowId);


    }

    @Override
    public void updateBorrowDate(List<String> borrowId) {
        borrowRepository.updateBorrowDate(borrowId);
    }

    @Override
    public void approveRequest(List<String> borrowIds) {
        borrowRepository.approveRequest(borrowIds);

        updateBorrowDate(borrowIds);

        List<String> bookIds = borrowRepository.getAllBookIdByBorrowId(borrowIds);

        bookIds.forEach(bookRepository::decreaseQuantity);
    }


}

