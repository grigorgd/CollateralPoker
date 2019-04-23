package game;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * This class calculate probability of Hands from specific player view.
 * It is use to estimate rivals hands by computer players.
 */
public class HandProbability {
    /**
     *
     * @param playerCards - are cards which specific player holds
     * @param rivalsC - all cards held by other players
     * @return mathematical probability of all 83 Hands in game from specific player view
     */
    public static double[] getProbabilityArray(ArrayList<Card> playerCards, int rivalsC){
        double[] result = new double[83];
        int allUnknownC = 24 - playerCards.size();

        //royal [79-82]
        int i = 0;
        int index = 79;
        for(Suit suit : Suit.values()){
            for(Card card : playerCards){
                if(card.getSuit().name().equals(suit.name()) && card.getRank().ordinal() != 0) i++;
            }
            result[index] = probabilityR(allUnknownC,rivalsC,5-i,5-i);
            i = 0;
            index++;
        }

        //straightflush [75-78]
        index = 75;
        for(Suit suit : Suit.values()){
            for(Card card : playerCards){
                if(card.getSuit().equals(suit) && card.getRank().ordinal() != 5) i++;
            }
            result[index] = probabilityR(allUnknownC,rivalsC,5-i,5-i);
            i = 0;
            index++;
        }

        //4Kind [69-74]
        index = 69;
        for(Rank rank : Rank.values()){
            for(Card card : playerCards){
                if(card.getRank().equals(rank)) i++;
            }
            result[index] = probabilityR(allUnknownC,rivalsC,4-i,4-i);
            i = 0;
            index++;
        }

        int j = 0;
        //Full [39-68]
        index = 39;
        for(Rank rank : Rank.values()){
            for (Rank rank1 : Rank.values()){
                if(rank1.equals(rank)) continue;
                for(Card card : playerCards){
                    if(card.getRank().equals(rank)) i++;
                    if(card.getRank().equals(rank1)) j++;
                }
                result[index] = probabilityAdvR(allUnknownC,rivalsC,4-i,3-i,4-j,2-j);
                if(i < 3 && j < 2){
                    result[index] += probabilityAdvR(allUnknownC,rivalsC,4-i,4-i,4-j,2-j);
                }
                i = 0;
                j = 0;
                index++;
            }
        }

        //Flush [35-38]
        index = 35;
        for(Suit suit : Suit.values()){
            for(Card card : playerCards){
                if(card.getSuit().equals(suit)) i++;
            }
            result[index] = probabilityR(allUnknownC,rivalsC,6-i,5-i);
            i = 0;
            index++;
        }

        //Straight [33-34]
        index = 33;
        for(int x = 0; x < 2; x++){
            for(int k = 0 + x; k < 5 + x; k++){
                for(Card card : playerCards){
                    if(card.getRank().ordinal() == k){
                        i++;
                        break;
                    }
                }
            }
            int cut = 0;

            //if cards in hands of all players divide by 4(for cards in specific rank)
            //is greater than different ranks in this player hand
            //there have to be at least allUnknownCards/4 different ranks in all hands
            if((playerCards.size() + rivalsC) / 4.0 > i){
                i = ((playerCards.size() + rivalsC) / 4) - 1;
                cut = i;
            }

            result[index] = probabilityStraight(allUnknownC,rivalsC - cut,5-i);

            i = 0;
            index++;
        }

        //3Kind [27-32]
        index = 27;
        for(Rank rank : Rank.values()){
            for(Card card : playerCards){
                if(card.getRank().equals(rank)) i++;
            }
            result[index] = probabilityR(allUnknownC,rivalsC,4-i,3-i);
            i = 0;
            index++;
        }

        //2Pair [12-26]
        index = 12;
        for(int k = 1; k < 6; k++){
            for (int n = 0; n < k; n++){
                for(Card card : playerCards){
                    if(card.getRank().ordinal() == k) i++;
                    if(card.getRank().ordinal() == n) j++;
                }
                result[index] = probabilityAdvR(allUnknownC,rivalsC,4-i,2-i,4-j,2-j);
                if(i < 2 && j < 2){
                    result[index] += probabilityAdvR(allUnknownC,rivalsC,4-i,3-i,4-j,2-j);
                    result[index] += probabilityAdvR(allUnknownC,rivalsC,4-i,4-i,4-j,2-j);
                }
                i = 0;
                j = 0;
                index++;
            }
        }

        //Pair [6-11]
        index = 6;
        for(Rank rank : Rank.values()){
            for(Card card : playerCards){
                if(card.getRank().equals(rank)) i++;
            }
            result[index] = probabilityR(allUnknownC,rivalsC,4-i,2-i);
            i = 0;
            index++;
        }

        //HighCard [0-5]
        index = 0;
        for(Rank rank : Rank.values()){
            for(Card card : playerCards){
                if(card.getRank().equals(rank)) i++;
            }
            result[index] = probabilityR(allUnknownC,rivalsC,4-i,1-i);
            i = 0;
            index++;
        }
        return result;
    }

