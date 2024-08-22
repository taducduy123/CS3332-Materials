package com.example.library.services.impl;

import com.example.library.repositories.*;
import com.example.library.repositories.impl.BookRepositoryImpl;
import com.example.library.repositories.impl.BorrowRepositoryImpl;
import com.example.library.repositories.impl.ReaderRepositoryImpl;
import com.example.library.services.IStaticService;

public class StaticServiceImpl implements IStaticService {


    private final IReaderRepository readerRepository;
    private final IBorrowRepository borrowRepository;
    private final IBookRepository bookRepository;

    public StaticServiceImpl() {
        readerRepository = new ReaderRepositoryImpl();
        borrowRepository = new BorrowRepositoryImpl();
        bookRepository = new BookRepositoryImpl();
    }

    @Override
    public int getTotalBook() {
        return bookRepository.getTotalBook();
    }

    @Override
    public int getTotalReader() {
        return readerRepository.getTotalReader();
    }

    @Override
    public int getTotalBorrow() {
        return borrowRepository.getTotalBorrow();
    }

    @Override
    public int getTotalLate() {
        return borrowRepository.getTotalLate();
    }

    @Override
    public int getTotalReturn() {
        return borrowRepository.getTotalReturn();
    }

}
