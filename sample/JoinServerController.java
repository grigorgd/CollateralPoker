package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.prefs.Preferences;


public class JoinServerController {
    private GameViewController mainController;
    private final String regName = "\\w{3,20}";
    private final String regIP = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
    private final String regPort = "\\d{4,5}";

    @FXML private TextField nameTextField, ipTextField, portTextField;
    @FXML private Button joinButton, cancelButton;
    @FXML private Label attentionLabel;

    @FXML
    public void initialize(){

        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
        String name = prefs.get("name", "Anon" + (int)(Math.random()*1000));
        String address = "";
        try{
            String ip = InetAddress.getLocalHost().getHostAddress();
            address = prefs.get("address", "" + ip);
        }catch(UnknownHostException ex){
            ex.getCause();
        }
        int clientPort = prefs.getInt("clientPort", 8282);

        nameTextField.setText(name);
        ipTextField.setText(address);
        portTextField.setText("" + clientPort);

        //joining server
        joinButton.setOnAction(event -> {
            if(nameTextField.getText().trim().length() <= 20 && InputChecker.check(regName, nameTextField.getText().trim())){
                if(ipTextField.getText().length() <= 15 && InputChecker.check(regIP, ipTextField.getText().trim())){
                    int portNumber = 0;
                    if(portTextField.getText().trim().length() <= 5 && InputChecker.check(regPort, portTextField.getText().trim())){
                        portNumber = Integer.parseInt(portTextField.getText().trim());
                        if(portNumber >= 1024 && portNumber <= 65535){
                            mainController.createClient(nameTextField.getText(), ipTextField.getText(), Integer.parseInt(portTextField.getText()));
                            attentionLabel.setText("");
                            prefs.put("name", nameTextField.getText().trim());
                            prefs.put("address", ipTextField.getText().trim());
                            prefs.putInt("clientPort", Integer.parseInt(portTextField.getText().trim()));
                            cancelButton.fire();
                        }
                        else{
                            attentionLabel.setText("port - number between 1024 and 65535");
                        }
                    }
                    else{
                        attentionLabel.setText("port - number between 1024 and 65535");
                    }
                }
                else{
                    attentionLabel.setText("ip - 4 numbers divided by dots");
                }
            }
            else{
                attentionLabel.setText("name - 3 to 30 characters, digits or underscore");
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
