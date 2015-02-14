package controller;

public abstract class Player {

    public abstract int[] decide(int[] yourMissiles, int[] opponentsMissiles);

    public abstract String getName();

    public abstract void restart();

    public abstract void exit();

}
