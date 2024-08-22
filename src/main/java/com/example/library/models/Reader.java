package com.example.library.models;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Reader {
    private String readerId;
    private String readerName;
    private String readerEmail;
    private String readerPhone;
    private LocalDate readerDOB;
    private String readerAddress;
    private boolean isBlocked;
    private String username;
    private int userId;
}
