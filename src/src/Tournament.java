package src;
import submissions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tournament {

    /**
     * @param args
     */
    public static void main(String[] args) {
        final int TOTAL_GAMES = 10000;
        final Map<Player, Integer> scores = new HashMap<Player, Integer>();
        final Map<Player, Integer> played = new HashMap<Player, Integer>();

        List<Player> players = new ArrayList<Player>();

        players.add(new GregPlayer());
        players.add(new OwenPlayer());
        players.add(new JamesPlayer());
        players.add(new GrahamPlayer());
        players.add(new NudgePlayer());
        players.add(new ElliotPlayer());
        players.add(new PlayerHynek());
//        players.add(new JessPlayer());
//        players.add(new NaivePlayer("Owen1"));
//        players.add(new NaivePlayer("Owen2"));
//        players.add(new NaivePlayer("Owen3"));
//        players.add(new NaivePlayer("Owen4"));
        
        
        for (Player p : players) {
            scores.put(p, new Integer(0));
            played.put(p, new Integer(0));
        }


        try {
        for (int i = 0; i < TOTAL_GAMES; i++) {
                Game game = new Game();
                java.util.Collections.shuffle(players); 
                
                if (i % 20000 == 0) {
                    System.out.println("Playing hand: " + i);
                }
                
                for (int j = 0; j < 4; j++) {
                    Player p = players.get(j);
                    game.addPlayer(p);
                    played.put(p, played.get(p) + 1);
                }

                game.deal();
                
                Player winner = game.play();
                if (scores.containsKey(winner))
                    scores.put(winner, scores.get(winner) + 1);
                else
                    scores.put(winner, 1);
        }
        } catch (SomeoneNoobException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        
        for (Player player : players)
            System.out.println(String.format("%1$-10s: ", player.getName()) + scores.get(player) + " / " + played.get(player) + "  " + (double)scores.get(player) / played.get(player));
    }

}
