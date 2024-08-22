package com.example.library.controllers;

import com.example.library.App;
import com.example.library.models.Account;
import com.example.library.models.Reader;
import com.example.library.services.*;
import com.example.library.services.impl.AccountServiceImpl;
import com.example.library.services.impl.MailService;
import com.example.library.services.impl.ReaderServiceImpl;
import com.example.library.utils.AlertUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import static com.example.library.common.Regex.isValid;

public class ReaderManagementController implements Initializable {
    @FXML
    private TextField txtUsername;
    @FXML
    private CheckBox isBlock;
    @FXML
    private TableColumn colUsername;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnRefresh;
    @FXML
    private TextField txtReaderId;
    @FXML
    private TextField txtReaderName;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPhoneNumber;
    @FXML
    private TextField txtAddress;
    @FXML
    private DatePicker dpDob;
    @FXML
    private TableView<Reader> tbReaders;
    @FXML
    private TableColumn colReaderId;
    @FXML
    private TableColumn colReaderName;
    @FXML
    private TableColumn colEmail;
    @FXML
    private TableColumn colPhoneNumber;
    @FXML
    private TableColumn colDob;
    @FXML
    private TableColumn colAddress;

    private final IReaderService readerService;
    private final IAccountService accountService;
    private final MailService mailService;

    public ReaderManagementController() {
        this.readerService = new ReaderServiceImpl();
        this.accountService = new AccountServiceImpl();
        this.mailService = new MailService("smtp.gmail.com");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadReaders();
        customDatePicker();
        txtReaderId.setText(readerService.getReaderId());

//        btnAdd.setVisible(true);
//        btnDelete.setVisible(false);
//        btnUpdate.setVisible(false);
    }

    public void loadReaders() {

        colReaderId.setCellValueFactory(new PropertyValueFactory<>("readerId"));
        colReaderName.setCellValueFactory(new PropertyValueFactory<>("readerName"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("readerEmail"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("readerPhone"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("readerDOB"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("readerAddress"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        tbReaders.setItems(readerService.getAllReaders());
    }


    public void onSelected(MouseEvent mouseEvent) {
        Optional<Reader> selectedReader = Optional.ofNullable(tbReaders.getSelectionModel().getSelectedItem());

        if (selectedReader.isPresent()) {
            txtReaderId.setText(selectedReader.get().getReaderId());
            txtReaderName.setText(selectedReader.get().getReaderName());
            txtEmail.setText(selectedReader.get().getReaderEmail());
            txtPhoneNumber.setText(selectedReader.get().getReaderPhone());
            dpDob.setValue(selectedReader.get().getReaderDOB());
            txtAddress.setText(selectedReader.get().getReaderAddress());
            txtUsername.setText(selectedReader.get().getUsername());
            isBlock.setSelected(selectedReader.get().isBlocked());

//            btnAdd.setVisible(false);
//            btnDelete.setVisible(true);
//            btnUpdate.setVisible(true);
        }
    }

    public void onClickAdd(ActionEvent actionEvent) {
        String readerId = txtReaderId.getText();
        String readerName = txtReaderName.getText();
        String email = txtEmail.getText();
        String phoneNumber = txtPhoneNumber.getText();
        String address = txtAddress.getText();
        String dob = dpDob.getValue() == null ? "" : dpDob.getValue().toString();
        String username = txtUsername.getText();

        if (isNull(readerId, readerName, email, phoneNumber, address, dob, username)) {
            return;
        }

        if (!isValid("EMAIL", email)) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, "Invalid email!");
            return;
        }

        if (!isValid("PHONE_NUMBER", phoneNumber)) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, "Invalid phone number!");
            return;
        }

        Reader reader = Reader.builder()
                .readerId(readerId)
                .readerName(readerName)
                .readerEmail(email)
                .readerPhone(phoneNumber)
                .readerDOB(dpDob.getValue())
                .readerAddress(address)
                .build();

        String password = UUID.randomUUID().toString().substring(0, 8);

        Account account = Account.builder()
                .username(username)
                .role("reader")
                .password(password)
                .build();

        try {
            accountService.registerAccount(account, reader);
            readerService.saveReader(reader);
            mailService.sendMail(
                    Map.of(
                            email,
                            String.format("Hello <b>%s</b>," +
                                    " your account has been created successfully with username <b>%s</b> and password <b>%s</b>," +
                                    " please change after login", readerName, username, password)
                    ),
                    "New account"
            );
            clear();
            AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Information", null, "Add reader successfully!");
        } catch (Exception e) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, e.getMessage());
            return;
        }
        tbReaders.setItems(readerService.getAllReaders());


    }

