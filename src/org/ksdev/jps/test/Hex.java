package org.ksdev.jps.test;

import java.util.ArrayList;

/**
 * x,y,z 坐标系
 * Created by JinMiao
 * 2021/5/11.
 */
public class Hex {
    /**
     * 朝向
     */
    public static final int
            FACE_RIGHT_DOWN = 0,FACE_RIGHT_UP = 1,FACE_UP = 2,FACE_LEFT_UP = 3,FACE_LEFT_DOWN = 4,FACE_DOWN = 5,
            FACE_RIGHT_DIAGONAL = 6,FACE_RIGHT_UP_DIAGONAL = 7,FACE_LEFT_UP_DIAGONAL = 8,FACE_LEFT_DIAGONAL = 9,FACE_LEFT_DOWN_DIAGONAL = 10, FACE_RIGHT_DOWN_DIAGONAL = 11;

    public Hex(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        if (x + y + z != 0) throw new IllegalArgumentException("q + z + s must be 0");
    }

    /**
     * q
     */
    public final int x;
    /**
     * s
     */
    public final int y;
    /**
     * r
     */
    public final int z;

    public Hex add(Hex b) {
        return new Hex(x + b.x, y + b.y, z + b.z);
    }


    public Hex subtract(Hex b) {
        return new Hex(x - b.x, y - b.y, z - b.z);
    }


    public Hex scale(int k) {
        return new Hex(x * k, y * k, z * k);
    }


    public Hex rotateLeft() {
        return new Hex(-y, -z, -x);
    }


    public Hex rotateRight() {
        return new Hex(-z, -x, -y);
    }

    static public ArrayList<Hex> directions = new ArrayList<Hex>() {{
        add(new Hex(1, -1, 0));
        add(new Hex(1, 0, -1));
        add(new Hex(0, 1, -1));
        add(new Hex(-1, 1, 0));
        add(new Hex(-1, 0, 1));
        add(new Hex(0, -1, 1));


        add(new Hex(2, -1, -1));
        add(new Hex(1, 1, -2));
        add(new Hex(-1, 2, -1));
        add(new Hex(-2, 1, 1));
        add(new Hex(-1, -1, 2));
        add(new Hex(1, -2, 1));
    }};

    static public Hex direction(int direction) {
        return Hex.directions.get(direction);
    }

    public Hex neighbor(int direction) {
        return add(Hex.direction(direction));
    }

    /**
     * 根据邻居获得朝向
     * @param neighbor
     * @return
     */
    public int getDirection(Hex neighbor){
        //TODO 这里应该计算一个直线上线的值





        Hex directionHex = neighbor.subtract(this);
        for (int i = 0; i < directions.size(); i++) {
            Hex tempDirectionHex = direction(i);
            if(directionHex.x ==tempDirectionHex.x &&directionHex.y ==tempDirectionHex.y&&directionHex.z ==tempDirectionHex.z){
                return i;
            }
        }
        return -1;
    }



    public int length() {
        return (Math.abs(x) + Math.abs(z) + Math.abs(y)) / 2;
    }


    public int distance(Hex b) {
        return subtract(b).length();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hex hex = (Hex) o;

        if (x != hex.x) return false;
        if (z != hex.z) return false;
        return y == hex.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + z;
        result = 31 * result + y;
        return result;
    }

    public static long tolong(Hex hex){
        return tolong(hex.x, hex.z,hex.y);
    }

    public static long tolong(int x,int y ,int z){
        return x*10000_10000+y*10000+z;
    }


    @Override
    public String toString() {
        return OffsetCoord.qoffsetFromCube(OffsetCoord.ODD,this).toString();
    }
}