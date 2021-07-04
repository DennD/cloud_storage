package ru.geekbrains.oskin_di;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.geekbrains.oskin_di.util.Config;

public class LoginWindow extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/login_panel.fxml"));
        stage.getIcons().add(new Image("/image/icon.png"));
        stage.setTitle("Portfile");
        stage.setScene(new Scene(root, 600, 600));
        stage.show();
        stage.setResizable(false);
    }
}