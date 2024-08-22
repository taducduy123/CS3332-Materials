package com.example.library.controllers;

import com.example.library.models.Borrow;
import com.example.library.services.impl.BorrowServiceImpl;
import com.example.library.services.IBorrowService;
import com.example.library.utils.UserContext;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import lombok.Setter;
import lombok.extern.java.Log;


import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Log
public class BorrowHistoryController implements Initializable {
    @FXML
    private TableColumn colBookId;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnReturn;
    @FXML
    private TableColumn colDueDate;
    @FXML
    private TableView<Borrow> tbBorrows;
    @FXML
    private TableColumn<Borrow, Integer> colBorrowId;
    @FXML
    private TableColumn colBookName;
    @FXML
    private TableColumn colBorrowDate;
    @FXML
    private TableColumn colReturnDate;

    @Setter
    private static String readerId;
    private final IBorrowService borrowService;

    public BorrowHistoryController() {
        this.borrowService = new BorrowServiceImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadBorrows();
        initForReader();

        btnReturn.setVisible(false);

        log.info(String.format("From %s ReaderId: %s", this.getClass().getName(), readerId));
    }

    private void loadBorrows() {
        colBorrowId.setCellValueFactory(cellData -> new SimpleIntegerProperty(tbBorrows.getItems().indexOf(cellData.getValue()) + 1).asObject());
//        colBorrowId.setCellValueFactory(new PropertyValueFactory<>("borrowId"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));

        tbBorrows.setItems(borrowService.getBorrowByReaderId(readerId));


    }

    private void initForReader() {
        if (UserContext.getInstance().getRole().equalsIgnoreCase("reader")) {
            btnReturn.setVisible(false);

            tbBorrows.setMinSize(847, 578);
        }


    }

    public void onClickReturn(ActionEvent actionEvent) {
        Optional<Borrow> selectedBorrow = Optional.ofNullable(tbBorrows.getSelectionModel().getSelectedItem());

        selectedBorrow.ifPresent(borrow -> {
            try {
                borrowService.returnBook(borrow);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            tbBorrows.setItems(borrowService.getBorrowByReaderId(readerId));
        });
    }

    public void onSearch(KeyEvent keyEvent) {
        String keyword = txtSearch.getText();
        if (keyword.isEmpty()) {
            tbBorrows.setItems(borrowService.getBorrowByReaderId(readerId));
            return;
        }

        ObservableList<Borrow> borrows = borrowService.getBorrowByReaderId(readerId);
        ObservableList<Borrow> filteredBorrows = borrows.filtered(borrow ->
                borrow.getBookName().toLowerCase().contains(keyword.toLowerCase()));


        tbBorrows.setItems(filteredBorrows);

    }
}
