module com.example.library {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.compiler;
    requires java.sql;
    requires static lombok;
    requires java.mail;


    opens com.example.library to javafx.fxml;
    opens com.example.library.controllers to javafx.fxml;
    opens com.example.library.models to javafx.fxml;
    opens com.example.library.controllers.client to javafx.fxml;

    exports com.example.library;
    exports com.example.library.controllers;
    exports com.example.library.models;
    exports com.example.library.services;
    exports com.example.library.utils;
    exports com.example.library.repositories;
    exports com.example.library.controllers.client;
    exports com.example.library.repositories.impl;
    exports com.example.library.services.impl;
}