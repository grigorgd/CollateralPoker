package server;

import game.Game;
import game.Hand;
import game.Player;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

public class Server implements Runnable{
    private Map<String, ServerClient> clients = new HashMap<>();
    private Set<String> userResponse = new HashSet<>();
    private Game game;

    private int port;
    private DatagramSocket socket;
    private Thread run, send, receive, manage;
    private boolean running = false;
    private final int ATTEMPTS = 5;//if attempts more than 5 user is disconnected

    private String[] seats = {"00000", "00000", "00000", "00000"};
    private boolean[] ready = new boolean[4];

    private boolean gameON = false;

    public Server(int port){
        this.port = port;
        try{
            socket = new DatagramSocket(port);
        }catch(SocketException ex){
            ex.printStackTrace();
            return;
        }
        run = new Thread(this, "Server");
        run.start();
    }

    public void run(){
        this.running = true;
        manageDisconnection();
        receive();
    }

    private void receive() {
        receive = new Thread("Receive") {
            public void run() {
                while (running) {
                    byte[] data = new byte[256];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    try {
                        socket.receive(packet);
                    }
                    catch(SocketException ex){
                        ex.printStackTrace();
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    process(packet);
                }
            }
        };
        receive.start();
    }

    private void manageDisconnection(){
        manage = new Thread("Manage"){
            public void run(){
                while(running){
                    //check if user respond
                    sendToAll("res");
                    try{
                        Thread.sleep(3000);
                    }catch (InterruptedException ex){
                        ex.printStackTrace();
                    }
                    for(Map.Entry<String, ServerClient> sc : clients.entrySet()){
                        if(!userResponse.contains(sc.getKey())){
                            if(sc.getValue().getAttempt() > ATTEMPTS){
                                manageDisconnected(sc.getKey(), sc.getValue().getName());
                            }else{
                                sc.getValue().increaseAttempt();
                            }
                        }
                        else{
                            userResponse.remove(sc.getKey());
                            sc.getValue().clearAttempt();
                        }
                    }
                }
            }
        };
        manage.start();
    }

    //PROCESS
    private void process(DatagramPacket packet) {
        String string = new String(packet.getData()).trim();
        String[] split = string.split("%");

        //CONNECT
        //connect%[userName]%[gameON]
        if (string.startsWith("connect")) {
            int id = Identifier.getIdentifier();//create new user identifier

            send(("connect%" + id + "%" + gameON).getBytes(), packet.getAddress(), packet.getPort());//sending id to user

            for(Map.Entry<String, ServerClient> entry : clients.entrySet()){
                //sending id's and name's of all users to new user
                send(("user%" + entry.getKey() + "%" + entry.getValue().getName()).getBytes(), packet.getAddress(), packet.getPort());
                //sending name and id of new user to all users
                send(("user%" + id + "%" + split[1]).getBytes(), entry.getValue().getAddress(), entry.getValue().getPort());
            }
            //put new client in hash map
            clients.put("" + id, new ServerClient(split[1], packet.getAddress(), packet.getPort()));
            //send info about seats taken
            for(int i = 0; i < seats.length; i++){
                String userName = (seats[i].equals("00000"))?"00000":clients.get(seats[i]).getName();
                send(("seat%" + seats[i] + "%" + userName + "%" + i + "%" + ready[i]).getBytes(), packet.getAddress(), packet.getPort());
            }
            //if game is running send status of a game
            if(gameON){
                for(Player player : game.getPlayers()){
                    send(("hands%" + player.getID() + "%" + player.getSize()).getBytes(), packet.getAddress(), packet.getPort());
                }
            }
        }

        //SEAT
        else if (string.startsWith("seat")) {
            int seatID = Integer.parseInt(split[2]);
            if(seats[seatID].equals("00000")){//take a seat when its free
                seats[seatID] = split[1];//assign userID to seat
                sendToAll("seat%" + split[1] + "%" + clients.get(split[1]).getName() + "%" + seatID + "%" + ready[seatID]);//inform all users
            }

        }

        //LEAVE
        else if (string.startsWith("leave")) {
            if(!gameON){//blocking players against leaving seats when game started
                int seatID = Integer.parseInt(split[2]);
                seats[seatID] = "00000";//leave seat
                Arrays.fill(ready, false);//if someone leaves seat, players must click ready again to start play
                sendToAll("leave%" + split[1] + "%" + seatID);
            }
        }

        //READY
        else if (string.startsWith("ready")){
            for(int i = 0; i < seats.length; i++){
                if(seats[i].equals(split[1]))
                    ready[i] = true;
            }
            if(gameReady()){//at least 2 users ready on seats, and no unready users on seat
                game = new Game();
                gameON = true;
                for(int i = 0; i < seats.length; i++){
                    if(!seats[i].equals("00000") && ready[i]){
                        game.addPlayer(seats[i], i);
                    }
                }
                sendToAll("start");
                nextRound();
            }
            else{//if another player ready inform players
                sendToAll(string);
            }
        }


        //RAISE
        else if(string.startsWith("raise")){
            game.raise(Hand.valueOf(split[2]));
            //send raise and turn in one message to avoid receiving turn info first by user
            sendToAll(string + "%" + game.getCurrentPlayer().getID());
        }

        //CALL
        else if(string.startsWith("call")){
            //round%[winnerID]%[looserID]%[option]%[currentPlayer]
            // option == 0 - no one drop out
            // option == 1 - looser drop out and game is on
            // option == 2 - winner won game
            String[] result = game.call();

            //send to all info about all players cards
            for(Player pl : game.getPlayers()) {
                sendToAll("cards%" + pl.getID() + pl.getCardsString());
            }

            if(result[2].equals("2")){
                Arrays.fill(seats, "00000");
                Arrays.fill(ready, false);
                gameON = false;
            }
            else if(result[2].equals("1")){
                for(int i = 0; i < seats.length; i++){
                    if(seats[i].equals(result[1])){//if player lost remove him from confirm decision 'ok'
                        seats[i] = "00000";
                        ready[i] = true;
                    }
                }
            }
            try{
                Thread.sleep(1000);
            }catch(InterruptedException ex){
                ex.printStackTrace();
            }
            sendToAll("round%" + result[0] + "%" + result[1] + "%" + result[2]);
        }

        //END ROUND CONFIRM
        else if (string.startsWith("ok")){
            for(int i = 0; i < seats.length; i++){
                if(seats[i].equals(split[1]))
                    ready[i] = true;
            }
            if(gameReady()){
                sendToAll("start");
                nextRound();
            }
            else{
                for(Player pl : game.getPlayers()){
                    send("ready%" + split[1], pl.getID());
                }
            }
        }

        //RESPONSE
        else if(string.startsWith("res")){
            userResponse.add(split[1]);
        }

        //DISCONNECT
        else if (string.startsWith("dis")) {
            manageDisconnected(split[1], split[2]);
        }
    }

    //inform all about disconnected player, when connection lost or player disconnected himself
    private void manageDisconnected(String id, String name){
        //if game has already started, check that user who left was player
        boolean playerDis = false;
        for(int i = 0; i < seats.length; i++){
            clients.remove(id);
            if(id.equals(seats[i])){
                playerDis = true;
                seats[i] = "00000";
                ready[i] = true;
                if(!gameON) sendToAll("leave%" + id + "%" + i);
            }
        }
        sendToAll("dis%" + id + "%" + name);

        if(gameON && playerDis){
            String winner = "";
            if(game.getPlayers().size() == 2){
                //end game, inform all
                for(Player pl : game.getPlayers()){
                    if(!pl.getID().equals(id)) winner = pl.getID();
                }
                //round%[winnerID]%[looserID]%[mode]%[looserName]
                sendToAll("round%" + winner + "%" + id + "%2");
                //clear game
                Arrays.fill(seats, "00000");
                Arrays.fill(ready, false);
                gameON = false;
            }
            else{
                //game continues, new round inform all
                sendToAll("round%" + name + "%" + id + "%3");
                //remove player
                game.removeP(id);
            }
        }
    }


    //NEXT ROUND
    private void nextRound(){

        //preparing ready table for next round
        for(int i = 0; i < ready.length; i++)
            ready[i] = (seats[i].equals("00000"))?true:false;

        game.prepare();
        for(Player pl : game.getPlayers()){
            //send info to everyone about number of cards of each player
            sendToAll("hands%" + pl.getID() + "%" + pl.getSize());
            //send info to each player about cards they get
            send("cards%" + pl.getID() + pl.getCardsString(), pl.getID());
        }
        //send playerID to all users about player turn
        sendToAll("turn%" + game.getCurrentPlayer().getID());
    }

    //READY
    private boolean gameReady(){
        int p = 0;
        for(int i = 0; i < seats.length; i++){
            if(!seats[i].equals("00000")){
                if(ready[i]) p++;
                else{
                    p = 0;
                    break;
                }
            }
        }
        if(p >= 2){
            return true;
        }
        return false;
    }


    //SEND
    private void send(String message, String id){
        send(message.getBytes(), clients.get(id).getAddress(), clients.get(id).getPort());
    }

    private void sendToAll(String message) {
        for(Map.Entry<String, ServerClient> entry : clients.entrySet()){
            send(message.getBytes(), entry.getValue().getAddress(), entry.getValue().getPort());
        }
    }

    private void send(final byte[] data, final InetAddress address, final int port) {
        send = new Thread("Send") {
            public void run() {
                DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }

    //QUIT
    public void quit() {
        for(String key : clients.keySet()){
            clients.remove(key);
        }
        running = false;
        System.out.println("server closed");
        socket.close();
    }


}
