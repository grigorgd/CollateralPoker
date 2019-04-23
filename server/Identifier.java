package server;

public class Identifier {

    private static final int BOTTOM_RANGE = 10000;
    private static final int TOP_RANGE = 99999;

    public static int getIdentifier() {
        int id = (int) (Math.random() * (TOP_RANGE - BOTTOM_RANGE) + BOTTOM_RANGE);
        return id;
    }
}
