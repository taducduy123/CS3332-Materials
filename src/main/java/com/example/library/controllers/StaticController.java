package com.example.library.controllers;

import com.example.library.services.IStaticService;
import com.example.library.services.impl.StaticServiceImpl;
import com.example.library.utils.AlertUtil;
import com.example.library.utils.SettingUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class StaticController implements Initializable {
    @FXML
    private Text txtNumberReader;
    @FXML
    private Text txtTotalBorrow;
    @FXML
    private Text txtTotalQuantityBook;
    @FXML
    private Text txtTotalLate;
    @FXML
    private Text txtNumberReturn;
    @FXML
    private TextArea taNumberReturn;
    @FXML
    private PieChart pieLate;
    private final IStaticService staticService;
    private final SettingUtils settingUtils = SettingUtils.getInstance();

    public StaticController() {
        staticService = new StaticServiceImpl();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtNumberReader.setText(String.valueOf(staticService.getTotalReader()));
        txtTotalBorrow.setText(String.valueOf(staticService.getTotalBorrow()));
        txtTotalQuantityBook.setText(String.valueOf(staticService.getTotalBook()));
        txtTotalLate.setText(String.valueOf(staticService.getTotalLate()));
        txtNumberReturn.setText(String.valueOf(staticService.getTotalReturn()));

        txtNumberReturn.setVisible(false);
        taNumberReturn.setVisible(false);
        drawChart();
    }

    private void drawChart() {
        pieLate.getData().clear();
        pieLate.getData().add(new PieChart.Data("Total Borrow", staticService.getTotalBorrow()));
        pieLate.getData().add(new PieChart.Data("Total Return Late", staticService.getTotalLate()));


    }

    public void onClickReturnOnTime(MouseEvent mouseEvent) {
        if (settingUtils.isHighlightReturn()) {
            if (AlertUtil.showConfirmation("Bạn có muốn tắt tuỳ chọn này không?")) {
                settingUtils.setHighlightReturn(false);
            }
        } else if (AlertUtil.showConfirmation("Bạn có muốn bật tuỳ chọn này không?")) {
            settingUtils.setHighlightReturn(true);
        }
    }

    public void onClickReturnLate(MouseEvent mouseEvent) {
        if (settingUtils.isHighlightLate()) {
            if (AlertUtil.showConfirmation("Bạn có muốn tắt tuỳ chọn này không?")) {
                settingUtils.setHighlightLate(false);
            }
        } else if (AlertUtil.showConfirmation("Bạn có muốn bật tuỳ chọn này không?")) {
            settingUtils.setHighlightLate(true);
        }
    }
}
