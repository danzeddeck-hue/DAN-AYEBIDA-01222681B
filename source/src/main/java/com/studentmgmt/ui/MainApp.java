package com.studentmgmt.ui;

import com.studentmgmt.util.AppLogger;
import com.studentmgmt.util.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;

/**
 * JavaFX application entry point.
 */
public class MainApp extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        AppLogger.info("Application starting.");

        // Ensure DB is ready
        DatabaseManager.getConnection();

        URL fxml = getClass().getResource("/fxml/Main.fxml");
        if (fxml == null) throw new RuntimeException("Cannot find Main.fxml");

        FXMLLoader loader = new FXMLLoader(fxml);
        Scene scene = new Scene(loader.load(), 1200, 750);

        // Load stylesheet
        URL css = getClass().getResource("/styles.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setTitle("Student Management System Plus  –  Dan Ayebida");
        stage.setScene(scene);
        stage.setMinWidth(950);
        stage.setMinHeight(600);
        stage.show();
    }

    @Override
    public void stop() {
        AppLogger.info("Application closing.");
        AppLogger.close();
        DatabaseManager.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
