package submissions;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import src.Card;
import src.GameConfig;
import src.Player;
import src.Suit;

public class GregPlayer implements Player {
    private final String name;
    private final Hand hand;
    private GameConfig config;
    private GameState gameState;

    private int myIndex;
    private boolean justPlayed = false;
    private int skipCount = 0;
    private boolean myTurnLast = false;

    public GregPlayer() {
        this("Greg");
    }

    public GregPlayer(String name) {
        this.name = name;
        this.hand = new Hand();
        this.gameState = new GameState();
    }

    @Override
    public void initialize(GameConfig config) {
        this.config = config;
        this.gameState = new GameState();
        myIndex = -1;
        skipCount = 0;
        myTurnLast = false;
    }

    @Override
    public void deal(Set<Card> cards) {
        this.hand.setCards(cards);
    }

    @Override
    public void movePlayed(int index, Card card) {
        if (myIndex == -1 && justPlayed)
            myIndex = index;

        if (card != null)
            gameState.cardPlayed(card);
        else if (!myTurnLast)
            skipCount++;
        
        myTurnLast = false;
    }

    @Override
    public Card play() {
        Card candidate = null;

        Iterator<Card> iter = null;
        if (skipCount == (config.getPlayerCount() - 1)) {
            iter = hand.getPairedCards();
        }
//        else if (skipCount <= (config.getPlayerCount() * (2 / 4)))
//            iter = hand.getOpeningCards();

        while (iter != null && iter.hasNext()) {
            Card card = iter.next();
            if (gameState.canPlay(card)) {
                candidate = card;
                break;
            }
        }


        if (candidate == null)
            for (Card card : hand) {
                if (gameState.canPlay(card)) {
                    candidate = card;
                    break;
                }
            }

        skipCount = 0;
        myTurnLast = true;
        if (candidate != null)
            hand.remove(candidate);

        return candidate;
    }

    @Override
    public String getName() {
        return name;
    }

    private class GameState {
        private final Map<Suit, Tower> towers;

        public GameState() {
            towers = new HashMap<Suit, Tower>();
            for (Suit suit : Suit.values())
                towers.put(suit, new Tower());
        }

        public void cardPlayed(Card card) {
            Tower tower = towers.get(card.getSuit());
            tower.play(card.getCardIndex());
        }

        public boolean canPlay(Card card) {
            Tower tower = towers.get(card.getSuit());
            return tower.canPlay(card.getCardIndex());
        }

        private class Tower {
            int upperBound = 7;
            int lowerBound = 7;

            public void play(int card) {
                if (canPlay(card)) {
                    if (card == upperBound)
                        upperBound += 1;

                    if (card == lowerBound)
                        lowerBound -= 1;
                }
            }

            public boolean canPlay(int cardIndex) {
                return !isClosed() && (cardIndex == lowerBound || cardIndex == upperBound);
            }

            public boolean isClosed() {
                return lowerBound <= 0 && upperBound >= 14;
            }
        }
    }
}
