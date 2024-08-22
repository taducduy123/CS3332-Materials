package com.example.library.controllers;

import com.example.library.models.Borrow;
import com.example.library.services.IBorrowService;
import com.example.library.services.impl.BorrowServiceImpl;
import com.example.library.services.impl.MailService;
import com.example.library.utils.AlertUtil;
import com.example.library.utils.UserContext;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class RequestController implements Initializable {
    @FXML
    private TableColumn colBookId;
    @FXML
    private TableColumn colSelect;
    @FXML
    private final IBorrowService borrowService;
    @FXML
    private TableColumn<Borrow, Integer> colId;
    @FXML
    private TableView<Borrow> tbRequest;
    @FXML
    private TableColumn colReaderName;
    @FXML
    private TableColumn colBookName;
    @FXML
    private TableColumn colBorrowDate;
    @FXML
    private TableColumn colReturnDate;
    @FXML
    private Button btnApprove;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnReject;
    private final List<String> selectedBorrowId;
    private final MailService mailService;

    public RequestController() {
        this.mailService = new MailService("smtp.gmail.com");
        this.borrowService = new BorrowServiceImpl();
        selectedBorrowId = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDataToTable();


        if (UserContext.getInstance().getRole().equals("Reader")) {
            setUpForReader();
        } else {
            btnApprove.setOnAction(this::onClickApprove);
            txtSearch.setPromptText("Search by Book Name or Reader Name");
            btnReject.setVisible(true);
        }
    }


    private void setDataToTable() {
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(tbRequest.getItems().indexOf(cellData.getValue()) + 1).asObject());
        colReaderName.setCellValueFactory(new PropertyValueFactory<>("readerName"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colSelect.setCellFactory(tc -> new TableCell<Borrow, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    Borrow borrow = getTableRow().getItem();
                    if (checkBox.isSelected()) {
                        selectedBorrowId.add(borrow.getBorrowId());
                    } else {
                        selectedBorrowId.remove(borrow.getBorrowId());
                    }

                    System.out.println(selectedBorrowId);
                });
            }

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(checkBox);
                }
            }
        });

        if (UserContext.getInstance().getRole().equals("Reader")) {
            tbRequest.setItems(borrowService.getAllRequestByReaderId(UserContext.getInstance().getReaderId()));
        } else {
            tbRequest.setItems(borrowService.getAllRequestBorrow());
        }
    }


    private void setUpForReader() {
        colReaderName.setVisible(false);
        btnApprove.setText("Delete Request");
        btnApprove.setOnAction(this::onClickDeleteRequest);
        btnReject.setVisible(false);
        txtSearch.setPromptText("Search by Book Name");
    }

    public void onClickApprove(ActionEvent actionEvent) {
        if(selectedBorrowId.isEmpty()){
            AlertUtil.showAlert(Alert.AlertType.ERROR, "No Request Selected", null,"Please select a request to approve");
            return;
        }


        Map<String, String> emailMessages = borrowService.getAllEmailWithMessagesByBorrowIds(selectedBorrowId);

        mailService.sendMail(
                emailMessages,
                "Request Approved"
        );


        borrowService.approveRequest(selectedBorrowId);
        setDataToTable();
        AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Request Approved", null,"Request Approved Successfully");
        selectedBorrowId.clear();
    }

    public void onClickReject(ActionEvent actionEvent) {
        if(selectedBorrowId.isEmpty()){
            AlertUtil.showAlert(Alert.AlertType.ERROR, "No Request Selected", null,"Please select a request to reject");
            return;
        }

        borrowService.declineRequest(selectedBorrowId);

        Map<String, String> emailMessages = borrowService.getAllEmailWithMessagesByBorrowIds(selectedBorrowId);

        mailService.sendMail(
                emailMessages,
                "Request Declined"
        );


        setDataToTable();
        borrowService.deleteRequest(selectedBorrowId);

        AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Request Declined", null,"Request Declined Successfully");
        selectedBorrowId.clear();
    }

    public void onClickDeleteRequest(ActionEvent actionEvent) {
        if(selectedBorrowId.isEmpty()){
            AlertUtil.showAlert(Alert.AlertType.ERROR, "No Request Selected", null,"Please select a request to delete");
            return;
        }

        if(!AlertUtil.showConfirmation("Are you sure you want to delete this request?")){
            return;
        }


        borrowService.deleteRequest(selectedBorrowId);
        setDataToTable();

        AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Request Deleted", null,"Request Deleted Successfully");

        selectedBorrowId.clear();


    }

    public void onSearch(KeyEvent keyEvent) {
        String keyword = txtSearch.getText();
        if (keyword.isEmpty()) {
            setDataToTable();
            return;
        }
        if (UserContext.getInstance().getRole().equals("Reader")) {
            ObservableList<Borrow> borrows = borrowService.getAllRequestByReaderId(UserContext.getInstance().getReaderId());
            ObservableList<Borrow> filteredBorrows = borrows.filtered(borrow ->
                    borrow.getBookName().toLowerCase().contains(keyword.toLowerCase()));
            tbRequest.setItems(filteredBorrows);
        } else {
            ObservableList<Borrow> borrows = borrowService.getAllRequestBorrow();
            ObservableList<Borrow> filteredBorrows = borrows.filtered(borrow ->
                    borrow.getBookName().toLowerCase().contains(keyword.toLowerCase()) ||
                            borrow.getReaderName().toLowerCase().contains(keyword.toLowerCase())

            );
            tbRequest.setItems(filteredBorrows);
        }
    }


}
