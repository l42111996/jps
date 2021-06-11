package org.ksdev.jps.test;

import java.util.ArrayList;

/**
 * 只有xy的坐标系
 * Created by JinMiao
 * 2021/5/11.
 */
public class OffsetCoord {
    public OffsetCoord(int x, int y) {
        this.x = x;
        this.y = y;
        this.hex = OffsetCoord.qoffsetToCube(ODD,this);
    }
    private final Hex hex;
    //col
    public final int x;
    //row
    public final int y;
    /**
     * 我们使用odd
     */
    @Deprecated
    static public int EVEN = 1;
    static public int ODD = -1;

    /**
     * x,y,z坐标点系转 x,y坐标系点
     * @param offset
     * @param h
     * @return
     */
    static public OffsetCoord qoffsetFromCube(int offset, Hex h) {
        return qoffsetFromCube(offset,h.x,h.y,h.z);
    }

    public OffsetCoord add(OffsetCoord b) {
        return new OffsetCoord(x + b.x, y + b.y);
    }

    public OffsetCoord neighbor(int direction) {
        return qoffsetFromCube(ODD,this.hex.neighbor(direction));
    }

    /**
     * x,y,z坐标点系转 x,y坐标系点
     * @param offset
     * @return
     */
    static public OffsetCoord qoffsetFromCube(int offset, int x,int y ,int z) {
        int col = x;
        int row = z + ((x + offset * (x & 1)) / 2);
        if (offset != OffsetCoord.EVEN && offset != OffsetCoord.ODD) {
            throw new IllegalArgumentException("offset must be EVEN (+1) or ODD (-1)");
        }
        return new OffsetCoord(col, row);
    }

    @Override
    public String toString() {
        return "OffsetCoord{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    static public int qoffsetYFromCube(int x, int z){
        int row = z + ((x + -1 * (x & 1)) / 2);
        return row;
    }

    static public int qoffsetXFromCube(int x){
        int col = x;
        return col;
    }



    /**
     * x,y坐标系转 x,y,z坐标系
     * @param offset
     * @param h
     * @return
     */
    static public Hex qoffsetToCube(int offset, OffsetCoord h) {
        return qoffsetToCube(offset,h.x,h.y);
    }


    /**
     * x,y坐标系转 x,y,z坐标系
     * @param offset
     * @return
     */
    static public Hex qoffsetToCube(int offset, int x ,int y) {
        int q = x;
        int r = y - ((x + offset * (x & 1)) / 2);
        int s = -q - r;
        if (offset != OffsetCoord.EVEN && offset != OffsetCoord.ODD) {
            throw new IllegalArgumentException("offset must be EVEN (+1) or ODD (-1)");
        }
        return new Hex(q, s, r);
    }



    /**
     * z,y,z坐标系转双倍坐标系
     * 我们用不上
     * @param offset
     * @param h
     * @return
     */
    @Deprecated
    static public OffsetCoord roffsetFromCube(int offset, Hex h) {
        int col = h.x + ((h.z + offset * (h.z & 1)) / 2);
        int row = h.z;
        if (offset != OffsetCoord.EVEN && offset != OffsetCoord.ODD) {
            throw new IllegalArgumentException("offset must be EVEN (+1) or ODD (-1)");
        }
        return new OffsetCoord(col, row);
    }

    /**
     * x,y坐标系转 x,y,z坐标系
     * 我们用不上
     * @param offset
     * @param h
     * @return
     */
    @Deprecated
    static public Hex roffsetToCube(int offset, OffsetCoord h) {
        int q = h.x - ((h.y + offset * (h.y & 1)) / 2);
        int r = h.y;
        int s = -q - r;
        if (offset != OffsetCoord.EVEN && offset != OffsetCoord.ODD) {
            throw new IllegalArgumentException("offset must be EVEN (+1) or ODD (-1)");
        }
        return new Hex(q, s, r);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OffsetCoord that = (OffsetCoord) o;

        if (x != that.x) return false;
        return y == that.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public Hex getHex() {
        return hex;
    }
}

