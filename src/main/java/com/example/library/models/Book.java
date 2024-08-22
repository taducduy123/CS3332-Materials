package com.example.library.models;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@AllArgsConstructor
@ToString
public class Book {
    private String bookId;
    private String bookName;
    private String author;
    private String category;
    private int quantity;
    private LocalDate publisher;

}
