package com.example.library.controllers;

import com.example.library.models.Borrow;
import com.example.library.services.*;
import com.example.library.services.impl.BookServiceImpl;
import com.example.library.services.impl.BorrowServiceImpl;
import com.example.library.services.impl.ReaderServiceImpl;
import com.example.library.utils.AlertUtil;
import com.example.library.utils.SettingUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class BorrowManagementController implements Initializable {
    @FXML
    private ComboBox<String> cbFilterLate;
    @FXML
    private ComboBox<String> cbReaderId;
    @FXML
    private ComboBox<String> cbBookId;
    @FXML
    private DatePicker dpReturnDate;
    @FXML
    private Label lbReaderName;
    @FXML
    private Label lbBookName;
    @FXML
    private TableView<Borrow> tbBorrows;
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colBookName;
    @FXML
    private TableColumn colReaderName;
    @FXML
    private TableColumn colBorrowDate;
    @FXML
    private TableColumn colReturnDate;
    @FXML
    private TableColumn colDueDate;

    private final IBorrowService borrowService;
    private final IBookService bookService;
    private final IReaderService readerService;
    private final SettingUtils settingUtils = SettingUtils.getInstance();

    public BorrowManagementController() {
        borrowService = new BorrowServiceImpl();
        bookService = new BookServiceImpl();
        readerService = new ReaderServiceImpl();


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadBorrows();
        initComboBox();
        customDatePickers();

        tbBorrows.setRowFactory(tv -> new TableRow<Borrow>() {
            @Override
            protected void updateItem(Borrow borrow, boolean empty) {
                super.updateItem(borrow, empty);
                if (borrow == null) {
                    setStyle("");
                } else {
                    if (borrow.getDueDate() != null && settingUtils.isHighlightReturn()) {
                        setStyle("-fx-background-color: green");
                    } else {
                        setStyle("");
                    }
                    if (borrow.getDueDate() == null && borrow.getReturnDate().isBefore(LocalDate.now()) && settingUtils.isHighlightLate()) {
                        setStyle("-fx-background-color: red;");
                    }
                }
            }
        });

//        tbBorrows.setRowFactory(tv -> new TableRow<Borrow>() {
//            @Override
//            protected void updateItem(Borrow borrow, boolean empty) {
//                super.updateItem(borrow, empty);
//                if (borrow == null || empty) {
//                    setStyle("");
//                } else {
//                    LocalDate now = LocalDate.now();
//                    if ((borrow.getDueDate() == null && borrow.getReturnDate().isBefore(now)) && settingUtils.isHighlightLate()) {
//                        setStyle("-fx-background-color: red;");
//                    } else {
//                        setStyle("");
//                    }
//                }
//            }
//        });

    }

    private void initComboBox() {
        cbFilterLate.getItems().addAll("Quá hạn", "Chưa trả", "Đã trả", "Không");
        cbFilterLate.getSelectionModel().selectLast();
        cbBookId.getItems().addAll(bookService.getAllBookId());
        cbReaderId.getItems().addAll(readerService.getAllReaderId());
    }

    private void loadBorrows() {
        colId.setCellValueFactory(new PropertyValueFactory<>("borrowId"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colReaderName.setCellValueFactory(new PropertyValueFactory<>("readerName"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        tbBorrows.setItems(borrowService.getAllBookBorrowed());
    }

    public void onChooseReaderId(ActionEvent actionEvent) {
        String readerId = cbReaderId.getValue();
        lbReaderName.setText(readerService.getReaderNameById(readerId));
    }

    public void onChooseBookId(ActionEvent actionEvent) {
        String bookId = cbBookId.getValue();
        lbBookName.setText(bookService.getBookNameById(bookId));
    }

    private void customDatePickers() {
        dpReturnDate.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isBefore(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                };
            }
        });
    }

    public void onClickBorrow(ActionEvent actionEvent) {

        if (isNull(cbReaderId.getValue(), cbBookId.getValue(), dpReturnDate.getValue())) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Lỗi", null, "Vui lòng chọn độc giả, sách và ngày trả");
            return;
        }

        if (borrowService.getTotalBorrowByReaderId(cbReaderId.getValue()) >= 3) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Lỗi", null, "Độc giả đã mượn quá số lượng cho phép (<=3)");
            return;
        }

        if (!bookService.isQuantityEnough(cbBookId.getValue())) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Lỗi", null, "Số lượng sách không đủ");
            return;
        }

        Borrow borrow = Borrow.builder()
                .bookName(cbBookId.getValue())
                .readerName(cbReaderId.getValue())
                .borrowDate(LocalDate.now())
                .returnDate(dpReturnDate.getValue())
                .dueDate(null)
                .build();

        borrowService.borrowBook(borrow);
        AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Thông báo", null, "Mượn sách thành công");
        tbBorrows.setItems(borrowService.getAllBookBorrowed());
    }

    private boolean isNull(Object... o) {
        for (Object obj : o) {
            if (obj == null || obj.toString().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void onSelected(MouseEvent mouseEvent) {
    }

    public void onChooseFilter(ActionEvent actionEvent) {
        String filter = cbFilterLate.getValue();
        ObservableList<Borrow> borrows = borrowService.getAllBookBorrowed();

        switch (filter) {
            case "Chưa trả":
                borrows = borrows.filtered(borrow -> borrow.getDueDate() == null);
                break;
            case "Đã trả":
                borrows = borrows.filtered(borrow -> borrow.getDueDate() != null);
                break;
            case "Quá hạn":
                LocalDate today = LocalDate.now();
                borrows = borrows.filtered(borrow -> {
                    String returnDate = String.valueOf(borrow.getReturnDate());
                    return returnDate != null && !returnDate.isEmpty() && LocalDate.parse(returnDate).isBefore(today);
                });
                break;
            default:
                break;
        }

        tbBorrows.setItems(borrows);
    }

    public void onClickRefresh(ActionEvent actionEvent) {
        tbBorrows.getSelectionModel().clearSelection();
        cbFilterLate.getSelectionModel().selectLast();
        cbBookId.getSelectionModel().clearSelection();
        cbReaderId.getSelectionModel().clearSelection();
        dpReturnDate.setValue(null);
        lbBookName.setText("Tên sách");
        lbReaderName.setText("Tên độc giả");
    }
}
