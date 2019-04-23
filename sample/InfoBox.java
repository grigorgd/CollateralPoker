package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Map;

public class InfoBox {

    private TextArea messageArea;
    private Button closeButton, allButton, gameButton, connectButton;
    private ListView<String> userList;
    private ObservableList<String> userListData;
    private boolean open = false;
    private Stage stage;
    private int mode = 0;
    private ArrayList<String> history;

    public void display(Map<String, String> users, ArrayList<String> messages, double x, double y){
        history = new ArrayList<>();
        this.history = messages;
        stage = new Stage();
        stage.setX(x);
        stage.setY(y);
        open = true;
        stage.setOnCloseRequest(event -> closeWindow());

        VBox vBoxLeft = new VBox(5);
        vBoxLeft.setLayoutX(10);
        messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setPrefWidth(300);
        messageArea.setPrefHeight(400);
        messageArea.setWrapText(true);
        Label labelM = new Label("Messages:");
        vBoxLeft.getChildren().addAll(labelM, messageArea);

        VBox vBoxRight = new VBox(5);
        userList = new ListView<>();
        userList.setEditable(false);
        userList.setPrefWidth(100);
        userList.setPrefHeight(400);
        userListData = FXCollections.observableArrayList();
        setUsersBox(users);
        Label labelU = new Label("Users:");
        vBoxRight.getChildren().addAll(labelU, userList);

        HBox hBoxUP = new HBox(10);
        hBoxUP.getChildren().addAll(vBoxLeft, vBoxRight);

        allButton = new Button("All");
        allButton.setOnAction(event -> {
            this.mode = 0;
            setMessageBox(history, 0);
        });

        gameButton = new Button("Game");
        gameButton.setOnAction(event -> {
            this.mode = 1;
            setMessageBox(history, 1);
        });

        connectButton = new Button("Connect");
        connectButton.setOnAction(event -> {
            this.mode = 2;
            setMessageBox(history, 2);
        });

        closeButton = new Button("Close");
        closeButton.setOnAction(event -> closeWindow());

        setMessageBox(history, 0);

        HBox hBoxDOWN = new HBox(10);
        hBoxDOWN.setAlignment(Pos.CENTER_LEFT);
        hBoxDOWN.getChildren().addAll(allButton, gameButton, connectButton, closeButton);

        VBox mainVBox = new VBox(10);
        mainVBox.getChildren().addAll(hBoxUP, hBoxDOWN);
        mainVBox.setPadding(new Insets(5, 0, 0, 10));
        mainVBox.getStyleClass().add("blue-style");

        Scene scene = new Scene(mainVBox);
        scene.getStylesheets().add("/sample/styles/boxStyles.css");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    public void closeWindow(){
        open = false;
        stage.close();
    }

    private void setMessageBox(ArrayList<String> message, int index){
        messageArea.setText("");
        if(index == 0){
            for(String s : message){
                messageArea.appendText(s + "\n");
            }
        }
        else if(index == 1){
            for(String s : message){
                if(s.startsWith("+")) messageArea.appendText(s + "\n");
            }
        }
        else if(index == 2){
            for(String s : message){
                if(s.startsWith("-")) messageArea.appendText(s + "\n");
            }
        }
    }

    public void addMessage(String message){
        if(mode == 0) messageArea.appendText(message);
        else if(mode == 1) if(message.startsWith("+")) messageArea.appendText(message);
        else if(mode == 2) if(message.startsWith("-")) messageArea.appendText(message);
        messageArea.appendText("\n");
    }



    public void setUsersBox(Map<String, String> users){
        userListData.clear();
        userListData.addAll(users.values());
        userList.setItems(userListData);
    }

    public boolean isOpen(){
        return open;
    }

    public Stage getStage(){
        return stage;
    }

}
