package submissions;

import src.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PetePlayer implements Player {
   
        private final String name;
    private Set<Card> hand;
    private List<Card> handList;
    private Map<Suit, Tower> towers = new HashMap<Suit, Tower>();
    private Map<Card,Double> cardPriorities = new HashMap<Card,Double>();
    private Map<Suit,Double> suitPriorities = new HashMap<Suit,Double>();

    public PetePlayer() {
        this("Pete");
    }
    
    public PetePlayer(String name) {
        this.name = name;
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
        this.handList = new ArrayList<Card>(hand);
    }

    @Override
    public void movePlayed(int index, Card card) {
        if (card != null) {
            Suit suit = card.getSuit();
            towers.get(suit).play(card.getCardIndex());
        }
    }
    
    private void setSuitPriorities(){
        suitPriorities.put(Suit.DIAMOND,  0.0);
        suitPriorities.put(Suit.HEART,  0.0);
        suitPriorities.put(Suit.SPADE,  0.0);
        suitPriorities.put(Suit.CLUB,  0.0);
        for (Card card : handList) {
                Double suitPriority = suitPriorities.get(card.getSuit());
                suitPriority = Math.max(suitPriority, Math.abs(7-card.getCardIndex()))+1;
                        suitPriorities.put(card.getSuit(),suitPriority);
        }       
    }
    
    private void setCardPriorities(){
        for (Card card:handList){
                double suitPriority = suitPriorities.get(card.getSuit());
                        if(card.getCardIndex()==7){             
                                cardPriorities.put(card,suitPriority*10);
                        }
                        else{
                                int sevenProximity =15-Math.abs(7-card.getCardIndex());
                                //sevenProximity=sevenProximity*sevenProximity;
                                suitPriority=suitPriority*10;
                                //suitPriority=suitPriority*suitPriority;
                                cardPriorities.put(card,suitPriority+sevenProximity*10);
                        }
                }
    }
    private void orderHand(){
        boolean hasSwapped=true;
        while(hasSwapped){
                Card c1 = handList.get(0);
                for(int i=1;i<handList.size();i++){
                        Card c2 = handList.get(i);
                        if(cardPriorities.get(c1)<cardPriorities.get(c2)){
                                handList.add(0, c2);
                                handList.add(i,c1);
                                break;
                        }
                }
                hasSwapped=false;
                
        }
    }

    @Override
    public Card play() {
        setSuitPriorities();
        setCardPriorities();
        orderHand(); 
        for (Card card : handList) {
            if (towers.get(card.getSuit()).canPlay(card.getCardIndex())) {
                handList.remove(card);
                return card;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return name;
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
}