    private static double probabilityStraight(int allUnknownC, int rivalsC, int neededRanks){
        if(neededRanks > rivalsC) return 0;
        if(neededRanks <= 0) return 1;
        long result = 0;
        int a = 1;int b = 1;int c = 1;int d = 1;int e = 1;
        if(neededRanks == 1){
            return probabilityR(allUnknownC,rivalsC,4,1);
        }
        else if(neededRanks == 2){
            while(a <= 4 && a+b <= rivalsC){
                while(b <= 4 && a+b <= rivalsC){
                    result += (probabilityFactor(4,a) * probabilityFactor(4,b)) *
                            (probabilityFactor(allUnknownC-8,rivalsC-(a+b)));
                    b++;
                }
                b=1;a++;
            }
        }
        else if(neededRanks == 3){
            while(a <= 4 && a+b+c <= rivalsC){
                while(b <= 4 && a+b+c <= rivalsC){
                    while(c <= 4 && a+b+c <= rivalsC){
                        result += (probabilityFactor(4,a) * probabilityFactor(4,b) * probabilityFactor(4,c)) *
                                (probabilityFactor(allUnknownC-12,rivalsC-(a+b+c)));
                        c++;
                    }
                    c=1;b++;
                }
                b=1;a++;
            }
        }
        else if(neededRanks == 4){
            while(a <= 4 && a+b+c+d <= rivalsC){
                while(b <= 4 && a+b+c+d <= rivalsC){
                    while(c <= 4 && a+b+c+d <= rivalsC){
                        while(d <= 4 && a+b+c+d <= rivalsC){
                            result += (probabilityFactor(4,a) * probabilityFactor(4,b) * probabilityFactor(4,c) *
                                    probabilityFactor(4,d)) * (probabilityFactor(allUnknownC-16,rivalsC-(a+b+c+d)));
                            d++;
                        }
                        d=1;c++;
                    }
                    c=1;b++;
                }
                b=1;a++;
            }
        }
        else if(neededRanks == 5){
            while(a <= 4 && a+b+c+d+e <= rivalsC){
                while(b <= 4 && a+b+c+d+e <= rivalsC){
                    while(c <= 4 && a+b+c+d+e <= rivalsC){
                        while(d <= 4 && a+b+c+d+e <= rivalsC){
                            while(e <= 4 && a+b+c+d+e <= rivalsC){
                                result += (probabilityFactor(4,a) * probabilityFactor(4,b) * probabilityFactor(4,c) *
                                            probabilityFactor(4,d) * probabilityFactor(4,e)) *
                                            (probabilityFactor(allUnknownC-20,rivalsC-(a+b+c+d+e)));
                                e++;
                            }
                            e=1;d++;
                        }
                        d=1;c++;
                    }
                    c=1;b++;
                }
                b=1;a++;
            }
        }

        return result / (double)(probabilityFactor(allUnknownC,rivalsC));
    }

    private static double probabilityR(int allUnknownC, int rivalsC, int allHelpingC, int neededC){
        if(neededC > rivalsC || neededC > allHelpingC) return 0;
        if(neededC <= 0) return 1;
        return ((double)probabilityFactor(allUnknownC - allHelpingC,rivalsC - neededC) /
                (double)probabilityFactor(allUnknownC,rivalsC) * (double)probabilityFactor(allHelpingC,neededC)) +
                probabilityR(allUnknownC,rivalsC,allHelpingC,neededC + 1);
    }

    private static double probabilityAdvR(int allUnknownC, int rivalsC, int helpingC1, int neededC1, int helpingC2, int neededC2){
        if(neededC2 > helpingC2) return 0;
        if(neededC1 + neededC2 > rivalsC) return 0;
        if(neededC1 < 0 && neededC2 > 0) return probabilityR(allUnknownC,rivalsC,helpingC2,neededC2);
        if(neededC2 < 0 && neededC1 > 0) return probabilityR(allUnknownC,rivalsC,helpingC1,neededC1);
        if(neededC1 < 0 && neededC2 < 0) return 1;

        return (double)(probabilityFactor(helpingC1,neededC1)) * (double)(probabilityFactor(helpingC2,neededC2)) *
                    (double)(probabilityFactor(allUnknownC - (helpingC1 + helpingC2),rivalsC - (neededC1 + neededC2))) /
                    (double)(probabilityFactor(allUnknownC,rivalsC)) + probabilityAdvR(allUnknownC,rivalsC,helpingC1,neededC1,helpingC2,neededC2 + 1);
    }

    private static long probabilityFactor(int counter, int denominator){
        int difference = counter - denominator;
        BigInteger c = factorial(BigInteger.valueOf(counter));
        BigInteger d = factorial(BigInteger.valueOf(denominator)).multiply(factorial(BigInteger.valueOf(difference)));
        return c.divide(d).longValue();
    }

    private static BigInteger factorial(BigInteger n) {
        BigInteger result = BigInteger.ONE;
        while (!n.equals(BigInteger.ZERO)) {
            result = result.multiply(n);
            n = n.subtract(BigInteger.ONE);
        }
        return result;
    }


}
