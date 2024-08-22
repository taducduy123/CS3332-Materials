CREATE database library;
use library;

drop database library;

create table authors
(
    authorId   int auto_increment primary key,
    authorName nvarchar(30),
    isDelete   bool default false
);


create table categories
(
    categoryId   int primary key auto_increment,
    categoryName nvarchar(30)
);



create table books
(
    bookId      nvarchar(30) primary key,
    authorId    nvarchar(30),
    categoryId  int,
    bookName    nvarchar(100),
    quantity    int,
    publishDate date,
    isDelete    bool default false
);

create table readers
(
    readerId          nvarchar(30) primary key,
    readerName        nvarchar(30),
    readerEmail       nvarchar(30),
    readerPhoneNumber nvarchar(11),
    readerDob         date,
    address           nvarchar(100),
    isDelete          bool default false
);


create table borrow
(
    borrowId   int auto_increment primary key,
    bookId     nvarchar(30),
    readerId   nvarchar(30),
    borrowDate date,
    returnDate date,
    dueDate date,
    isDelete   bool default false
);

create table users
(
    userId   int auto_increment primary key,
    username varchar(100),
    password varchar(1000)
);



ALTER TABLE books
    ADD CONSTRAINT fk_authorId
        FOREIGN KEY (authorId)
            REFERENCES authors (authorId);

ALTER TABLE books
    ADD CONSTRAINT fk_categoryId
        FOREIGN KEY (categoryId)
            REFERENCES categories (categoryId);

ALTER TABLE borrow
    ADD CONSTRAINT fk_bookId
        FOREIGN KEY (bookId)
            REFERENCES books (bookId);

ALTER TABLE borrow
    ADD CONSTRAINT fk_readerId
        FOREIGN KEY (readerId)
            REFERENCES readers (readerId);


