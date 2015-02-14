package controller;

public class OneAtATime extends Player {


    @Override
    public int[] decide(int[] yourMissiles, int[] opponentsMissiles) {
        int smallestIndex = Integer.MAX_VALUE;
        int smallestX = 0;
        int smallestY = 0;
        for (int i = 0; i < yourMissiles.length; i += 3) {
            if (yourMissiles[i] < smallestIndex) {
                smallestIndex = i;
                smallestX = yourMissiles[i + 1];
                smallestY = yourMissiles[i + 2];
            }
        }
        return new int[]{smallestIndex, smallestX + 1, smallestY};
    }

    @Override
    public String getName() {
        return "One At A Time";
    }

    @Override
    public void restart() {

    }

    @Override
    public void exit() {

    }
}
