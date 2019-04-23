package sample;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.io.*;

public class RulesBox {

    private WebView webView;
    private WebEngine webEngine;
    private boolean open = false;
    private Stage stage;
    private String ENG_RULES = "/text-resources/rules_eng.txt";
    private String PL_RULES = "/text-resources/rules_pl.txt";

    public void display(String title, double x, double y){
        stage = new Stage();
        stage.setX(x);
        stage.setY(y);
        stage.setTitle(title);
        stage.setMaxWidth(500);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> closeWindow());

        this.open = true;

        webView = new WebView();
        webEngine = webView.getEngine();
        webView.setPrefSize(430, 500);

        show("/text-resources/rules-eng.txt");

        Button closeButton = new Button("close");
        closeButton.setOnAction(event -> {
            closeWindow();
        });

        Button plButton = new Button("PL");
        plButton.setOnAction(event -> {
            show("/text-resources/rules-pl.txt");
        });

        Button engButton = new Button("ENG");
        engButton.setOnAction(event -> {
            show("/text-resources/rules-eng.txt");
        });

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(plButton, engButton, closeButton);

        VBox mainBox = new VBox(10);
        mainBox.getChildren().addAll(webView, buttonBox);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setPadding(new Insets(0,5,10,5));
        mainBox.getStyleClass().add("blue-style");
        Scene scene = new Scene(mainBox);
        scene.getStylesheets().add("/sample/styles/boxStyles.css");
        stage.setScene(scene);

        stage.show();
    }

    public void closeWindow(){
        open = false;
        stage.close();
    }

    public boolean isOpen(){
        return open;
    }

    public Stage getStage(){
        return stage;
    }

    private void show(String file){
        StringBuilder sb = new StringBuilder();
        String line = null;
        InputStream in = getClass().getResourceAsStream(file);
        try(BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"))){
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            webEngine.loadContent(sb.toString());
        }catch(IOException ex){
            ex.printStackTrace();
        }

    }

}
