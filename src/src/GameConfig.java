package src;

public class GameConfig {
    private int playerCount;
    private int[] playerCardCount;
    
    public GameConfig(int playerCount, int[] playerCardCount) {
        this.playerCount = playerCount;;
        this.playerCardCount = playerCardCount;
    }
    
    public int getPlayerCount() {
        return playerCount;
    }
    
    public int[] getPlayerCardCount() {
        return playerCardCount;
    }
}
