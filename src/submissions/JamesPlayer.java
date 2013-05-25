package submissions;

import src.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JamesPlayer implements Player {
    private final String name = "James";
    private Set<ExtendedCard> hand;
    private Map<Suit, Integer> suitsTop = new HashMap<Suit,Integer>();
    private Map<Suit, Integer> suitsBottom = new HashMap<Suit,Integer>();
    private Map<Suit, Tower> towers = new HashMap<Suit, Tower>();
    private GameConfig _config;
    private int _playPosition = 0;
    private Set<ExtendedCard> deck = new HashSet<ExtendedCard>();
    private double c1 = 0;
    private double c2 = 0;
    private double c3 = 0;
    private double c4 = 0;
    private double c5 = 0;
    
    public JamesPlayer() {
        c1 = 0.532;
        c2 = 3.345;
        c3 = 2.549;
        c4 = 0.518;
        c5 = 1.471;
        for (Suit suit : Suit.values())
            towers.put(suit, new Tower());
    }

    @Override
    public void initialize(GameConfig config) {
        for (Suit suit : Suit.values())
            towers.put(suit, new Tower());
        _config = config;
        suitsTop = new HashMap<Suit,Integer>();
        suitsBottom = new HashMap<Suit,Integer>();
    }

    @Override
    public void deal(Set<Card> hand) {
        HashSet<ExtendedCard> _hand = new HashSet<ExtendedCard>();
        suitsTop.put(Suit.CLUB,0);
        suitsTop.put(Suit.DIAMOND,0);
        suitsTop.put(Suit.HEART,0);
        suitsTop.put(Suit.SPADE,0);
        
        suitsBottom.put(Suit.CLUB,0);
        suitsBottom.put(Suit.DIAMOND,0);
        suitsBottom.put(Suit.HEART,0);
        suitsBottom.put(Suit.SPADE,0);
        
        for (Card card : hand) {
            _hand.add(new ExtendedCard(card));
            if(card.getCardIndex() < 7)
                suitsBottom.put(card.getSuit(), suitsBottom.get(card.getSuit()) + 1);
            if(card.getCardIndex() > 7)
                suitsTop.put(card.getSuit(), suitsTop.get(card.getSuit()) + 1);
        }
        this.hand = _hand;
    }

    @Override
    public void movePlayed(int index, Card card) {
        if (card != null) {
            Suit suit = card.getSuit();
            towers.get(suit).play(card.getCardIndex());
            //LEAK NOOB
            //deck.add(new ExtendedCard(card,_playPosition++, index));
        }
    }

    @Override
    public Card play() {
        HashSet<ExtendedCard> _playCards = new HashSet<ExtendedCard>();
        for (ExtendedCard card : hand) {
            if (towers.get(card._card.getSuit()).canPlay(card._card.getCardIndex())) {
                _playCards.add(card);
            }
       }
        
        if(_playCards.isEmpty())
            return null;
        if(_playCards.size() == 1)
        {
            hand.remove((ExtendedCard)_playCards.toArray()[0]);
            Card pc = ((ExtendedCard)_playCards.toArray()[0])._card;
            if(pc.getCardIndex() < 7)
                suitsBottom.put(pc.getSuit(), suitsBottom.get(pc.getSuit()) - 1);
            if(pc.getCardIndex() > 7)
                suitsTop.put(pc.getSuit(), suitsTop.get(pc.getSuit()) - 1);
            
            return pc;
        }
        
        for(ExtendedCard handCard : hand) {
            handCard._distanceFromBeingPlayed = cardDistance(handCard);
            if(handCard._card.getCardIndex() > 7) {
              handCard._cardsOpenedFromPlaying = 13 - handCard._card.getCardIndex();
            } else if(handCard._card.getCardIndex() < 7) {
              handCard._cardsOpenedFromPlaying = handCard._card.getCardIndex();
            }
        }
        
        for(ExtendedCard ex : _playCards) {
            ex._totalHelpfulDistanceGainedByPlaying = 0;
            ex._nextBlockerCount = 0;
            for(ExtendedCard handCard : hand) {
                if (ex.isPlayedBefore(handCard)) {
                  ex._totalHelpfulDistanceGainedByPlaying += handCard._distanceFromBeingPlayed;
                  ex._nextBlockerCount += 1;
                }
                
            }
            ex.PlayDesire = ex._totalHelpfulDistanceGainedByPlaying * c4; //1
            ex.PlayDesire -= ex._cardsOpenedFromPlaying * c1; //2
            ex.PlayDesire += ex._nextBlockerCount * c2; //4
            
            if(ex._card.getCardIndex() < 7)
                ex.PlayDesire +=  suitsBottom.get(ex._card.getSuit()) * c3; //2
            if(ex._card.getCardIndex() > 7)
                ex.PlayDesire +=  suitsTop.get(ex._card.getSuit()) * c3; //2
            
            if(ex._totalHelpfulDistanceGainedByPlaying == 0) {
                ex._isBlocker = true;
                ex.PlayDesire -= 20 * c5;
            }
        }
        
        
       ExtendedCard cardToPlay = null;
       double bestDesire = -9999999;
       for (ExtendedCard card : _playCards) {
          if(card.PlayDesire > bestDesire) {
              bestDesire = card.PlayDesire;
              cardToPlay = card;
          }
       }
       
       if(cardToPlay != null) {
         hand.remove(cardToPlay);
         Card pc = cardToPlay._card;
         if(pc.getCardIndex() < 7)
                suitsBottom.put(pc.getSuit(), suitsBottom.get(pc.getSuit()) - 1);
          if(pc.getCardIndex() > 7)
                suitsTop.put(pc.getSuit(), suitsTop.get(pc.getSuit()) - 1);
       }
       return cardToPlay._card;
    }

    @Override
    public String getName() {
        return name;
    }
    
    public int cardDistance(ExtendedCard card) {
        return towers.get(card._card.getSuit()).distance(card._card.getCardIndex());
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

        public int distance(int index) {
            if(index > upperBound)
                return index - upperBound;
            if(index < lowerBound)
                return lowerBound - index;
            return 0;
        }
        
        public boolean canPlay(int cardIndex) {
            return cardIndex == lowerBound || cardIndex == upperBound;
        }
    }
}
