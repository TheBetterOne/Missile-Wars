package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;

public class Submission extends Player {

    private String name;

    private BufferedReader reader;
    private PrintStream writer;

    public Submission(String name, String[] command) {
        this.name = name;
        Process process;
        try {
            process = Runtime.getRuntime().exec(command);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            writer = new PrintStream(process.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int[] decide(int[] yourMissiles, int[] opponentsMissiles) {
        writer.println(Arrays.toString(yourMissiles).replaceAll(",|\\[|\\]", "").trim() + "," + Arrays.toString(opponentsMissiles).replaceAll(",|\\[|\\]", "").trim());
        String output = "";
        try {
            output = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Arrays.stream(output.trim().split("\\D+")).mapToInt(Integer::parseInt).toArray();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void restart() {
        writer.println("restart");
    }

    @Override
    public void exit() {
        writer.println("exit");
    }
}
