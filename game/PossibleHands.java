package game;

import java.util.ArrayList;

public class PossibleHands {

    /**
     * @param cards
     * @param handsValue
     * @return list of Hand which can be align from specific cards and value greater than handsValue
     */
    public static ArrayList<Hand> getPossibleHands(ArrayList<Card> cards, int handsValue){
        ArrayList<Hand> hands = new ArrayList<>();
        boolean[] handsArray = compute(cards);
        for(int i = 0; i < handsArray.length; i++){
            if(handsArray[i] && Hand.values()[i].getValue() > handsValue) {
                hands.add(Hand.values()[i]);
            }
        }
        return hands;
    }

    public static ArrayList<Hand> getPossibleHands(ArrayList<Card> cards){
        return getPossibleHands(cards,-1);
    }

    private static boolean[] compute(ArrayList<Card> cards){
        int iter = 0;
        boolean[] handsArray = new boolean[83];

        //call for HighCard 0-5
        for(Rank r : Rank.values()){
            handsArray[iter++] = isHighCard(r,cards);
        }
        //call for Pair
        for(Rank r : Rank.values()){
            handsArray[iter++] = isPair(r,cards);
        }
        //call for 2Pair
        for(int i = 1; i < 6; i++){
            for(int j = 0; j < i; j++){
                handsArray[iter++] = is2Pair(Rank.values()[i], Rank.values()[j], cards);
            }
        }
        //call for 3Kind
        for(Rank r : Rank.values()){
            handsArray[iter++] = is3Kind(r,cards);
        }
        //call for Straight
        for(int i = 0; i < 2; i++){
            handsArray[iter++] = isStraight(i,cards);
        }
        //call for Flush
        for(Suit s : Suit.values()){
            handsArray[iter++] = isFlush(s,cards);
        }
        //call FullHause
        for(Rank r : Rank.values()){
            for(Rank p : Rank.values()){
                if(r == p) continue;
                handsArray[iter++] = isFull(r, p,cards);
            }
        }
        //call 4Kind
        for(Rank r : Rank.values()){
            handsArray[iter++] = is4Kind(r,cards);
        }
        //call StraightFlush
        for(Suit s : Suit.values()){
            handsArray[iter++] = isStraightFlush(s, 0,cards);
        }
        //call RoyalFlush
        for(Suit s : Suit.values()){
            handsArray[iter++] = isStraightFlush(s, 1,cards);
        }
        return handsArray;
    }

    private static boolean isHighCard(Rank rank, ArrayList<Card> cards){
        for(Card c : cards){
            if(c.getRank() == rank) return true;
        }
        return false;
    }

    private static boolean isPair(Rank rank,ArrayList<Card> cards){
        int i = 0;
        for(Card c : cards){
            if(c.getRank() == rank) i++;
        }
        return i >= 2 ? true : false;
    }

    private static boolean is2Pair(Rank rank1, Rank rank2, ArrayList<Card> cards){
        if(isPair(rank1,cards) && isPair(rank2,cards)) return true;
        return false;
    }

    private static boolean is3Kind(Rank rank,ArrayList<Card> cards){
        int i = 0;
        for(Card c : cards){
            if(c.getRank() == rank) i++;
        }
        return i >= 3 ? true : false;
    }

    //value can have only 0 or 1
    private static boolean isStraight(int value,ArrayList<Card> cards){
        if(isHighCard(Rank.values()[value++],cards)){
            if(isHighCard(Rank.values()[value++],cards)){
                if(isHighCard(Rank.values()[value++],cards)){
                    if(isHighCard(Rank.values()[value++],cards)){
                        if(isHighCard(Rank.values()[value],cards)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean isFlush(Suit suit, ArrayList<Card> cards){
        int i = 0;
        for(Card c : cards){
            if(c.getSuit() == suit) i++;
        }
        if(i >= 5) return true;
        return false;
    }

    private static boolean isFull(Rank rank1, Rank rank2,ArrayList<Card> cards){
        if(is3Kind(rank1,cards) && isPair(rank2,cards)) return true;
        return false;
    }

    private static boolean is4Kind(Rank rank,ArrayList<Card> cards){
        int i = 0;
        for(Card c : cards){
            if(c.getRank() == rank) i++;
        }
        return i == 4 ? true : false;
    }

    //value can have only 0 or 1
    private static boolean isStraightFlush(Suit suit, int value,ArrayList<Card> cards){
        int i = 0;
        for(Card c : cards){
            if(c.getSuit() == suit){
                if(c.getRank() == Rank.values()[0 + value] || c.getRank() == Rank.values()[1 + value]
                        || c.getRank() == Rank.values()[2 + value] || c.getRank() == Rank.values()[3 + value]
                        || c.getRank() == Rank.values()[4 + value]){
                    i++;
                }
            }
        }
        return i == 5 ? true : false;
    }
}
