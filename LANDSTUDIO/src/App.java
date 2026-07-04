import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));

        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Booking Fotografer");

        stage.setScene(scene);

        stage.setFullScreen(true);

        stage.show();

    }

    public static void main(String[] args) {

        launch(args);

    }

}