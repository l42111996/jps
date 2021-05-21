package org.ksdev.jps.test;

/**
 * 双倍坐标系
 * Created by JinMiao
 * 2021/5/11.
 */
public class DoubledCoord {
    public DoubledCoord(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public final int col;
    public final int row;

    static public DoubledCoord qdoubledFromCube(Hex h) {
        int col = h.x;
        int row = 2 * h.z + h.x;
        return new DoubledCoord(col, row);
    }


    public Hex qdoubledToCube() {
        int q = col;
        int r = (int) ((row - col) / 2);
        int s = -q - r;
        return new Hex(q, s, r);
    }


    static public DoubledCoord rdoubledFromCube(Hex h) {
        int col = 2 * h.x + h.z;
        int row = h.z;
        return new DoubledCoord(col, row);
    }


    public Hex rdoubledToCube() {
        int q = (int) ((col - row) / 2);
        int r = row;
        int s = -q - r;
        return new Hex(q, s, r);
    }

}