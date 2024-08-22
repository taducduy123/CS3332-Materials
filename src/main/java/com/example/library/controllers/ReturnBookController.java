package com.example.library.controllers;

import com.example.library.models.Borrow;
import com.example.library.services.IBorrowService;
import com.example.library.services.impl.BorrowServiceImpl;
import com.example.library.utils.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ReturnBookController {
    @FXML
    private TextField txtBookId;
    @FXML
    private TextField txtReaderId;

    private final IBorrowService borrowService;

    public ReturnBookController() {
        this.borrowService = new BorrowServiceImpl();
    }

    public void onClickOk(ActionEvent actionEvent) {
        String bookId = txtBookId.getText();
        String readerId = txtReaderId.getText();

        if (bookId.isEmpty() || readerId.isEmpty()) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, "Please fill all fields");
            return;
        }

        Borrow borrow = Borrow.builder()
                .bookId(bookId)
                .readerId(readerId)
                .build();

        try {
            borrowService.returnBook(borrow);
        } catch (Exception e) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, e.getMessage());
            return;
        }

        AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Information", null, "Return book successfully");


    }
}
