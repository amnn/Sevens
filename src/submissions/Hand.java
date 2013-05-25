package submissions;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import src.Card;
import src.Suit;

class Hand implements Iterable<Card> {
    private SortedSet<Card> cards;
    private Set<Card> pairedCards;
    private SortedSet<Card> openingCards;
    private boolean[] cardsByValue;
    private boolean[] paired;
    private int[] follow;

    public void setCards(Set<Card> cards) {
        this.cardsByValue = new boolean[53];
        this.paired = new boolean[53];
        this.follow = new int[53];

        if (cards == null)
            return;

        for (Card card : cards) {
            int value = getIndex(card);
            cardsByValue[value] = true;
        }

        for (int i = 0; i < 39; i += 13) {
            paired[i + 1] = true;
            paired[i + 13] = true;
            
            
            for (int j = 2; j < 7; j++) {
                paired[i + j] = cardsByValue[i + j - 1];
                follow[i + j] = follow[i + j - 1] + (cardsByValue[i + j - 1] ? getCardWeight(i + j - 1) : 0); 
            }

            for (int j = 12; j > 7; j--) {
                paired[i + j] = cardsByValue[i + j + 1];
                follow[i + j] = follow[i + j + 1] + (cardsByValue[i + j + 1] ? getCardWeight(i + j + 1) : 0); 
            }
            
            follow [i + 7] = follow[i + 6] + follow[i + 8];
            paired[i + 7] = paired[i + 6] && paired[i + 8];
        }
        
        this.cards = new TreeSet<Card>(new GregComparator());
        this.openingCards = new TreeSet<Card>(new GregComparator());
        this.pairedCards = new HashSet<Card>();
        for (Card card : cards) {
            this.cards.add(card);
            if (paired[getIndex(card)])
                pairedCards.add(card);
            if (follow[getIndex(card)] >= 8)
                openingCards.add(card);
        }
        
        
//        for (Card card : this.cards)
//            System.out.println(pad(card.toString(), 20)
//                    + ": Points=" 
//                    + pad(Integer.toString(getPointScore(card)), 2)
//                    + "    paired="
//                    + paired[getIndex(card)]
//                    + "    follow="
//                    + follow[getIndex(card)]);
//        
//        for (Card card : pairedCards)
//            System.out.println("Paired: " + card.toString());
//                    
//        for (Card card : openingCards)
//            System.out.println("Opening: " + card.toString());
    }
    
    public static String pad(String s, int n) {
        return String.format("%1$-" + n + "s", s);  
   }

    public void remove(Card card) {
        cards.remove(card);
        pairedCards.remove(card);
        openingCards.remove(card);
    }

    @Override
    public Iterator<Card> iterator() {
        return cards.iterator();
    }
    
    public Iterator<Card> getPairedCards() {
        return pairedCards.iterator();
    }
    
    public Iterator<Card> getOpeningCards() {
        return pairedCards.iterator();
    }
    
    public int getIndex(Card card) {
        int value = card.getCardIndex();
        if (card.getSuit() == Suit.CLUB)
            value += 13;
        if (card.getSuit() == Suit.HEART)
            value += 26;
        if (card.getSuit() == Suit.SPADE)
            value += 39;
        return value;
    }
    
    public int getCardWeight(Card card) {
        return Math.abs(card.getCardIndex() - 7);
    }
    
    public int getCardWeight(int value) {
        return Math.abs(((value - 1) % 13) - 6);
    }

    public int getPointScore(Card card) {
        int index = card.getCardIndex();
        int score = Math.abs(index - 7) * 8;
        if (index > 7)
            score -= 4;

        if (card.getSuit() == Suit.CLUB)
            score += 1;
        if (card.getSuit() == Suit.HEART)
            score += 2;
        if (card.getSuit() == Suit.SPADE)
            score += 3;

        return score;
    }

    private class GregComparator implements Comparator<Card> {
        
        @Override
        public int compare(Card c0, Card c1) {
            int index0 = getIndex(c0);
            int score0 = getPointScore(c0);
            score0 += follow[index0] * 52;
//            if (paired[index0]) 
//                score0 += 104;
            
            int index1 = getIndex(c1);
            int score1 = getPointScore(c1);
            score1 += follow[index1] * 52;
//            if (paired[index1]) 
//                score1 += 104;
            
            return Integer.compare(score1, score0);
        }

    }
}
