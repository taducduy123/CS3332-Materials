package com.example.library.models;

import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Borrow {
    private String borrowId;
    private String readerId;
    private String bookName;
    private String readerName;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private String dueDate;
    private String status;
    private String bookId;



}
