package com.example.library.controllers;

import com.example.library.models.Book;
import com.example.library.services.IBorrowService;
import com.example.library.services.impl.BookServiceImpl;
import com.example.library.services.IBookService;
import com.example.library.services.impl.BorrowServiceImpl;
import com.example.library.utils.AlertUtil;
import com.example.library.utils.UserContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.example.library.common.Regex.isValid;

public class BookManagementController implements Initializable {
    @FXML
    private Button btnRequestBorrow;
    @FXML
    private DatePicker dpReturn;
    @FXML
    private GridPane grForReader;
    @FXML
    private Text lbCategory;
    @FXML
    private Text lbQuantity;
    @FXML
    private Text lbPublishDate;
    @FXML
    private Text lbBookId;
    @FXML
    private Text lbBookName;
    @FXML
    private Text lbAuthor;
    @FXML
    private TextField txtAuthor;
    @FXML
    private Button btnAddAuthor;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnAddCategory;
    @FXML
    private ComboBox<String> cbCategory;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnUpdate;
    @FXML
    private TextField txtBookId;
    @FXML
    private TextField txtBookName;
    @FXML
    private TextField txtCategory;
    @FXML
    private TextField txtQuantity;
    @FXML
    private DatePicker dpPublish;
    @FXML
    private ComboBox<String> cbAuthor;
    @FXML
    private TableView<Book> tbBooks;
    @FXML
    private TableColumn<Book, String> colBookId;
    @FXML
    private TableColumn<Book, String> colBookName;
    @FXML
    private TableColumn<Book, String> colAuthorName;
    @FXML
    private TableColumn<Book, String> colCategory;
    @FXML
    private TableColumn<Book, Integer> colQuantity;
    @FXML
    private TableColumn<Book, LocalDate> colPublishDate;


    private final IBookService bookService;
    private final IBorrowService borrowService;
    private boolean isAddingCategory = false;
    private boolean isAddingAuthor = false;


