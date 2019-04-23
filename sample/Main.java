package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    public void start(Stage primaryStage){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/gameView.fxml"));
            Parent root = loader.load();
            GameViewController mainController = loader.getController();
            Scene scene = new Scene(root, 800, 600);
            primaryStage.setTitle("Collateral Poker");
            primaryStage.getIcons().add(new Image("/image/col.png"));
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setOnCloseRequest(event -> {
                event.consume();
                mainController.closeProgram();
            });
            primaryStage.show();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

    }

    public static void main(String[] args){
        launch(args);
    }

}
