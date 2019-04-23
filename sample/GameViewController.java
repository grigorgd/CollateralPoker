package sample;

import client.Client;
import game.Card;
import game.CardImage;
import game.PossibleHands;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import server.Server;
import javafx.scene.media.AudioClip;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import game.Hand;

public class GameViewController implements Initializable {

    private static final String GREEN_STYLE = "-fx-text-fill: #00F000";
    private static final String WHITE_STYLE = "-fx-text-fill: white";
    private static final String SEAT_BUTTON_STYLE = "-fx-background-color: linear-gradient(#8B1222 0.0%, #66122A 50.0%, #661240 100.0%); -fx-text-fill: white; -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7), 2,0,0,2)";
    private static final String BORDER_STYLE = "-fx-border-width: 4px; -fx-border-radius: 10.0; -fx-border-color: linear-gradient(#B6001B 0.0%, #D61466 33.0%, #EB0005 66.0%, #9D005D 100.0%)";
    private static final String CLIP_SOURCE = "/sounds/button.mp3";

    private static AudioClip buttonClip;

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    private Map<String, String> users;
    private ArrayList<String> history;
    private boolean seatTakenByClient = false;
    private Hand bet = null;
    private Set<Card> allCards;
    private ArrayList<Card> playerCards;
    private boolean gameON = false;

    private Server server;
    private Client client;
    boolean connection = false;
    private Thread listen;
    private boolean runningClient = false;
    private boolean runningServer = false;

    @FXML private AnchorPane mainAnchor;
    @FXML private Label infoConnectionLabel, infoServerLabel;
    @FXML private MenuItem createMenuItem, joinMenuItem, closeClientMenuItem, infoMenuItem, exitMenuItem, rulesMenuItem, probabilityMenuItem;
    @FXML private CheckMenuItem deckMenuItem, soundMenuItem;
    @FXML private ListView<Hand> handsList;
    private ObservableList<Hand> handsListData = FXCollections.observableArrayList();
    @FXML private Button callButton, raiseButton;
    @FXML private Button playButton;
    @FXML private Label betLabel;

    @FXML private MenuItem backMenuItem;

    private CreateServerController createServerController;
    private JoinServerController joinServerController;

    private InfoBox infoBox;
    private RulesBox rulesBox;
    private ProbabilityBox probabilityBox;

    private Seat[] seats;
    private Seat seat1, seat2, seat3, seat4;

    public GameViewController(){

        users = new HashMap<>();

        history = new ArrayList<>();

        allCards = new HashSet<>();
        playerCards = new ArrayList<>();

        seats = new Seat[4];
        seat1 = new Seat("0");
        seat2 = new Seat("1");
        seat3 = new Seat("2");
        seat4 = new Seat("3");
        seats[0] = seat1;
        seats[1] = seat2;
        seats[2] = seat3;
        seats[3] = seat4;

    }

    @FXML
    public void initialize(URL url, ResourceBundle rb){

        mainAnchor.getChildren().addAll(seat1, seat2, seat3, seat4);
        seat1.setLayoutX(20);
        seat1.setLayoutY(40);
        seat2.setLayoutX(280);
        seat2.setLayoutY(40);
        seat3.setLayoutX(280);
        seat3.setLayoutY(380);
        seat4.setLayoutX(20);
        seat4.setLayoutY(380);

        infoBox = new InfoBox();
        rulesBox = new RulesBox();
        probabilityBox = new ProbabilityBox();

        playButton.setOnAction(event -> {
            buttonClick();
            client.send(("ready%" + client.getID()).getBytes());
            playButton.setVisible(false);
        });

        callButton.setOnAction(event -> {
            buttonClick();
            callButton.setDisable(true);
            raiseButton.setDisable(true);
            client.send(("call%" + client.getID()).getBytes());
        });

        raiseButton.setOnAction(event -> {
            buttonClick();
            callButton.setDisable(true);
            raiseButton.setDisable(true);
            Hand hand = handsList.getSelectionModel().getSelectedItem();
            client.send(("raise%" + client.getID() + "%" + hand.name()).getBytes());
        });

        initializeCreateGame();
        initializeJoinGame();
        initializeCloseClientGame();
        initializeInfoGame();
        initializeExitGame();
        initializeProbabilityHelp();
        initializeRulesHelp();
        initializeBackOption();

    }