    public BookManagementController() {
        this.bookService = new BookServiceImpl(); // dependency injection
        this.borrowService = new BorrowServiceImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { // khoi tao ban dau
        txtCategory.setVisible(false);
        cbCategory.setVisible(true);

        txtAuthor.setVisible(false);
        cbAuthor.setVisible(true);

        loadBooksOnTable();
        initComboBox();
        customDatePicker();

//        btnAdd.setVisible(true);
//
//        btnDelete.setVisible(false);
//        btnUpdate.setVisible(false);

        btnRequestBorrow.setDisable(true);

        txtBookId.setText(bookService.getBookId());

        if (UserContext.getInstance().getRole().equalsIgnoreCase("reader")) {
            setupForReader();
        } else {
            grForReader.setVisible(false);
            btnRequestBorrow.setVisible(false);
        }
    }


    private void setupForReader() {
        btnAdd.setVisible(false);
        btnDelete.setVisible(false);
        btnUpdate.setVisible(false);
        txtAuthor.setVisible(false);
        txtCategory.setVisible(false);
        txtBookId.setVisible(false);
        txtBookName.setVisible(false);
        txtQuantity.setVisible(false);
        dpPublish.setVisible(false);
        cbAuthor.setVisible(false);
        cbCategory.setVisible(false);
        btnAddCategory.setVisible(false);
        btnAddAuthor.setVisible(false);

        lbBookId.setVisible(false);
        lbBookName.setVisible(false);
        lbCategory.setVisible(false);
        lbQuantity.setVisible(false);
        lbPublishDate.setVisible(false);
        lbAuthor.setVisible(false);

        grForReader.setVisible(true);
        btnRequestBorrow.setVisible(true);
        tbBooks.setMinSize(847, 400);

    }


    private void customDatePicker() {
        dpPublish.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isAfter(LocalDate.now()));
            }
        });

        dpReturn.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(LocalDate.now()));
            }
        });
    }

    private void initComboBox() {
        cbCategory.getItems().addAll(bookService.getAllCategoryName());
        bookService.getAllAuthors().forEach(author -> cbAuthor.getItems().add(author.getAuthorName()));
    }

    private void loadBooksOnTable() {
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colAuthorName.setCellValueFactory(new PropertyValueFactory<>("author"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPublishDate.setCellValueFactory(new PropertyValueFactory<>("publisher"));

        tbBooks.setItems(bookService.getAllBook());
    }

    public void fillToTextField(MouseEvent mouseEvent) {
        Optional<Book> tblBook = Optional.ofNullable(tbBooks.getSelectionModel().getSelectedItem());

        tblBook.ifPresent(book -> {
            btnRequestBorrow.setDisable(false);

            txtBookId.setText(book.getBookId());
            txtBookName.setText(book.getBookName());
            cbCategory.setValue(book.getCategory());
            txtQuantity.setText(String.valueOf(book.getQuantity()));
            dpPublish.setValue(book.getPublisher());
            cbAuthor.setValue(book.getAuthor());

//            btnAdd.setVisible(false);
//
//            btnDelete.setVisible(true);
//            btnUpdate.setVisible(true);


        });

    }

    public void onClickAdd(ActionEvent actionEvent) {
        String bookId = txtBookId.getText();
        String bookName = txtBookName.getText();
        String category = txtCategory.getText().isBlank() ? cbCategory.getValue() : txtCategory.getText();
        String quantity = txtQuantity.getText();
        LocalDate publishDate = dpPublish.getValue();
        String author = txtAuthor.getText().isBlank() ? cbAuthor.getValue() : txtAuthor.getText();
        // check null
        if (isNull(bookId, bookName, category, quantity, publishDate, author)) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, "Please fill all fields!");
            return;
        }

        // validate
        if (!isValid("INTEGER_NUMBER", quantity)) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, "Quantity must be an integer!");
            return;
        }


        Book book = Book.builder()
                .bookId(bookId)
                .bookName(bookName)
                .category(category)
                .quantity(Integer.parseInt(quantity))
                .publisher(publishDate)
                .author(author)
                .build();


        try {
            bookService.saveBook(book);

            AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Information", null, "Add new book successfully!");
            tbBooks.setItems(bookService.getAllBook());
            clear();

        } catch (Exception e) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, e.getMessage());
        }


    }

    public void onClickDelete(ActionEvent actionEvent) {
        Optional<Book> selectedBook = Optional.ofNullable(tbBooks.getSelectionModel().getSelectedItem());

        if (selectedBook.isPresent() && AlertUtil.showConfirmation("Are you sure you want to delete?\nThis action cannot be undone!")) {
            bookService.deleteBook(selectedBook.get());
        } else if (selectedBook.isEmpty()) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, "Please select a book to delete!");
        }
        tbBooks.setItems(bookService.getAllBook());
    }

    public void onClickUpdate(ActionEvent actionEvent) {
        Optional<Book> selectedBook = Optional.ofNullable(tbBooks.getSelectionModel().getSelectedItem());

        String bookId = txtBookId.getText();
        String bookName = txtBookName.getText();
        String category = txtCategory.getText().isBlank() ? cbCategory.getValue() : txtCategory.getText();
        String quantity = txtQuantity.getText();
        LocalDate publishDate = dpPublish.getValue();
        String author = cbAuthor.getValue();

        //check null
        if (isNull(bookId, bookName, category, quantity, publishDate, author)) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, "Please fill all fields!");
            return;
        }

        //validate
        if (!isValid("INTEGER_NUMBER", quantity)) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, "Quantity must be an integer!");
            return;
        }

        if (selectedBook.isPresent()) {
            selectedBook.get().setBookName(bookName);
            selectedBook.get().setCategory(category);
            selectedBook.get().setQuantity(Integer.parseInt(quantity));
            selectedBook.get().setPublisher(publishDate);
            selectedBook.get().setAuthor(author);
            bookService.updateBook(selectedBook.get());
            tbBooks.setItems(bookService.getAllBook());
            AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Success", null, "Update book successfully!");
            clear();
        } else {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, "Please select a book to update!");
        }

    }

    public void onClickRefresh(ActionEvent actionEvent) {
        clear();
        tbBooks.setItems(bookService.getAllBook());

        btnRequestBorrow.setDisable(true);

    }

    public void onClickAddCategory(ActionEvent actionEvent) {
        if (isAddingCategory) {
            txtCategory.setVisible(false);
            cbCategory.setVisible(true);
            txtCategory.clear();
            btnAddCategory.setText("Add Category");
            cbCategory.getItems().clear();
            cbCategory.getItems().addAll(bookService.getAllCategoryName());
        } else {
            txtCategory.setVisible(true);
            cbCategory.setVisible(false);
            cbCategory.getSelectionModel().clearSelection();
            btnAddCategory.setText("Cancel");
        }
        isAddingCategory = !isAddingCategory;


    }

    public void onClickAddAuthor(ActionEvent actionEvent) {
        if (isAddingAuthor) {
            txtAuthor.setVisible(false);
            cbAuthor.setVisible(true);
            txtAuthor.clear();
            btnAddAuthor.setText("Add Author");
            cbAuthor.getItems().clear();
            bookService.getAllAuthors().forEach(author -> cbAuthor.getItems().add(author.getAuthorName()));
        } else {
            txtAuthor.setVisible(true);
            cbAuthor.setVisible(false);
            cbAuthor.getSelectionModel().clearSelection();
            btnAddAuthor.setText("Cancel");
        }
        isAddingAuthor = !isAddingAuthor;
    }


    private boolean isNull(Object... objects) {
        for (Object obj : objects) {
            if (obj == null || obj.toString().isBlank()) {
                return true;
            }
        }
        return false;
    }

    private void clear() {
        txtBookId.setText(bookService.getBookId());
        txtBookName.clear();
        txtCategory.clear();
        txtQuantity.clear();
        dpPublish.setValue(null);
        cbAuthor.setValue(null);
        cbCategory.setValue(null);

        txtAuthor.clear();
        txtCategory.clear();

        btnAddAuthor.setText("Add Author");
        btnAddCategory.setText("Add Category");

        txtCategory.setVisible(false);
        txtAuthor.setVisible(false);

        cbCategory.setVisible(true);
        cbAuthor.setVisible(true);

        isAddingAuthor = false;
        isAddingCategory = false;


        tbBooks.getSelectionModel().clearSelection();

//        btnAdd.setVisible(true);
//
//        btnDelete.setVisible(false);
//        btnUpdate.setVisible(false);

    }


    public void onSearch(KeyEvent keyEvent) {
        String keyword = txtSearch.getText();
        if (keyword.isEmpty()) {
            tbBooks.setItems(bookService.getAllBook());
        } else {
            tbBooks.setItems(bookService.getAllBook().filtered(book -> {
                // search by bookId, bookName, author, category
                return book.getBookId().toLowerCase().contains(keyword.toLowerCase()) ||
                        book.getBookName().toLowerCase().contains(keyword.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(keyword.toLowerCase()) ||
                        book.getCategory().toLowerCase().contains(keyword.toLowerCase());
            }));
        }
    }


    public void onClickRequest(ActionEvent actionEvent) {
        Book selectedBook = tbBooks.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            return;
        }

        if (selectedBook.getQuantity() == 0) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, "This book is out of stock!");
            return;
        }


        LocalDate returnDate = dpReturn.getValue();

        if (returnDate == null) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, "Please choose a return date!");
            return;

        }

        try {
            borrowService.requestBorrow(selectedBook.getBookId(), returnDate);
        } catch (Exception e) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, e.getMessage());
            return;
        }
        AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Success", null, "Request borrow successfully!");

    }
}
