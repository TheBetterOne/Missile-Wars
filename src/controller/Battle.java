package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Battle {

    private final static int BOARD_WIDTH = 55;
    private final static int BOARD_HEIGHT = 12;
    private static final boolean DEBUG = false;
    private static int idGenerator = 0;
    private final Player player1;
    private final Player player2;
    private int id;
    private Field field;

    public Battle(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.id = idGenerator;
        idGenerator++;
        field = new Field(BOARD_WIDTH, BOARD_HEIGHT);
        new File("battles\\" + id).mkdirs();
    }

    public Player run() {
        Player currentPlayer = player2;
        field.flip();
        int round = 0;
        while (!field.isFinished()) {
            currentPlayer = currentPlayer == player1 ? player2 : player1;
            field.flip();
            doTurn(currentPlayer, field);
            if (currentPlayer == player1) {
                round++;
                try {
                    new PrintStream("battles\\" + id + "\\" + round + ".txt").print(field);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        if (field.hasWon()) {
            return currentPlayer;
        } else if (field.hasLost()) {
            return currentPlayer == player1 ? player2 : player1;
        }
        return null;
    }

    private void doTurn(Player currentPlayer, Field field) {
        int[] args1 = field.getCurrentPlayersMissiles();
        int[] args2 = field.getOpponentMissiles();
        int[] decision;
        try {
            decision = currentPlayer.decide(args1, args2);
        } catch (Throwable th) {
            if (DEBUG) {
                th.printStackTrace();
            }
            decision = new int[0];
        }
        Map<Integer, int[]> newMissileLocations = new HashMap<>();
        for (int i = 0; i + 2 < decision.length; i += 3) {
            newMissileLocations.put(decision[i], new int[]{decision[i + 1], decision[i + 2]});
        }
        for (Field.Missile missile : field.leftMissiles) {
            if (newMissileLocations.containsKey(missile.getIndex())) {
                missile.move(newMissileLocations.get(missile.getIndex()));
            } else {
                missile.move();
            }
        }
        field.detonate();
        if (DEBUG) {
            System.out.println(currentPlayer.getName());
            System.out.println(Arrays.toString(args1));
            System.out.println(Arrays.toString(args2));
            System.out.println(Arrays.toString(decision));
            synchronized (this) {
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
