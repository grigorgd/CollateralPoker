package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateServerController{
    private GameViewController mainController;
    private final String regPort = "\\d{4,5}";
    private Pattern pattern;
    private Matcher matcher;

    @FXML
    private TextField portTextField;
    @FXML
    private Button createButton, cancelButton;
    @FXML
    private Label attentionLabel;

    @FXML
    public void initialize(){
        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
        int port = prefs.getInt("serverPort", 8282);
        portTextField.setText("" + port);

        //creating new server
        createButton.setOnAction(event -> {
            int portNumber = 0;
            pattern = Pattern.compile(regPort);
            matcher = pattern.matcher(portTextField.getText().trim());
            if(portTextField.getText().trim().length() <= 5 && matcher.matches()) {
                portNumber = Integer.parseInt(portTextField.getText().trim());
                if(portNumber >= 1024 && portNumber <= 65535){
                    prefs.putInt("serverPort", portNumber);
                    attentionLabel.setText("");
                    mainController.createServer(portNumber);
                    cancelButton.fire();
                }
                else{
                    attentionLabel.setText("port - number between 1024 and 65535");
                }
            }
            else{
                attentionLabel.setText("port - number between 1024 and 65535");
            }
        });

        cancelButton.setOnAction(event -> {
            Stage stage = (Stage) this.cancelButton.getScene().getWindow();
            stage.close();
        });

    }

    public void injectMainController(GameViewController mainController){
        this.mainController = mainController;
    }

}