    public void onClickDelete(ActionEvent actionEvent) {
        Optional<Reader> selectedReader = Optional.ofNullable(tbReaders.getSelectionModel().getSelectedItem());

        if (selectedReader.isPresent() && AlertUtil.showConfirmation("Are you sure you want to delete this reader?")) {
            readerService.deleteReader(selectedReader.get());
            clear();

        }
    }

    public void onClickUpdate(ActionEvent actionEvent) {
        Optional<Reader> selectedReader = Optional.ofNullable(tbReaders.getSelectionModel().getSelectedItem());

        if (selectedReader.isEmpty()) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Lỗi", null, "Please select a reader to update");
            return;
        }

        String readerId = txtReaderId.getText();
        String readerName = txtReaderName.getText();
        String email = txtEmail.getText();
        String phoneNumber = txtPhoneNumber.getText();
        String address = txtAddress.getText();
        String dob = dpDob.getValue().toString();

        boolean isBlocked = isBlock.isSelected();

        if (isNull(readerId, readerName, email, phoneNumber, address, dob)) {
            return;
        }

        if (!isValid("EMAIL", email)) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, "Invalid email!");
            return;
        }

        if (!isValid("PHONE_NUMBER", phoneNumber)) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, "Invalid phone number!");
            return;
        }


        Reader reader = Reader.builder()
                .readerName(readerName)
                .readerEmail(email)
                .readerPhone(phoneNumber)
                .readerDOB(dpDob.getValue())
                .readerAddress(address)
                .isBlocked(isBlocked)
                .username(txtUsername.getText())
                .readerId(readerId)
                .build();


        try {
            readerService.updateReader(reader);
            AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Information", null, "Update reader successfully!");
            clear();
        } catch (Exception e) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, e.getMessage());
            return;
        }
        tbReaders.setItems(readerService.getAllReaders());
    }

    public void onClickRefresh(ActionEvent actionEvent) {
        clear();


    }

    private void clear() {
        txtAddress.clear();
        txtEmail.clear();
        txtPhoneNumber.clear();
        txtReaderName.clear();
        dpDob.setValue(null);
        tbReaders.getSelectionModel().clearSelection();
        txtReaderId.setText(readerService.getReaderId());
        txtUsername.clear();

        tbReaders.setItems(readerService.getAllReaders());
    }

    private boolean isNull(Object... o) {
        for (Object obj : o) {
            if (obj == null || obj.toString().isEmpty()) {
                AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, "Please fill all fields!");
                return true;
            }
        }
        return false;
    }


    private void customDatePicker() {
        dpDob.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(item.isAfter(LocalDate.now()));
            }
        });
    }

    public void onSearch(KeyEvent keyEvent) {
        String keyword = txtSearch.getText();

        if (keyword.isEmpty()) {
            tbReaders.setItems(readerService.getAllReaders());
        } else {
            tbReaders.setItems(readerService.getAllReaders().filtered(reader -> {
// search by name, id, email, phone number, address, username
                return reader.getReaderName().toLowerCase().contains(keyword.toLowerCase()) ||
                        reader.getReaderId().toLowerCase().contains(keyword.toLowerCase()) ||
                        reader.getReaderEmail().toLowerCase().contains(keyword.toLowerCase()) ||
                        reader.getReaderPhone().toLowerCase().contains(keyword.toLowerCase()) ||
                        reader.getReaderAddress().toLowerCase().contains(keyword.toLowerCase()) ||
                        reader.getUsername().toLowerCase().contains(keyword.toLowerCase());

            }));
        }
    }

    public void onClickHistory(ActionEvent actionEvent) {
        Optional<Reader> selectedReader = Optional.ofNullable(tbReaders.getSelectionModel().getSelectedItem());

        selectedReader.ifPresent(reader -> {
            BorrowHistoryController.setReaderId(reader.getReaderId());
            try {
                App.setRootPop("BorrowHistoryFrm", "Borrow history", false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
