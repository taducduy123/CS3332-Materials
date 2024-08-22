package com.example.library.services.impl;

import com.example.library.models.Reader;
import com.example.library.repositories.IReaderRepository;
import com.example.library.repositories.impl.ReaderRepositoryImpl;
import com.example.library.services.IReaderService;
import javafx.collections.ObservableList;

import java.util.Optional;
import java.util.UUID;

public class ReaderServiceImpl implements IReaderService {
    private final IReaderRepository readerRepository;


    public ReaderServiceImpl() {
        this.readerRepository = new ReaderRepositoryImpl();
    }

    @Override
    public ObservableList<Reader> getAllReaders() {
        return readerRepository.getAllReaders();
    }

    @Override
    public ObservableList<String> getAllReaderId() {
        return readerRepository.getAllReaderId();
    }

    @Override
    public String getReaderNameById(String readerId) {
        return readerRepository.getReaderNameById(readerId);
    }

    @Override
    public void saveReader(Reader reader) {
        readerRepository.save(reader);

    }

    @Override
    public void updateReader(Reader reader) throws Exception {
        Reader existReader = readerRepository.getReaderByUsername(reader.getUsername());

        boolean isEmailExist = readerRepository.existsByEmailAndNotId(reader.getReaderEmail(), existReader.getReaderId());
        boolean isPhoneExist = readerRepository.existsByPhoneAndNotId(reader.getReaderPhone(), existReader.getReaderId());

        if (isEmailExist) {
            throw new IllegalArgumentException("Email is already exist");
        }

        if (isPhoneExist) {
            throw new IllegalArgumentException("Phone number is already exist");
        }

        readerRepository.save(reader);
    }

    @Override
    public void deleteReader(Reader reader) {
        readerRepository.delete(reader);
    }

    @Override
    public String getReaderId() {
        return "R" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }

    @Override
    public Reader getReaderByUsername(String username) {
        return readerRepository.getReaderByUsername(username);
    }

    public Optional<Reader> getReaderById(String readerId) {
        return readerRepository.getReaderById(readerId);
    }


}
