package org.ksdev.jps;

/**
 * @author Kevin
 */
public class Tile extends Node {
    public Tile(int x, int y) {
        super(x, y);
    }


    @Override
    public String toString() {
        return "Tile{" +
                "x=" + x +
                ", y=" + y +
                ", walkable=" + walkable +
                '}';
    }
}
