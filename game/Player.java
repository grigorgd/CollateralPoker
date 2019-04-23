package game;

import java.util.ArrayList;

public class Player {

    private String id = "";
    private int seat;
    private int size;
    private ArrayList<Card> cards;

    public Player(String id, int seat){
        this.id = id;
        this.seat = seat;
        size = 1;
        cards = new ArrayList<>();
    }

    public int getSize(){
        return size;
    }

    public void increaseSize(){
        size++;
    }

    public ArrayList<Card> getCards(){
        return cards;
    }

    public String getCardsString(){
        StringBuilder sb = new StringBuilder();
        for(Card c : cards){
            sb.append("%");
            sb.append(c);
        }
        return sb.toString();
    }

    public void addCard(Card card){
        cards.add(card);
    }

    public void clear(){
        cards.clear();
    }

    public String getID(){
        return id;
    }

    public int getPosition(){
        return seat;
    }
}
