package com.example.library;

import com.example.library.models.Account;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    private static Scene scene;
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        scene = new Scene(loadFXML("LoginFrm"), 600, 328);

        primaryStage.setScene(scene);

        primaryStage.setResizable(false);

        centerStage(primaryStage);

        primaryStage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        Parent root = loadFXML(fxml);
        scene.setRoot(root);

        stage.setWidth(root.prefWidth(-1));
        stage.setHeight(root.prefHeight(-1));

        centerStage(stage);
    }

    public static void setRootPop(String fxml, String title, boolean resizable) throws IOException {
        Stage stage = new Stage();
        Scene newScene = new Scene(loadFXML(fxml));
        stage.setResizable(resizable);
        stage.setScene(newScene);
        stage.setTitle(title);

        centerStage(stage);

        stage.showAndWait();
    }

    private static void centerStage(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        double x = (screenBounds.getWidth() - stage.getWidth()) / 2;
        double y = (screenBounds.getHeight() - stage.getHeight()) / 2;

        stage.setX(x);
        stage.setY(y);
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
