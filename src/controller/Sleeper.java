package controller;

public class Sleeper extends Player {
    @Override
    public int[] decide(int[] yourMissiles, int[] opponentsMissiles) {
        return new int[0];
    }

    @Override
    public String getName() {
        return "Sleeper";
    }

    @Override
    public void restart() {

    }

    @Override
    public void exit() {

    }
}
