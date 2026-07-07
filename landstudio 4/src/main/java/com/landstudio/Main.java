package com.landstudio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.init(primaryStage);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();

        Screen screen = Screen.getPrimary();
        double width = screen.getVisualBounds().getWidth();
        double height = screen.getVisualBounds().getHeight();

        Scene scene = new Scene(root, width, height);
        controller.bindResponsiveSizing(scene);

        primaryStage.setTitle("Land Studio");
        primaryStage.setScene(scene);
        primaryStage.setX(screen.getVisualBounds().getMinX());
        primaryStage.setY(screen.getVisualBounds().getMinY());
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
