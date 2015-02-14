package controller;

import java.util.Arrays;

public class Field {

    public char[][] field;

    public Missile[] leftMissiles;
    public Missile[] rightMissiles;

    private int width;
    private int height;

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        field = new char[width][height];
        leftMissiles = new Missile[height];
        rightMissiles = new Missile[height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                field[x][y] = ' ';
            }
        }
        for (int i = 0; i < height; i++) {
            field[0][i] = '+';
            field[1][i] = '>';
            field[width - 2][i] = '<';
            field[width - 1][i] = '+';
            leftMissiles[i] = new Missile(1, i, i);
            rightMissiles[i] = new Missile(1, i, i);
        }
    }

    private static char getDirectionChar(int[] direction) {
        switch (direction[0]) {
            case 0:
                switch (direction[1]) {
                    case 1:
                        return 'V';
                    case -1:
                        return '^';
                }
            case 1:
                switch (direction[1]) {
                    case 1:
                        return '\\';
                    case 0:
                        return '-';
                    case -1:
                        return '/';
                }
        }
        throw new IllegalStateException();
    }

    public boolean isFinished() {
        for (int i = 0; i < height; i++) {
            if (field[0][i] != '+' || field[width - 1][i] != '+') {
                return true;
            }
        }
        for (Missile missile : leftMissiles) {
            if (!missile.destroyed) {
                return false;
            }
        }
        for (Missile missile : rightMissiles) {
            if (!missile.destroyed) {
                return false;
            }
        }
        return true;
    }

    public void flip() {
        char[][] newField = new char[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                newField[x][y] = flip(field[width - x - 1][y]);
            }
        }
        field = newField;
        Missile[] buffer = leftMissiles;
        leftMissiles = rightMissiles;
        rightMissiles = buffer;
    }

    private char flip(char c) {
        switch (c) {
            case '\\':
                return '/';
            case '/':
                return '\\';
            case '>':
                return '<';
            case '<':
                return '>';
            default:
                return c;
        }
    }

    public boolean hasWon() {
        for (int i = 0; i < height; i++) {
            if (field[width - 1][i] != '+') {
                return true;
            }
        }
        return false;
    }

    public int[] getOpponentMissiles() {
        return Arrays.stream(rightMissiles)
                .filter(a -> !a.destroyed)
                .map(a -> new int[]{a.index, a.coordinates[0], a.coordinates[1]})
                .map(a -> new int[]{a[0], width - a[1] - 1, a[2]})
                .reduce((a, b) -> {
                    int[] c = Arrays.copyOf(a, a.length + b.length);
                    System.arraycopy(b, 0, c, a.length, a.length + b.length - a.length);
                    return c;
                }).get();
    }

    public boolean hasLost() {
        for (int i = 0; i < height; i++) {
            if (field[0][i] != '+') {
                return true;
            }
        }
        return false;
    }

    public int[] getCurrentPlayersMissiles() {
        return Arrays.stream(leftMissiles)
                .filter(a -> !a.destroyed)
                .map(a -> new int[]{a.index, a.coordinates[0], a.coordinates[1]})
                .reduce((a, b) -> {
                    int[] c = Arrays.copyOf(a, a.length + b.length);
                    System.arraycopy(b, 0, c, a.length, a.length + b.length - a.length);
                    return c;
                }).get();
    }

    public void detonate() {
        for (Missile missile : leftMissiles) {
            if (missile.detonate) {
                missile.detonate();
            }
        }
    }

    private boolean isSolid(char c) {
        switch (c) {
            case '+':
            case '>':
            case '<':
                return true;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        String result = "";
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result += field[x][y];
            }
            result += '\n';
        }
        return result;
    }

    public class Missile {
        private int[] coordinates;
        private int[] direction;
        private boolean launched;
        private boolean destroyed;
        private boolean detonate;
        private int index;

        public Missile(int x, int y, int index) {
            coordinates = new int[]{x, y};
            launched = false;
            destroyed = false;
            direction = new int[]{1, 0};
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void move(int[] coordinates) {
            if (destroyed) {
                return;
            }
            if (field[this.coordinates[0]][this.coordinates[1]] != '>') {
                destroyed = true;
                return;
            }
            if (coordinates[0] == -1 && coordinates[1] == -1) {
                detonate = true;
                return;
            }
            if (coordinates[0] < 0 || coordinates[0] >= width || coordinates[1] < 0 || coordinates[1] >= height) {
                move();
                return;
            }
            int[] newDirection = new int[]{coordinates[0] - this.coordinates[0], coordinates[1], this.coordinates[1]};
            if (newDirection[0] < 0 || newDirection[0] > 1 || newDirection[1] < -1 || newDirection[1] > 1) {
                move();
                return;
            }
            if (!launched) {
                if (newDirection[0] == 0) {
                    return;
                }
                launched = true;
            } else if (Math.abs(direction[0] - newDirection[0]) + Math.abs(direction[1] - newDirection[1]) > 1) {//TODO
                move();
                return;
            }
            direction = newDirection;
            move();
        }

        public void move() {
            if (!launched || destroyed) {
                return;
            }
            if (field[coordinates[0]][coordinates[1]] != '>') {
                destroyed = true;
                return;
            }
            field[coordinates[0]][coordinates[1]] = getDirectionChar(direction);
            coordinates[0] += direction[0];
            coordinates[1] += direction[1];
            if (coordinates[0] < 0 || coordinates[0] >= width || coordinates[1] < 0 || coordinates[1] >= height) {
                coordinates[0] = Math.max(coordinates[0], 0);
                coordinates[0] = Math.min(coordinates[0], width - 1);
                coordinates[1] = Math.max(coordinates[0], 1);
                coordinates[1] = Math.min(coordinates[1], height - 1);
                detonate = true;
                return;
            }
            if (isSolid(field[coordinates[0]][coordinates[1]])) {
                detonate = true;
            } else {
                field[coordinates[0]][coordinates[1]] = '>';
            }
        }

        private void detonate() {
            for (int x = coordinates[0] - 2; x <= coordinates[0] + 2; x++) {
                for (int y = coordinates[1] - 2; y <= coordinates[1] + 2; y++) {
                    if (Math.abs(x - coordinates[0]) + Math.abs(y - coordinates[1]) > 2) {
                        continue;
                    }
                    if (x < 0 || x >= width || y < 0 || y >= height) {
                        continue;
                    }
                    field[x][y] = '#';
                }
            }
        }
    }
}
