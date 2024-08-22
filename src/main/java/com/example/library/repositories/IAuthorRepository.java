package com.example.library.repositories;

import com.example.library.models.Author;
import javafx.collections.ObservableList;

public interface IAuthorRepository {
    ObservableList<Author> getAllAuthor();
    String getAuthorIdByName(String author);
}
