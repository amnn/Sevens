package submissions;

import src.*;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class GrahamPlayer implements Player {
    private Map<Suit, Hand> hands = new EnumMap<Suit, Hand>(Suit.class);
    private Map<Suit, Tower> towers = new EnumMap<Suit, Tower>(Suit.class);

    @Override
    public void initialize(GameConfig config) {
        for (Suit suit : Suit.values()) {
            towers.put(suit, new Tower());
            hands.put(suit, new Hand());
        }
    }

    @Override
    public void deal(Set<Card> hand) {
        for (Card c : hand)
                hands.get(c.getSuit()).add(c);
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
        Play p = new Play();
        for (Suit s : Suit.values()) {
                Hand h = hands.get(s);
                Tower t = towers.get(s);
                Play p0 = h.getBestPlay(t);
                if (p.compareTo(p0) < 0)
                        p = p0;
        }
        if (p.card != null)
                hands.get(p.card.getSuit()).remove(p.card);
        return p.card;
    }

    @Override
    public String getName() {
        return "Graham";
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
        
        public boolean canPlay(Card card) {
                return canPlay(card.getCardIndex());
        }
    }
    
    private class Hand {
        SortedSet<Card> highs = new TreeSet<Card>(new Comparator<Card>() {
                        @Override
                        public int compare(Card c0, Card c1) {
                                return c0.getCardIndex() - c1.getCardIndex();
                        }
        });
        SortedSet<Card> lows = new TreeSet<Card>(new Comparator<Card>() {
                @Override
                public int compare(Card c0, Card c1) {
                        return c1.getCardIndex() - c0.getCardIndex();
                }
        });
        Card seven = null;
        
        public void add(Card card) {
                int cValue = card.getCardIndex();
                if (cValue == 7)
                        seven = card;
                else if (cValue > 7)
                        highs.add(card);
                else
                        lows.add(card);
        }
        
        public void remove(Card card) {
                int cValue = card.getCardIndex();
                if (cValue == 7)
                        seven = null;
                else if (cValue > 7)
                        highs.remove(card);
                else
                        lows.remove(card);
        }
        
        public Play getBestPlay(Tower t) {
                SortedSet<Play> plays = new TreeSet<Play>();
                if (seven != null && t.canPlay(seven)) {
                        int min = Integer.MAX_VALUE;
                        if (!highs.isEmpty())
                                min = 14 - highs.last().getCardIndex();
                        if (!lows.isEmpty())
                                min = Math.min(min, lows.last().getCardIndex());
                        plays.add(new Play(seven, size(), min));
                }
                if (!highs.isEmpty() && t.canPlay(highs.first()))
                        plays.add(new Play(highs.first(), highs.size(), 14 - highs.last().getCardIndex()));
                if (!lows.isEmpty() && t.canPlay(lows.first()))
                        plays.add(new Play(lows.first(), lows.size(), lows.last().getCardIndex()));
                return plays.isEmpty() ? new Play() : plays.last();
        }
        
        public int size() {
                return lows.size()
                     + highs.size()
                     + (seven == null ? 0 : 1);
        }
        
    }
    
    private static class Play implements Comparable<Play> {
        Card card;
        int size;
        int min;
        
        Play() {
                card = null;
                size = 0;
                min = Integer.MAX_VALUE;
        }
        
        Play(Card card, int size, int min) {
                this.card = card;
                this.size = size;
                this.min = min;
        }

                @Override
                public int compareTo(Play p) {
                        if (size == p.size)
                                return p.min - min;
                        else if (size > p.size)
                                return 1;
                        else
                                return -1;
                }
                
                static int normalize(Card card) {
                        return normalize(card.getCardIndex());
                }
                
                static int normalize(int value) {
                        return value > 7 ? 14 - value : value;
                }
    }
}

