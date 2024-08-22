package com.example.library.controllers;

import com.example.library.models.Account;
import com.example.library.services.impl.AccountServiceImpl;
import com.example.library.services.IAccountService;
import com.example.library.utils.AlertUtil;
import com.example.library.utils.UserContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;

public class ChangePasswordController {
    @FXML
    private PasswordField pwCur;
    @FXML
    private PasswordField pwNew;
    @FXML
    private PasswordField pwReEnter;

    private final IAccountService accountService;

    public ChangePasswordController() {
        this.accountService = new AccountServiceImpl();
    }

    public void onClickOk(ActionEvent actionEvent) {
        String oldPassword = pwCur.getText();
        String newPassword = pwNew.getText();
        String reEnterPassword = pwReEnter.getText();

        if (!newPassword.equals(reEnterPassword)) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, "Re-type password not match!");
            return;
        }

        Account account = Account.builder()
                .role(UserContext.getInstance().getRole())
                .password(oldPassword)
                .username(UserContext.getInstance().getUsername())
                .build();

        try {
            accountService.changePassword(account, newPassword);
            AlertUtil.showAlert(Alert.AlertType.INFORMATION, "Information", null, "Change password successfully!");

        } catch (Exception e) {
            AlertUtil.showAlert(Alert.AlertType.ERROR, "Error", null, e.getMessage());

        }
    }
}
