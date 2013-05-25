package submissions;

import src.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class JessPlayer implements Player {

    public JessPlayer(String name, Set<Card> hand, GameState gs, int w1, int w2, int w3, int w4) {
        super();
        this.name = "JessPlayer";
        this.w1 = w1;
        this.w2 = w2;
        this.w3 = w3;
        this.w4 = w4;
    }

    private String name;

    private Set<Card> hand;
    private GameState gs;
    private static int nPlayers;
    private int w1, w2, w3, w4;

    public static void printCardSet(String label, Set<Card> s) {

        System.out.print(label);
        for (Card c : s) {
            System.out.print(c.toString() + " - " + c + ", ");
        }
        System.out.println();
    }

    @Override
    public void initialize(GameConfig config) {
        nPlayers = config.getPlayerCount();
    }

    public JessPlayer() {
        super();
        this.name = "JessPlayer";
        this.w1 = 100;
        this.w2 = 10;
        this.w3 = 50;
        this.w4 = 100;
    }

    @Override
    public void deal(Set<Card> hand) {
        // TODO Auto-generated method stub
        this.hand = new HashSet<Card>(hand);
        gs = new GameState(this.hand, nPlayers);
        for (Card card : hand) 
            System.out.println("I was dealt: " + card.toString());
    }

    @Override
    public void movePlayed(int playerIndex, Card card) {
        gs.play(card);
    }

    @Override
    public Card play() {
        // printCardSet(name + "'s hand: ",hand);
        Card card = Search.cheap(gs, name, w1, w2, w3, w4);
        hand.remove(card);
        gs.hand.remove(card);
        System.out.println("I am playing " + (card != null ? card.toString() : "nothing"));
        return card;
    }

    @Override
    public String getName() {
        return name;
    }

    public static class Search {

        public static Card cheap(GameState gs, String name, int w1, int w2, int w3, int w4) {
            Card best = null;
            Integer bestv = Integer.MIN_VALUE;

            Set<Card> s = gs.playable(true);

            JessPlayer.printCardSet(name + " hand: ", gs.hand);
            JessPlayer.printCardSet(name + " playable: ", s);

            for (Card c : s) {
                // System.out.println(name + " Considering " + c.toString() +
                // " (top level)");
                GameState gs2 = gs.copy();
                gs2.play(c);
                Integer v = gs.cheapEval(c, w1, w2, w3, w4);
                // System.out.println("-- Would give minValue " + v);
                if (v > bestv) {
                    best = c;
                    bestv = v;
                }
            }
            if (best == null) {
                // System.out.println("###");
            }
            // System.out.println("Gonna play " + best + ", with v = " + bestv);
            gs.play(best);
            return best;
        }

        public static Card go(GameState gs) {
            int alpha = Integer.MIN_VALUE;
            int beta = Integer.MAX_VALUE;
            Card best = null;
            HashMap<Card, Integer> map = new HashMap<Card, Integer>();
            for (Card c : gs.playable(true)) {
                // //System.out.println("----Considering " + c +
                // " (inner level - max)");
                GameState gs2 = gs.copy();
                gs2.play(c);
                int v = minValue(gs2, 5, 1, alpha, beta);
                map.put(c, v);
                if (gs.playable(true).size() == 1) {
                    System.out.println("alpha = " + alpha + ", v = " + v);
                }
                if (alpha < v) {
                    alpha = v;
                    best = c;
                }
                if (gs.playable(true).size() == 1) {
                    System.out.println("alpha = " + alpha + ", v = " + v + ", best = " + best);
                    if (best == null) {
                        System.exit(0);
                    }
                }
                if (beta <= alpha) {
                    // System.out.println("Cutoff reached at a = " + alpha +
                    // ", b = " + beta);
                    break;
                }
                // //System.out.println("updated v " + v);
            }

            for (Card c2 : map.keySet()) {
                System.out.print(c2.toString() + " -> " + map.get(c2) + " ## ");
            }
            System.out.println();

            gs.play(best);
            return best;

            /*
             * Integer bestv = Integer.MIN_VALUE;
             * System.out.println("Top level search"); for (Card c :
             * gs.playable(true)) { // System.out.println("Considering " +
             * c.toString() + " (top level)"); GameState gs2 = gs.copy();
             * gs2.play(c); Integer v = minValue(gs2,10,1, Integer.MIN_VALUE,
             * Integer.MAX_VALUE); System.out.println("   " + c.toString() +
             * " would give minValue " + v); if (v > bestv) { best = c; bestv =
             * v; } } if (best == null && gs.playable(true).size() > 0) {
             * System.out.println("Uh-oh!"); System.exit(0); }
             * //System.out.println("Gonna play " + best + ", with v = " +
             * bestv); gs.play(best); return best;
             */
        }

        private static Integer maxValue(GameState gs, int depth, int turn, int alpha, int beta) {
            if (depth < 1 || gs.isTerminal()) {
                return gs.eval();
            }
            if (turn == 0) {
                for (Card c : gs.playable(true)) {
                    // //System.out.println("----Considering " + c +
                    // " (inner level - max)");
                    GameState gs2 = gs.copy();
                    gs2.play(c);
                    alpha = java.lang.Math.max(alpha, minValue(gs2, depth - 1, (turn + 1) % nPlayers, alpha, beta));
                    if (beta <= alpha) {
                        // System.out.println("Cutoff reached at a = " + alpha +
                        // ", b = " + beta);
                        break;
                    }
                    // //System.out.println("updated v " + v);
                }
            } else {
                // System.out.println("skipping my turn");
                return minValue(gs, depth, turn, alpha, beta);
            }
            // //System.out.println("chose v " + v);
            // System.out.println("d = " + depth + ", a = " + alpha + ", b = " +
            // beta);
            return alpha;
        }

        private static Integer minValue(GameState gs, int depth, int turn, int alpha, int beta) {
            if (depth < 1 || gs.isTerminal()) {
                // //System.out.println("------eval'ing " + gs.eval());
                return gs.eval();
            }
            for (Card c : gs.playable(false)) {
                // //System.out.println("----Considering " + c +
                // " (inner level - min)");
                GameState gs2 = gs.copy();
                gs2.play(c);
                beta = java.lang.Math.min(beta, maxValue(gs2, depth - 1, (turn + 1) % nPlayers, alpha, beta));
                if (beta <= alpha) {
                    // System.out.println("Cutoff reached at a = " + alpha +
                    // ", b = " + beta);
                    break;
                }
                // //System.out.println("updated v " + v);
            }
            // //System.out.println("chose v " + v);
            // System.out.println("d = " + depth + ", a = " + alpha + ", b = " +
            // beta);
            return beta;
        }

    }

    public class GameState {
        public HashMap<Suit, Integer> top, bottom, blocked, popular;
        private GameState targetState;
        public int nPlayers;

        public int cardsplayed;

        public Integer eval() {
            setTargetState();

            return movesNeededToReach(targetState);
        }

        public boolean isTerminal() {
            for (Suit s : Suit.values()) {
                if (top.get(s) != 13 || bottom.get(s) != 1) {
                    return false;
                }
            }
            return true;
        }

        public int cheapEval(Card c, int w1, int w2, int w3, int w4) {
            Suit s = c.getSuit();
            int i = c.getCardIndex();

            int score = 0;

            setTargetState();
            if (i >= 7) {
                score += w1 * (targetState.top.get(s) - i);
                score -= w2 * -targetState.top.get(s);
                score += w3 * -blocked.get(s);
                score -= w4 * -((popular.get(s) * 10) / (cardsplayed + 1));
            }

            if (i <= 7) {
                score += w1 * (i - targetState.bottom.get(s));
                score -= w2 * targetState.bottom.get(s);
                score += w3 * -blocked.get(s);
                score -= w4 * -((popular.get(s) * 10) / (cardsplayed + 1));
            }

            return score;
        }

        public Set<Card> hand;

        public boolean terminal() {
            return hand.isEmpty();
        }

        public int getTop(Suit s) {
            return top.get(s);
        }

        public int getBottom(Suit s) {
            return bottom.get(s);
        }

        public int movesNeededToReach(GameState gs) {
            int moves = 0;

            for (Suit s : Suit.values()) {
                moves += java.lang.Math.max(0, gs.getTop(s) - top.get(s));
                moves += java.lang.Math.max(0, gs.getBottom(s) - bottom.get(s));
            }

            return moves;
        }

        public int blocked() {
            int total = 0;
            for (Suit s : Suit.values()) {
                if (top.get(s) == 0) {
                    total += 14;
                    continue;
                } else {
                    total += 13 - top.get(s);
                    total += bottom.get(s) - 1;
                }
            }
            return total;

        }

        public GameState(Set<Card> hand, int nPlayers) {
            top = new HashMap<Suit, Integer>();
            bottom = new HashMap<Suit, Integer>();
            blocked = new HashMap<Suit, Integer>();
            popular = new HashMap<Suit, Integer>();
            this.nPlayers = nPlayers;
            this.hand = new HashSet<Card>(hand);
            for (Suit s : Suit.values()) {
                top.put(s, 0);
                bottom.put(s, 0);
                blocked.put(s, 0);
                popular.put(s, 0);
            }
            cardsplayed = 0;
            top.put(Suit.DIAMOND, 0);
            bottom.put(Suit.DIAMOND, 0);
        }

        public void setTargetState() {

            targetState = new GameState(hand, nPlayers);

            for (Card c : hand) {
                targetState.play(c);
            }
        }

        public GameState copy() {
            GameState gs = new GameState(new HashSet<Card>(hand), nPlayers);
            for (Suit s : Suit.values()) {
                if (top.get(s) > 0) {
                    gs.play(new Card(s, top.get(s)));
                }
                if (bottom.get(s) > 0) {
                    gs.play(new Card(s, bottom.get(s)));
                }
                gs.popular.put(s, popular.get(s));
                gs.blocked.put(s, blocked.get(s));
                gs.cardsplayed = cardsplayed;
            }
            return gs;
        }

        public void play(Card c) {
            if (c == null) {
                for (Suit s : Suit.values()) {
                    blocked.put(s, 1);
                }
                return;
            }
            if (c.getCardIndex() <= 7) {
                bottom.put(c.getSuit(), c.getCardIndex());
            }
            if (c.getCardIndex() >= 7) {
                top.put(c.getSuit(), c.getCardIndex());
            }

            blocked.put(c.getSuit(), 0);
            popular.put(c.getSuit(), popular.get(c.getSuit()) + 1);
            cardsplayed++;
            /*
             * if (c.getSuit() == Suit.DIAMOND && c.getCardIndex() == 7){
             * 
             * top.put(Suit.HEART, 0); bottom.put(Suit.HEART, 0);
             * top.put(Suit.CLUB, 0); bottom.put(Suit.CLUB, 0);
             * top.put(Suit.SPADE, 0); bottom.put(Suit.SPADE, 0); }
             */
        }

        public Set<Card> playable(boolean me) {
            Set<Card> cs = new HashSet<Card>();

            for (Suit s : Suit.values()) {
                if (top.get(s) == 0) {
                    cs.add(new Card(s, 7));
                } else if (0 < top.get(s) && top.get(s) < 13) {
                    cs.add(new Card(s, top.get(s) + 1));
                }

                if (bottom.get(s) > 1) {
                    cs.add(new Card(s, bottom.get(s) - 1));
                }
            }

            // JessPlayer.printCardSet("gs - cs: ", cs);
            if (me) {
                cs.retainAll(hand);
            } else {
                cs.removeAll(hand);
            }

            // JessPlayer.printCardSet("gs - hand: ", hand);

            System.out.println("TOTAL " + cs.size() + " cards playable.");

            return cs;
        }
    }

}
