package submissions;

import src.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Random;

public class OwenPlayer implements Player {
    private final String name;
    private Set<Card> hand;
    private Map<Suit, Tower> towers = new HashMap<Suit, Tower>();
    private GameConfig config;
    private int checkCount;
    private int gameCount;
    private boolean right;

    public OwenPlayer() {
        this("Owen");
    }

    public OwenPlayer(String name) {
        this.name = name;
        for (Suit suit : Suit.values())
            towers.put(suit, new Tower());
        right = true;
    }

    @Override
    public void initialize(GameConfig config) {
        this.config = config;
        for (Suit suit : Suit.values())
            towers.put(suit, new Tower());
    }

    @Override
    public void deal(Set<Card> hand) {
        this.hand = hand;
    }

    @Override
    public void movePlayed(int index, Card card) {
        if (card != null) {
            Suit suit = card.getSuit();
            towers.get(suit).play(card.getCardIndex());
        } else {
            checkCount++;
        }
    }

    public static String replaceCharAt(String s, int pos, char c) {
        return s.substring(0,pos) + c + s.substring(pos+1);
    }

    @Override
    public Card play() {
        Card bestCard = null;
        double bestScore = 0;
        // System.out.println("\nIn Hand:");
        // for (Card potentialCard : hand) {
        //     System.out.println(potentialCard.toString());
        // }

        // System.out.println("\nCould Play:");
        for (Card potentialCard : hand) {
            if (towers.get(potentialCard.getSuit()).canPlay(potentialCard.getCardIndex())) {
                int potentialCardIndex = potentialCard.getCardIndex();
                int minScore;
                int distScore = 0;
                int tweakScore = 0;

                if (potentialCardIndex == 7) {
                    minScore = 13;
                } else if (potentialCardIndex < 7) {
                    minScore = potentialCardIndex;  
                } else {
                    minScore = 13 - potentialCardIndex;   
                }
                
                for (Card myCard: hand) {
                    if (myCard.getSuit() == potentialCard.getSuit()) {
                        int suitCardIndex = myCard.getCardIndex();
                        if (potentialCardIndex >= 7 && suitCardIndex > potentialCardIndex) {
                            int newMinScore = suitCardIndex-potentialCardIndex;
                            if (newMinScore < minScore) {
                                minScore = newMinScore;
                            }
                            int newDistScore = suitCardIndex-potentialCardIndex;
                            if (newDistScore > distScore) {
                                distScore = newDistScore;
                            }
                        }
                        if (potentialCardIndex <= 7 && suitCardIndex < potentialCardIndex) {
                            int newMinScore = potentialCardIndex-suitCardIndex;
                            if (newMinScore < minScore) {
                                minScore = newMinScore;
                            }
                            int newDistScore = potentialCardIndex-suitCardIndex;
                            if (newDistScore > distScore) {
                                distScore = newDistScore;
                            }
                        }
                    }
                }
                double newBestScore = Math.pow(distScore,1.5) + (13-minScore)/1.8;
                // System.out.println(potentialCard.toString() + ": " + Math.round(Math.pow(distScore,1.5)) + ", "+Math.round((13-minScore)/1.8));
                if (newBestScore >= bestScore) {
                    bestScore = newBestScore;
                    bestCard = potentialCard;
                }
            }
        }
        checkCount = 0;
        if (bestCard != null) {
            hand.remove(bestCard);
            return bestCard;
        } else {
            return null;
        }
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
