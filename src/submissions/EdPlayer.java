package submissions;

import src.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class EdPlayer implements Player {
    private final String name;
    private static int instance;
    private Set<Card> hand;
    private Map<Suit, Tower> towers = new HashMap<Suit, Tower>();
        private int lastPlayer = 0;
        private int players;
        EnumMap<Suit, Integer>[] playerSuitPlays;
        private int iAmPlayer;
        
    public EdPlayer() {
        this.name = "Sober Player " + instance + "";
    }

    @Override
    public void initialize(GameConfig config) {
        for (Suit suit : Suit.values()){
            towers.put(suit, new Tower());
        }
        players = config.getPlayerCount();
        playerSuitPlays = new EnumMap[players];
        for (int i = 0; i<players; i++){
                playerSuitPlays[i] = new EnumMap<Suit, Integer>(Suit.class);
                for (Suit suit: Suit.values()){
                        playerSuitPlays[i].put(suit, 0);
                }
        }
    }

    @Override
    public void deal(Set<Card> hand) {
        this.hand = hand;
    }

    @Override
    public void movePlayed(int index, Card card) {
        lastPlayer = index;
        if (card != null) {
            Suit suit = card.getSuit();
            towers.get(suit).play(card.getCardIndex());
                if (index == iAmPlayer) return;
            EnumMap<Suit, Integer> playerCount = playerSuitPlays[index];
            playerCount.put(card.getSuit(), playerCount.get(card.getSuit()) + 1);
        }
    }

    @Override
    public Card play() {
        iAmPlayer = (lastPlayer+1)%players;
        
        List<Card> playable = new ArrayList<>();
        for (Suit suit : Suit.values()){
                Tower tower = towers.get(suit);
                if (tower.upperBound != 14)
                        playable.add(new Card(suit, tower.upperBound));
                if (tower.lowerBound != 0)
                        playable.add(new Card(suit, tower.lowerBound));
        }
        
        playable.retainAll(hand);

        
        SortedSet<Ranked<Card>> playableBroke = new TreeSet<>();
        
//      for (Suit suit : Suit.values()){
//              Tower tower = towers.get(suit);
//              if (tower.upperBound != 14){
//                      Card card = new Card(suit, tower.upperBound);
//                      if(hand.contains(card))
//                              playableBroke.add(new Ranked<Card> (card, getRank(card)));
//              }
//              if (tower.lowerBound != 0){
//                      Card card = new Card(suit, tower.lowerBound);
//                      if (hand.contains(card))
//                              playableBroke.add(new Ranked<Card> (card, getRank(card)));
//              }
//      }
        
        for (Card card : playable){
                playableBroke.add(new Ranked<Card>(card, getRank(card)));
        }
        
        if (playableBroke.size() <= 0) {
                return null;
        }
        
        Card card = playableBroke.first().value;

        hand.remove(card);
        return card;
    }

    private float getRank(Card card) {
        float rankSuit = 1;
        float rankPlayability = 1;
        
        for (EnumMap<Suit, Integer> playerCount : playerSuitPlays){
                rankSuit += playerCount.get(card.getSuit());
        }
        int value = card.getCardIndex();
        if (value == 7){
                Card six = new Card(card.getSuit(), 6);
                Card eight = new Card(card.getSuit(), 8);
                if (hand.contains(six) && hand.contains(eight)){
                        return 0;
                }
        }
        else if (value != 13 && value != 1) {
                Card plus1 = new Card(card.getSuit(), (value>7)?(value+1):(value-1));
                if (hand.contains(plus1)){
                        return 0;
                }
        }
        
        rankPlayability = closeness(card);
        
        return rankSuit * rankPlayability;
        }

        private float closeness(Card card) {
                return 1;
        }

        @Override
    public String getName() {
        return name;
    }

    private class Ranked<T> implements Comparable<Ranked<T>>{
        public T value;
        public float rank;
        Ranked(T value, float rank){
                this.value = value;
                this.rank = rank;
        }
                @Override
                public int compareTo(Ranked<T> o) {
                        return Float.compare(this.rank, o.rank);
                }
        
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

