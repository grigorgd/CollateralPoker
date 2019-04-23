package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmBox {

    private static boolean answer;

    public static boolean display(String title, String message, double x, double y){
        Stage stage = new Stage();
        stage.setX(x);
        stage.setY(y);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setMinWidth(280);
        stage.setMaxHeight(280);
        Label label = new Label(message);

        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");

        yesButton.setOnAction(event -> {
            answer = true;
            stage.close();
        });

        noButton.setOnAction(event -> {
            answer = false;
            stage.close();
        });

        VBox vBox = new VBox(20);
        vBox.setAlignment(Pos.CENTER);
        HBox hBox = new HBox(20);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPrefWidth(180);
        hBox.getChildren().addAll(yesButton, noButton);
        vBox.getChildren().addAll(label, hBox);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.getStyleClass().add("blue-style");
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("/sample/styles/boxStyles.css");
        stage.setScene(scene);
        stage.showAndWait();

        return answer;
    }
}
