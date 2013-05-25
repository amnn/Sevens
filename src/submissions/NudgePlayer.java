package submissions;

import src.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NudgePlayer implements Player {
    private Set<Card> hand;
    private Map<Suit, Tower> towers = new HashMap<Suit, Tower>();
    private int[] mostCards = new int[8];

    public NudgePlayer() {
        for (Suit suit : Suit.values())
            towers.put(suit, new Tower());
    }

    @Override
    public void initialize(GameConfig config) {
        for (Suit suit : Suit.values())
            towers.put(suit, new Tower());
    }

    @Override
    public void deal(Set<Card> hand) {
        this.hand = hand;
        for (Card c : hand){
                switch (c.getSuit()){
                case HEART:
                        if (c.getCardIndex() < 7){
                        mostCards[0] += 1;
                        }
                        else {
                        mostCards[1] += 1;
                        }
                case SPADE:
                        if (c.getCardIndex() < 7){
                        mostCards[2] += 1;
                        }
                        else {
                        mostCards[3] += 1;
                        }
                case DIAMOND:
                        if (c.getCardIndex() < 7){
                        mostCards[4] += 1;
                        }
                        else {
                        mostCards[5] += 1;
                        }
                case CLUB:
                        if (c.getCardIndex() < 7){
                        mostCards[6] += 1;
                        }
                        else {
                        mostCards[7] += 1;
                        }
                }
        }
    }

    @Override
    public void movePlayed(int index, Card card) {
        if (card != null) {
            Suit suit = card.getSuit();
            towers.get(suit).play(card.getCardIndex());
        }
    }

    @Override
    public Card play() {
        Card playCard = null;
        int cardScore = 0;
        for (Card card : hand) {
            if (towers.get(card.getSuit()).canPlay(card.getCardIndex())) {
                if (playCard == null) { 
                        playCard = card;
                        cardScore = getCardScore(card);
                }
                else {
                        if (getCardScore(card) > cardScore) {
                                playCard = card;
                                cardScore = getCardScore(card);
                        }
                }
            }
        }
        if (playCard != null) { 
                hand.remove(playCard); 
                reduceCardScore(playCard);
                return playCard; 
        }
        else { return null; }
    }

    @Override
    public String getName() {
        return "Nudgosaur";
    }

    private class Tower {
        private int upperBound = 7;
        private int lowerBound = 7;

        public void play(int cardIndex) {
            if (cardIndex == upperBound)
                upperBound += 1;

            if (cardIndex == lowerBound)
                lowerBound -= 1;
        }

        public boolean canPlay(int cardIndex) {
            return cardIndex == lowerBound || cardIndex == upperBound;
        }
    }
    
    private int getCardScore(Card card){
        int cardScore = 0;
        switch (card.getSuit()){
        case HEART:
                if (card.getCardIndex() < 7){
                        cardScore = mostCards[0];
                }
                else {
                        cardScore = mostCards[1];
                }
        case SPADE:
                if (card.getCardIndex() < 7){
                        cardScore = mostCards[2];
                }
                else {
                        cardScore = mostCards[3];
                }
        case DIAMOND:
                if (card.getCardIndex() < 7){
                        cardScore = mostCards[4];
                }
                else {
                        cardScore = mostCards[5];
                }
        case CLUB:
                if (card.getCardIndex() < 7){
                        cardScore = mostCards[6];
                }
                else {
                        cardScore = mostCards[7];
                }
        }
        return cardScore;
    }
    
    private void reduceCardScore(Card c){
        switch (c.getSuit()){
        case HEART:
                if (c.getCardIndex() < 7){
                        mostCards[0] -= 1;
                }
                else {
                        mostCards[1] -= 1;
                }
        case SPADE:
                if (c.getCardIndex() < 7){
                        mostCards[2] -= 1;
                }
                else {
                        mostCards[3] -= 1;
                }
        case DIAMOND:
                if (c.getCardIndex() < 7){
                        mostCards[4] -= 1;
                }
                else {
                        mostCards[5] -= 1;
                }
        case CLUB:
                if (c.getCardIndex() < 7){
                        mostCards[6] -= 1;
                }
                else {
                        mostCards[7] -= 1;
                }
        }
    }
}
