package controller;

import java.util.Arrays;

public class Tournament {

    private final static int ROUNDS = 3;

    public static void main(String[] args) {
        Player[] players = new Player[]{//Put players here.
                new Sleeper(),
                new OneAtATime()
                //new Submission("name", new String[]{"interpreter", "filename"});
        };
        int[] score = new int[players.length];

        for (int i = 0; i < players.length - 1; i++) {
            for (int j = i + 1; j < players.length; j++) {
                for (int k = 0; k < ROUNDS; k++) {
                    Battle battle = Math.random() > .5 ? new Battle(players[i], players[j]) : new Battle(players[j], players[i]);
                    Player winner = battle.run();
                    battle.close();
                    Player loser = winner == players[i] ? players[j] : players[i];
                    winner.restart();
                    loser.restart();
                    System.out.println(winner.getName() + " wins " + loser.getName());
                    score[winner == players[i] ? i : j]++;
                }
            }
        }

        for (Player player : players) {
            player.exit();
        }

        Integer[] sorted = new Integer[players.length];
        Arrays.setAll(sorted, a -> a);
        Arrays.sort(sorted, (a, b) -> score[b] - score[a]);

        for (int index : sorted) {
            System.out.println(players[index].getName() + ": " + score[index]);
        }

    }

}
