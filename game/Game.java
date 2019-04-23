package game;

import java.util.ArrayList;

import static game.PossibleHands.*;

public class Game {
    private ArrayList<Player> players;
    private ArrayList<Card> allPlayersCards;
    private Hand bet = null;
    private int turn = 0;

    public Game(){
        players = new ArrayList<>();
        allPlayersCards = new ArrayList<>();
        turn = (int)(Math.random() * 12);
    }

    public void setTurn(){
        this.turn++;
    }

    public ArrayList<Player> getPlayers(){
        return players;
    }

    public void addPlayer(String id, int seat){
        players.add(new Player(id, seat));
    }

    public void prepare(){
        allPlayersCards.clear();
        this.bet = null;
        Deck deck = new Deck();
        deck.shuffle();
        for(Player pl : players){
            pl.clear();
            for(int i = 0; i < pl.getSize(); i++){
                pl.getCards().add(deck.getTop());
            }
            this.allPlayersCards.addAll(pl.getCards());
        }
    }

    public void raise(Hand hand){
        this.bet = hand;
        setTurn();
    }

    public String[] call(){
        String[] result = new String[3];
        if(isBet()){
            getCurrentPlayer().increaseSize();
            result[0] = getPreviousPlayer().getID();
            result[1] = getCurrentPlayer().getID();
            if(getCurrentPlayer().getSize() > 5) result[2] = (players.size() > 2)?"1":"2";
            else result[2] = "0";
        }
        else{
            getPreviousPlayer().increaseSize();
            result[0] = getCurrentPlayer().getID();
            result[1] = getPreviousPlayer().getID();
            if(getPreviousPlayer().getSize() > 5) result[2] = (players.size() > 2)?"1":"2";
            else result[2] = "0";
        }
        removePlayer();
        //result = {winnerID, looserID, 0-no one drop out, 1-player drop out, 2-game ends}
        return result;
    }

    public void removePlayer(){
        Player toRemove = null;
        for(Player player : players){
            if(player.getSize() > 5) {
                toRemove = player;
            }
        }
        players.remove(toRemove);
    }

    public void removeP(String id){
        Player toRemove = null;
        for(Player player : players){
            if(player.getID().equals(id)) {
                toRemove = player;
            }
        }
        players.remove(toRemove);
    }

    private boolean isBet(){
        ArrayList<Hand> hand = new ArrayList<>();
        hand.addAll(getPossibleHands(allPlayersCards));
        for(Hand h : hand){
            if(h.name().equals(bet.name())) return true;
        }
        return false;
    }

    public Player getCurrentPlayer(){
        return players.get(turn % players.size());
    }

    private Player getPreviousPlayer(){
        if(turn % players.size() == 0) return players.get(players.size() - 1);
        return players.get((turn % players.size()) - 1);
    }

}
