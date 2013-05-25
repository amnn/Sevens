package src;
import java.util.Set;

public interface Player {
    /**
     * All players are at the table, the config object stores any information
     * you need
     * 
     * @param config
     *            Stores how many players there are at the table, and how many
     *            cards each of them have
     *            Calling this should reset the game state, so one Player object
     *            can play many hands (sequentially)
     */
    public void initialize(GameConfig config);

    /**
     * You have been dealt a hand
     * 
     * @param hand
     */
    public void deal(Set<Card> hand);

    /**
     * A player has made a move
     * 
     * @param playerIndex
     *            index from [0..numPlayers) of the player
     * @param card
     *            The card object they played (its immutable, don't try it)
     */
    public void movePlayed(int playerIndex, Card card);

    /**
     * Called by the dealer when it is your turn to play, return the Card you
     * would like to play. The dealer will be checking your move it valid, but
     * it screws up the game if you do invalid moves, and you will probably just
     * be removed from the competition :(
     * 
     * @return
     */
    public Card play();

    /**
     * Your name, or the name you want you program to go by. Please don't change
     * this on-the-fly
     * 
     * @return
     */
    public String getName();
}
