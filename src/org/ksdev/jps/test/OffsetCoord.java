package org.ksdev.jps.test;

/**
 * 只有xy的坐标系
 * Created by JinMiao
 * 2021/5/11.
 */
public class OffsetCoord {
    public OffsetCoord(int x, int y) {
        this.x = x;
        this.y = y;
    }
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
        int col = h.x;
        int row = h.z + ((h.x + offset * (h.x & 1)) / 2);
        if (offset != OffsetCoord.EVEN && offset != OffsetCoord.ODD) {
            throw new IllegalArgumentException("offset must be EVEN (+1) or ODD (-1)");
        }
        return new OffsetCoord(col, row);
    }


    static public int qoffsetXFromCube(int x, int y){
        int row = y + ((x - (x & 1)) / 2);
        return row;
    }

    static public int qoffsetYFromCube(int x){
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
        int q = h.x;
        int r = h.y - ((h.x + offset * (h.x & 1)) / 2);
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
    public String toString() {
        return "OffsetCoord{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

