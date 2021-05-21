package org.ksdev.jps.test;

import java.util.ArrayList;

/**
 * 布局
 * Created by JinMiao
 * 2021/5/11.
 */
public class Layout {
    public Layout(Orientation orientation, Point size, Point origin) {
        this.orientation = orientation;
        this.size = size;
        this.origin = origin;
    }

    public final Orientation orientation;
    public final Point size;
    /**
     * 原始坐标点  相当于0.0点
     */
    public final Point origin;
    static public Orientation pointy = new Orientation(Math.sqrt(3.0), Math.sqrt(3.0) / 2.0, 0.0, 3.0 / 2.0, Math.sqrt(3.0) / 3.0, -1.0 / 3.0, 0.0, 2.0 / 3.0, 0.5);
    static public Orientation flat = new Orientation(3.0 / 2.0, 0.0, Math.sqrt(3.0) / 2.0, Math.sqrt(3.0), 2.0 / 3.0, 0.0, -1.0 / 3.0, Math.sqrt(3.0) / 3.0, 0.0);

    /**
     * 坐标系转x,y像素
     * @param h
     * @return
     */
    public Point hexToPixel(Hex h) {
        Orientation M = orientation;
        double x = (M.f0 * h.x + M.f1 * h.z) * size.x;
        double y = (M.f2 * h.x + M.f3 * h.z) * size.y;
        return new Point(x + origin.x, y + origin.y);
    }


    /**
     * xy像素转 z,y,z像素
     * @param p
     * @return
     */
    public FractionalHex pixelToHex(Point p) {
        Orientation M = orientation;
        Point pt = new Point((p.x - origin.x) / size.x, (p.y - origin.y) / size.y);
        double q = M.b0 * pt.x + M.b1 * pt.y;
        double r = M.b2 * pt.x + M.b3 * pt.y;
        return new FractionalHex(q, r, -q - r);
    }

    /**
     *
     * @param corner
     * @return
     */
    public Point hexCornerOffset(int corner) {
        Orientation M = orientation;
        double angle = 2.0 * Math.PI * (M.start_angle - corner) / 6.0;
        return new Point(size.x * Math.cos(angle), size.y * Math.sin(angle));
    }


    public ArrayList<Point> polygonCorners(Hex h) {
        ArrayList<Point> corners = new ArrayList<Point>() {{
        }};
        Point center = hexToPixel(h);
        for (int i = 0; i < 6; i++) {
            Point offset = hexCornerOffset(i);
            corners.add(new Point(center.x + offset.x, center.y + offset.y));
        }
        return corners;
    }

}
