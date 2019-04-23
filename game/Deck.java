package game;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Object <code>Deck</code> represents deck of cards (24 cards)
 */
public class Deck {
    private ArrayList<Card> deck;

    public Deck(){
        deck = new ArrayList<>();
        for(Suit s : Suit.values()){
            for(Rank r : Rank.values()){
                deck.add(new Card(r, s));
            }
        }
    }

    public int getSize(){
        return deck.size();
    }

    public void shuffle(){
        Collections.shuffle(deck);
    }

    /**
     *
     * @return first card in deck
     */
    public Card getTop(){
        Card card = deck.get(0);
        deck.remove(0);
        return card;
    }

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        for(Object c : deck){
            result.append(c.toString());
        }
        return result.toString();
    }
}
