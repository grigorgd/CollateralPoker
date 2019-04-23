package game;

/**
 * Object <code>Card</code> represents a card e.g. ACE DIAMONDS
 */
public class Card {

    private Rank rank;
    private Suit suit;

    public Card(String string){
        this.rank = Rank.valueOf(string.split("_")[0]);
        this.suit = Suit.valueOf(string.split("_")[1]);
    }

    public Card(Rank rank, Suit suit){
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank(){
        return rank;
    }

    public Suit getSuit(){
        return suit;
    }

    @Override
    public boolean equals(Object otherObject) {
        if(otherObject == null){
            return false;
        }
        if(otherObject instanceof Card){
            Card otherCard = (Card) otherObject;
            return this.rank == otherCard.rank &&
                    this.suit == otherCard.suit;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 17 * suit.hashCode() + 31 * rank.hashCode();
    }

    @Override
    public String toString(){
        return String.format("%s_%s", rank, suit);
    }

}
