package sample;

import game.Card;
import game.Hand;
import game.HandProbability;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ProbabilityBox {
    private Stage stage;
    private TextArea textArea;
    private StringBuilder sb;
    private double[][] prob;

    public void display(ArrayList<Card> cardsArray, double x, double y){
        stage = new Stage();
        stage.setX(x);
        stage.setY(y);
        stage.setMaxWidth(500);
        stage.setMaxHeight(600);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> stage.close());

        textArea = new TextArea();
        textArea.setPrefSize(580, 450);
        textArea.setEditable(false);

        if(cardsArray.size() > 0){
            sb = new StringBuilder();
            prob = new double[5][83];
            for(int i = 0; i < prob.length; i++){
                prob[i] = HandProbability.getProbabilityArray(cardsArray, i + 1);
            }

            for(int j = 0; j < Hand.values().length; j++){
                sb.append(String.format("%-24s%9.2f%9.2f%9.2f%8.2f%9.2f", Hand.values()[j].name(), prob[0][j], prob[1][j], prob[2][j], prob[3][j], prob[4][j]));
                sb.append("\n");
            }
            textArea.setText(sb.toString());
        }

        VBox mainVBox = new VBox(10);
        Label description = new Label("This table shows the probability of having a specific hand with a\ncertain number of cards in relation to the cards held by the user");
        Label desc = new Label(String.format("%-23s%9s%9s%9s%9s%9s", "Hand", "1", "2", "3", "4", "5"));
        desc.setPrefWidth(480);
        mainVBox.getChildren().addAll(description, desc, textArea, closeButton);
        mainVBox.getStyleClass().add("blue-style");
        mainVBox.setAlignment(Pos.CENTER);
        mainVBox.setPadding(new Insets(5,5,10,5));

        Scene scene = new Scene(mainVBox);
        scene.getStylesheets().add("/sample/styles/boxStyles.css");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.showAndWait();
    }

}
