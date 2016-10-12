package com.mcelrea;

public class WumpusTile {
    public static final int BLACK=1,
                            CAVE=2,
                            GLITTER=3,
                            GOLD=4,
                            GROUND=5,
                            SPIDER=6,
                            STINK=7,
                            WEB=8,
                            WIND=9,
                            WUMPUS=10;
    private int type = GROUND;

    public WumpusTile() {}

    public WumpusTile(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
