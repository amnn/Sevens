package src;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * A single game of Sevens.
 * Add the players, then called .deal(), then call .play()
 * @author Greg
 *
 */
public class Game {
    private final List<Player> players;
    private final Map<Player, HashSet<Card>> hands;
    private final GameState gameState;

    private boolean gameStarted = false;
    private int playerCount = 0;
    private int nextPlayerIndex = 0;

    public Game() {
        players = new ArrayList<Player>();
        hands = new HashMap<Player, HashSet<Card>>();
        gameState = new GameState();
    }

    public void addPlayer(Player player) {
        if (!gameStarted && player != null)
            if (!players.contains(player)) {
                players.add(player);
                hands.put(player, new HashSet<Card>());
                playerCount++;
            }
    }

    public void addPlayers(List<Player> players) {
        for (Player player : players)
            addPlayer(player);
    }

    /**
     * Deal random hands to all players in the game, and send them their own hands,
     * and a GameConfig object
     */
    public void deal() {
        if (playerCount >= 3) {
            gameStarted = true;

            int[] playerCardCount = new int[playerCount];

            // Initialize an unshuffled deck
            List<Card> pack = new ArrayList<Card>();
            for (int i = 1; i <= 52; i++) {
                pack.add(new Card(i));
            }

            // Pick random card from the deck, and give it to the next player
            Random rng = new Random();
            for (int i = 0; i < 52; i++) {
                int playerNum = i % playerCount;
                Player player = players.get(playerNum);

                Card nextCard = pack.remove(rng.nextInt(52 - i));
                hands.get(player).add(nextCard);
                playerCardCount[playerNum]++;
            }

            // Send out the GameConfig and Hands to players
            GameConfig config = new GameConfig(playerCount, playerCardCount);
            for (Player player : players) {
                player.initialize(config);
                player.deal((Set<Card>) hands.get(player).clone());
            }
        }
    }

    /**
     * Request that the next player makes their move, and update all state
     * @return
     * @throws SomeoneNoobException
     */
    private Player playStep() throws SomeoneNoobException {
        if (!gameStarted || isGameFinished())
            throw new GregNoobException("His program tried to playStep() when it shouldn't");

        Player player = players.get(nextPlayerIndex);
        Card card = player.play();

        // Has the user played a card?
        if (card != null) {
            
            if (!hands.get(player).contains(card))
                throw new SomeoneNoobException(player.getName(), "They tried to play a card they don't have!");
            else if (!gameState.canPlay(card))
                throw new SomeoneNoobException(player.getName(), "They tried to play an invalid card!");
            else {
                gameState.play(card);
                hands.get(player).remove(card);
            }
        } else if (hasValidMove(player)) {
                throw new SomeoneNoobException(player.getName(), "They skipped when they could have played " + getPlayableCard(player));
        }
        
        // This should be out here so we also notify of skips as well as valid plays
        for (Player p : players)
            p.movePlayed(nextPlayerIndex, card);

        nextPlayerIndex = (nextPlayerIndex + 1) % playerCount;
        return player;
    }
    
    public Card getPlayableCard(Player player) {
        for (Card card : hands.get(player)) {
            if (gameState.canPlay(card))
                return card;
        }
        return null;
     }

    /**
     * Play a game of sevens
     * @return          
     *          The winner of the game
     * @throws SomeoneNoobException     
     *          Someone doesn't know how to program...
     */
    public Player play() throws SomeoneNoobException {
        Player currentPlayer = null;
        while (!isGameFinished()) {
            currentPlayer = playStep();
            if (hands.get(currentPlayer).isEmpty())
                break;
        }
        return currentPlayer;
    }

    
    /**
     * Have all 52 cards been played?
     * @return
     */
    public boolean isGameFinished() {
        return gameState.isGameFinished();
    }

    /**
     * Does the player have a card in their hand which could be played?
     * @param player
     * @return
     */
    private boolean hasValidMove(Player player) {
        for (Card card : hands.get(player)) {
            if (gameState.canPlay(card))
                return true;
        }
        return false;
    }

    private class GameState {
        private final Map<Suit, Tower> towers;

        public GameState() {
            towers = new HashMap<Suit, Tower>();
            for (Suit suit : Suit.values())
                towers.put(suit, new Tower());
        }

        public void play(Card card) throws GregNoobException {
            Tower tower = towers.get(card.getSuit());
            tower.play(card.getCardIndex());
        }

        public boolean canPlay(Card card) {
            Tower tower = towers.get(card.getSuit());
            return tower.canPlay(card.getCardIndex());
        }

        public boolean isGameFinished() {
            return towers.get(Suit.DIAMOND).isClosed() 
                    && towers.get(Suit.CLUB).isClosed() 
                    && towers.get(Suit.HEART).isClosed() 
                    && towers.get(Suit.SPADE).isClosed();
        }

        private class Tower {
            int upperBound = 7;
            int lowerBound = 7;

            public void play(int card) throws GregNoobException {
                if (!canPlay(card))
                    throw new GregNoobException("Why'd he try to play a card before checking if he could?");

                if (card == upperBound)
                    upperBound += 1;

                if (card == lowerBound)
                    lowerBound -= 1;
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
