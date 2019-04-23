package sample;

import client.Client;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {
    private static boolean sending = false;

    public static void display(String title, String message, Client client, boolean send, double x, double y){
        sending = send;
        Stage stage = new Stage();
        stage.setX(x + 120);
        stage.setY(y + 200);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setMinWidth(280);
        stage.setMaxWidth(500);
        stage.setResizable(false);

        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(490);
        label.setAlignment(Pos.CENTER);
        Button closeButton = new Button("ok");
        closeButton.setPrefWidth(80);
        closeButton.setOnAction(event -> {
            close(client);
            stage.close();
        });

        VBox vBox = new VBox(10);
        vBox.getChildren().addAll(label, closeButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.getStyleClass().add("blue-style");
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("/sample/styles/boxStyles.css");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> close(client));

        stage.showAndWait();
    }

    private static void close(Client client){
        if(sending) client.send(("ok%" + client.getID()).getBytes());
    }
}