    @FXML
    private void initializeCreateGame(){
        createMenuItem.setOnAction(event -> {
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("createServer.fxml"));
                Parent root = (Parent)loader.load();
                createServerController = (CreateServerController)loader.getController();
                createServerController.injectMainController(this);
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setX(getMainLayoutX() + 260);
                stage.setY(getMainLayoutY() + 180);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.showAndWait();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        });
    }

    @FXML
    private void initializeJoinGame(){
        joinMenuItem.setOnAction(event -> {
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("joinServer.fxml"));
                Parent root = (Parent)loader.load();
                joinServerController = (JoinServerController)loader.getController();
                joinServerController.injectMainController(this);
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setX(getMainLayoutX() + 260);
                stage.setY(getMainLayoutY() +  80);
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.showAndWait();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        });
    }

    @FXML
    private void initializeCloseClientGame(){
        closeClientMenuItem.setDisable(true);
        closeClientMenuItem.setOnAction(event -> {
            boolean answer = ConfirmBox.display("Close client", "Are you sure want to disconnect?", getMainLayoutX() + 260, getMainLayoutY() + 200);
            if(answer){
                if(infoBox.isOpen()) infoBox.closeWindow();
                if(!runningServer) {
                    history.clear();
                    users.clear();
                }
                closeClient();
                closeClientMenuItem.setDisable(true);
                joinMenuItem.setDisable(false);
                clearUserInterface();
                infoConnectionLabel.setText("");
            }
        });
    }

    @FXML
    private void initializeInfoGame(){
        infoMenuItem.setOnAction(event -> {
            if(infoBox.isOpen()){
                infoBox.getStage().toFront();
            }
            else
                infoBox.display(users, history, getMainLayoutX() + 100, getMainLayoutY() + 100);
        });
    }

    @FXML
    private void initializeExitGame(){
        exitMenuItem.setOnAction(event -> closeProgram());
    }

    @FXML
    private void initializeBackOption(){
        backMenuItem.setOnAction(event -> CardImage.setBack());
    }

    @FXML
    private void initializeProbabilityHelp(){
        probabilityMenuItem.setOnAction(event -> {
            probabilityBox.display(playerCards, 100, 100);
        });
    }

    @FXML
    private void initializeRulesHelp(){
        rulesMenuItem.setOnAction(event -> {
            if(rulesBox.isOpen()){
                rulesBox.getStage().toFront();
            }
            else
                rulesBox.display("Rules", getMainLayoutX() + 150, getMainLayoutY() + 50);
        });
    }

    public void closeProgram(){
        String message = "";
        if(runningServer) message += "If you close window, current game will be lost and all users will be disconnected.\n";
        message += "Are you sure want to close server?";
        boolean answer = ConfirmBox.display("Exit", message, getMainLayoutX() + 100, getMainLayoutY() + 200);
        if(answer){
            if(runningServer) server.quit();
            else if(runningClient){
                closeClient();
            }
            Platform.exit();
        }
    }

    private void closeClient(){
        client.send(("dis%" + client.getID() + "%" + client.getName()).getBytes());
        runningClient = false;
        client.close();
    }

    public void createServer(int port){
        this.server = new Server(port);
        runningServer = true;
        String address = "";
        try{
            address = Inet4Address.getLocalHost().getHostAddress();
        }catch(UnknownHostException ex){
        }
        infoServerLabel.setText("server " + address + ":" + port + " started at " + LocalTime.now().format(dtf));
        createMenuItem.setDisable(true);
    }

    public void createClient(String name, String address, int port){
        if(runningClient) client.send(("dis%" + client.getID() + "%" + client.getName()).getBytes());
        this.client = new Client(name, address, port);
        connection = client.openConnection(address);
        if(connection){
            runningClient = true;
            listen();
            String string = "connect%" + client.getName();
            client.send(string.getBytes());
        }
        else{
            infoConnectionLabel.setText("connection failed " + LocalTime.now().format(dtf));
        }
    }

    //////////////////////
    //LISTEN FROM SERVER//
    //////////////////////

    private void listen(){
        listen = new Thread("Listen"){
            public void run(){
                while(runningClient){
                    String message = client.receive().trim();
                    String[] split = message.split("%");

                    //CONNECT
                    //connect%[id] - initial connection and id setting
                    if(message.startsWith("connect")){
                        client.setID(split[1]);
                        setGameON(Boolean.parseBoolean(split[2]));
                        users.put(client.getID(), client.getName());
                        Platform.runLater(() -> {
                            infoConnectionLabel.setText(client.getName() + " connected at " + LocalTime.now().format(dtf));
                            closeClientMenuItem.setDisable(false);
                        });
                    }

                    //USER
                    //user%[id]%[name] - new user info
                    else if(message.startsWith("user")){
                        users.put(split[1], split[2]);
                        Platform.runLater(() -> {
                            if(infoBox.isOpen())
                                infoBox.setUsersBox(users);
                            console("-" + client.getName() + " has joined");
                        });
                    }

                    //SEAT
                    //seat%[clientID]%[seatID] - seat is free to take
                    else if(message.startsWith("seat")){
                        int seatID = Integer.parseInt(split[3]);
                        //enable all free seats
                        if(split[1].equals("00000") && !getGameON()){
                            Platform.runLater(() -> {
                                seats[seatID].seatButton.setDisable(false);
                            });
                        }
                        //seats client
                        else if(split[1].equals(client.getID())){
                            Platform.runLater(() -> {
                                for(Seat s : seats) s.seatButton.setDisable(true);
                                playButton.setVisible(true);
                                seatTakenByClient = true;
                                seats[seatID].setPlayer(client.getID(), client.getName());
                                seats[seatID].seatButton.setDisable(false);
                                seats[seatID].seatButton.setText("leave seat");
                                console("-you have taken seat " + seatID);
                            });
                        }
                        //seats user
                        else{
                            Platform.runLater(() -> {
                                Boolean ready = Boolean.parseBoolean(split[4]);
                                if(ready) seats[seatID].nameLabel.setStyle(GREEN_STYLE);
                                seats[seatID].setPlayer(split[1], split[2]);
                                seats[seatID].seatButton.setVisible(false);
                                console("-" + split[2] + " has taken seat " + seatID);
                            });
                        }
                    }

                    //LEAVE
                    //leave%[seatID] - client leave seat
                    else if(message.startsWith("leave")){
                        for(Seat s : seats){
                            s.nameLabel.setStyle(WHITE_STYLE);
                        }
                        int seatID = Integer.parseInt(split[2]);
                        if(split[1].equals(client.getID())) {
                            Platform.runLater(() -> {
                                seats[seatID].removePlayer();
                                playButton.setVisible(false);
                                seatTakenByClient = false;
                                for(Seat s : seats)
                                    s.seatButton.setDisable(false);
                                console("-you have left seat " + seatID);
                            });
                        }
                        else{
                            Platform.runLater(() -> {
                                seats[seatID].removePlayer();
                                seats[seatID].seatButton.setVisible(true);
                                if(seatTakenByClient)
                                    playButton.setVisible(true);
                                console("-" + users.get(split[1]) + " has left seat " + seatID);
                            });
                        }
                    }
                    //ready%[id] - player ready do play
                    else if(message.startsWith("ready")){
                        for(Seat s : seats){
                            if(split[1].equals(s.playerID)){
                                s.nameLabel.setStyle(GREEN_STYLE);
                            }
                        }
                    }

                    //START
                    //start% - game start, buttons blocked
                    else if(message.startsWith("start")){
                        Platform.runLater(() -> {
                            for(Seat s : seats){
                                s.seatButton.setVisible(false);
                                s.lastBetLabel.setVisible(true);
                                s.lastBetLabel.toFront();
                                s.nameLabel.setStyle(WHITE_STYLE);
                            }
                            setHandList(-1);
                            handsList.setDisable(false);
                        });

                    }

                    //DISCONNECTED
                    //dis%[userID]%[userName] - user disconnected
                    else if(message.startsWith("dis")){
                        String name = users.get(split[1]);
                        users.remove(split[1]);
                        Platform.runLater(() -> {
                            if(infoBox.isOpen())
                                infoBox.setUsersBox(users);
                            console("-" + name + " disconnected");
                        });
                        if(client.getID().equals(split[1]))
                            closeClientMenuItem.setDisable(true);
                    }

                    //RESPOND
                    //res - respond on server call
                    else if(message.startsWith("res")){
                        client.send(("res%" + client.getID()).getBytes());
                    }

                    //NUMBER OF CARDS
                    //hands%[playerID]%[cardsNumber] - send info about players cards
                    else if(message.startsWith("hands")){
                        for(Seat s : seats){
                            if(split[1].equals(s.getPlayerID()) && !split[1].equals(client.getID())){
                                Platform.runLater(() -> {
                                    s.setBack(Integer.parseInt(split[2]));
                                    s.lastBetLabel.setText("");
                                });
                            }
                        }
                    }

                    //PLAYER HAND
                    //cards%[?clientID?]%[cardString]%... - string with client cards
                    else if(message.startsWith("cards")){
                        for(Seat s : seats){
                            if(split[1].equals(s.getPlayerID())){
                                String[] cardsArray = Arrays.copyOfRange(split, 2, split.length);
                                //creating list for probability box
                                if(split[1].equals(client.getID())){
                                    for(String st : cardsArray){
                                        playerCards.add(new Card(st));
                                    }
                                }
                                //creating set for overview box
                                for(String st : cardsArray){
                                    allCards.add(new Card(st));
                                }
                                Platform.runLater(() -> {
                                    s.setFront(cardsArray);
                                    s.lastBetLabel.setText("");
                                });
                            }
                        }
                    }

                    //TURN
                    //turn%[playerID] - info about player turn
                    else if(message.startsWith("turn")){
                        Platform.runLater(() -> {
                            turnHandle(split[1]);
                        });
                    }

                    //RAISE
                    //raise%[playerID]%[Hand]%[turnID] - raise info
                    else if(message.startsWith("raise")){
                        Platform.runLater(() -> {
                            setBet(Hand.valueOf(split[2]));
                            setHandList(Hand.valueOf(split[2]).ordinal());
                            betLabel.setText(getBet().name());
                            for(Seat s : seats){
                                if(s.playerID.equals(split[1])){
                                    s.lastBetLabel.setText(split[2]);
                                }
                            }
                            turnHandle(split[3]);
                            console("+" + users.get(split[1]) + " has raised to " + split[2]);
                        });
                    }

                    //END OF ROUND
                    //round%[winnerID]%[looserID]%[option]%[caller] - confirm round result and continue game
                    else if(message.startsWith("round")){

                        //secure dis
                        int betInt;
                        String betN;
                        if(getBet() != null){
                            betInt = getBet().getValue();
                            betN = getBet().name();
                        }
                        else{
                            betInt = -1;
                            betN = "no bet";
                        }

                        //get possible higher hands
                        String higherHands = PossibleHands.getPossibleHands(new ArrayList<>(allCards), betInt).toString();

                        Platform.runLater(() -> {
                            betLabel.setText("");
                            setBet(null);
                            allCards.clear();
                            playerCards.clear();
                            if(split[3].equals("0")){
                                console("+" + users.get(split[1]) + " won round,\n " + users.get(split[2]) + " lost round and received extra card");
                            }
                            else if(split[3].equals("1") || split[3].equals("3")) {
                                for(Seat s : seats){
                                    if(client.getID().equals(split[2])) seatTakenByClient = false;
                                    if(s.playerID.equals(split[2])){
                                        s.anchorPane.getChildren().clear();
                                        s.anchorPane.getChildren().add(new CardImage("LOST"));
                                        s.lastBetLabel.setText("");
                                    }
                                }
                                if(split[3].equals("1")){
                                    console("+" + users.get(split[1]) + " won round,\n " + users.get(split[2]) + " lost round and drop out the game");
                                }
                                else{
                                    console("+" + split[1] + " drop out the game");
                                }
                            }
                            else if(split[3].equals("2")){
                                for(Seat s : seats){
                                    s.anchorPane.getChildren().clear();
                                    s.clearSeat();
                                    setGameON(false);
                                }
                                console("+" + users.get(split[1]) + " won the game");
                            }
                        });

                        boolean response;
                        if(seatTakenByClient){//client have to be a player to get alert message
                            StringBuilder sb = new StringBuilder();
                            if(split[3].equals("0")){//if no one dropped out
                                response = true;
                                if(client.getID().equals(split[1])){//if player won
                                    sb.append("You have won this round\n" + users.get(split[2]) + " will get extra card next round");
                                }
                                else if(client.getID().equals(split[2])){//if player loose
                                    sb.append("You have lost and you will get extra card next round");
                                }
                                else{
                                    sb.append(users.get(split[1]) + " has won round\n" + users.get(split[2]) + " has lost round");
                                }
                            }
                            else if(split[3].equals("1")){//if player dropped out the game
                                if(client.getID().equals(split[1])){//if player won
                                    sb.append("You have won this round\n" + users.get(split[2]) + " dropped out of the game");
                                    response = true;
                                }
                                else if(client.getID().equals(split[2])){//if player loose
                                    sb.append("You have lost the game");
                                    response = false;
                                }
                                else{
                                    sb.append(users.get(split[1]) + " has won round\n" + users.get(split[2]) + " dropped out of the game");
                                    response = true;
                                }
                            }
                            else if(split[3].equals("2")){
                                response = false;
                                if(client.getID().equals(split[1])){//if player won
                                    sb.append("Congratulations!!!\nYou have won the whole game");
                                }
                                else{//if player loose
                                    sb.append("You have lost the game and the game has been won by " + users.get(split[1]));
                                }
                            }
                            else{
                                response = true;
                                sb.append(split[1] + " disconnected. Next round will start without " + split[1]);
                            }
                            Platform.runLater(() -> {
                                AlertBox.display("Round overview", sb.toString() + "\nbet: " + betN + "\nhigher hands: " + higherHands, client, response, getMainLayoutX(), getMainLayoutY());
                            });
                        }
                    }
                }
            }
        };
        listen.start();
    }

    private void turnHandle(String id){
        for(Seat s : seats){
            if(s.getPlayerID().equals(id)) s.setStyle(BORDER_STYLE);
            else s.setStyle("-fx-border-width: 0");
        }
        if(client.getID().equals(id)){
            //disable callButton when there was no bet yet or previous player bet highest hand in game
            callButton.setDisable(getBet() == null);
            raiseButton.setDisable(false);
            if(getBet() != null) raiseButton.setDisable(getBet().getValue() == 73);
        }
        else{
            callButton.setDisable(true);
            raiseButton.setDisable(true);
        }
    }

    ///////////////////////
    //////////SEAT/////////
    ///////////////////////

    class Seat extends AnchorPane {

        private String seatID = null;
        private Label nameLabel;
        private Button seatButton;
        private Label lastBetLabel;
        private String playerID = "00000";
        private AnchorPane anchorPane;

        public Seat(String seatID){
            this.seatID = seatID;
            this.setPrefSize(230, 180);
            this.setMaxSize(230, 180);

            nameLabel = new Label("");
            nameLabel.setFont(new Font("Consolas", 16));
            nameLabel.setStyle(WHITE_STYLE);
            nameLabel.setMaxWidth(210);
            nameLabel.setPrefWidth(210);
            nameLabel.setLayoutX(10);
            nameLabel.setLayoutY(5);
            nameLabel.setAlignment(Pos.CENTER);
            nameLabel.setTextAlignment(TextAlignment.CENTER);
            this.getChildren().add(nameLabel);

            anchorPane = new AnchorPane();
            anchorPane.setPrefSize(220, 110);
            anchorPane.setLayoutX(5);
            anchorPane.setLayoutY(30);
            this.getChildren().add(anchorPane);

            seatButton = new Button();
            seatButton.setFont(new Font("Consolas", 12));
            seatButton.setStyle(SEAT_BUTTON_STYLE);
            seatButton.setLayoutX(65);
            seatButton.setLayoutY(150);
            seatButton.setPrefWidth(100);
            seatButton.setPrefHeight(25);
            seatButton.setText("take a seat");
            seatButton.setDisable(true);

            this.getChildren().add(seatButton);

            lastBetLabel = new Label("");
            lastBetLabel.setFont(new Font("Consolas", 12));
            lastBetLabel.setStyle(WHITE_STYLE);
            lastBetLabel.setPrefWidth(210);
            lastBetLabel.setAlignment(Pos.CENTER);
            lastBetLabel.setTextAlignment(TextAlignment.CENTER);
            lastBetLabel.setLayoutX(10);
            lastBetLabel.setLayoutY(150);
            lastBetLabel.setVisible(false);
            this.getChildren().add(lastBetLabel);

            seatButton.setOnAction(event -> {
                buttonClick();
                if(!playerID.equals("00000")){//if seat is taken by client sent leave info to server
                    String string = "leave%" + client.getID() + "%" + this.seatID;
                    client.send(string.getBytes());
                }
                //
                else{//if seat is not taken by client sent take a seat  info to server
                    String string = "seat%" + client.getID() + "%" + this.seatID;
                    client.send(string.getBytes());
                }
            });
        }

        private void setBack(int cardsNumber){
            anchorPane.getChildren().clear();
            for(int i = 0; i < cardsNumber; i++){
                anchorPane.getChildren().add(new CardImage(74 + ((i - cardsNumber / 2) * 30)));
            }
        }

        private void setFront(String[] cards){
            anchorPane.getChildren().clear();
            for(int i = 0; i < cards.length; i++){
                anchorPane.getChildren().add(new CardImage(new Card(cards[i]), 74 + ((i - cards.length / 2) * 30), deckMenuItem.isSelected()));
            }
        }

        private void setPlayer(String id, String name){
            this.nameLabel.setText(name);
            this.playerID = id;
        }

        private String getPlayerID(){
            return playerID;
        }

        private void removePlayer(){
            playerID = "00000";
            this.nameLabel.setText("");
            this.seatButton.setText("take a seat");
        }

        private void clearSeat(){
            this.playerID = "00000";
            setStyle("-fx-border-width: 0");
            nameLabel.setText("");
            nameLabel.setStyle(WHITE_STYLE);
            anchorPane.getChildren().clear();
            seatButton.setDisable(false);
            seatButton.setVisible(true);
            lastBetLabel.setText("");
            lastBetLabel.setVisible(false);
            lastBetLabel.toBack();
        }

    }


    /////////////////////
    //////HAND_LIST//////
    /////////////////////
    //populating hands list with hands ranked higher than current bet
    private void setHandList(int betOrdinal){
        handsListData.clear();
        if(betOrdinal >= 0){
            int value = Hand.values()[betOrdinal].getValue();
            for(Hand h : Hand.values()){
                if(h.getValue() > value){
                    handsListData.add(h);
                }
            }
        }
        else{
            handsListData.addAll(new ArrayList<>(EnumSet.allOf(Hand.class)));
        }
        handsList.setItems(handsListData);
        handsList.getSelectionModel().selectFirst();
    }


    /////////////////////
    ///////HISTORY///////
    /////////////////////

    private void console(String message) {
        history.add(message);
        if(infoBox.isOpen()){
            Platform.runLater(() -> {
                infoBox.addMessage(message);
            });
        }
    }

    private void setBet(Hand hand){
        this.bet = hand;
    }

    private Hand getBet(){
        return this.bet;
    }

    private double getMainLayoutX(){
        return mainAnchor.getScene().getWindow().getX();
    }

    private double getMainLayoutY(){
        return mainAnchor.getScene().getWindow().getY();
    }

    private void clearUserInterface(){
        for(Seat s : seats){
            s.clearSeat();
            s.seatButton.setDisable(true);
            setHandList(-1);
            handsList.setDisable(true);
            callButton.setDisable(true);
            raiseButton.setDisable(true);
        }
    }

    private void buttonClick(){
        if(soundMenuItem.isSelected()){
            URL res = getClass().getResource(CLIP_SOURCE);
            if(res != null){
                buttonClip = new AudioClip(res.toString());
                buttonClip.play();
            }
        }

    }

    private void setGameON(boolean gameON){
        this.gameON = gameON;
    }

    private boolean getGameON(){
        return gameON;
    }

}

