package org.ksdev.jps.hex;


import org.ksdev.jps.test.Hex;
import org.ksdev.jps.test.OffsetCoord;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StaticHexagonMapGUI {

    private JFrame frame;
    private JPanel canvas;

    //格子边大小
    static double outR = 10;
    static int width = 1800;
    static int height = 1200;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                StaticHexagonMapGUI window = new StaticHexagonMapGUI();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public StaticHexagonMapGUI() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {

        frame = new JFrame();
        frame.setBounds(100, 100, width, height);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        canvas = new Canvas();
        canvas.setBounds(30, 30, width+100, height+100);
        canvas.setBackground(new Color(244, 244, 244));
        frame.getContentPane().add(canvas);

        JButton btnNewButton = new JButton("刷新");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.repaint();
            }
        });
        btnNewButton.setBounds(10, 10, 93, 23);
        frame.getContentPane().add(btnNewButton);
    }

    public class Canvas extends JPanel {

        double innerR = outR / 2 * Math.sqrt(3);

        public Canvas() {
             repaint();
        }

        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(0, 0, 0));// 边线颜色

            //List<Coordinate> totalC = new ArrayList<>();
            List<Hex> totalC = new ArrayList<>();


            int sizeX = 11;
            int sizeY = 8;

            byte[][] map = new byte[sizeX][sizeY];
            int tx = 9;
            int ty = 6;
            int sx = 3;
            int sy = 6;

            for (int i = 0; i < sizeX; i++) {
                for (int j = 0; j < sizeY; j++) {
                    OffsetCoord offsetCoord = new OffsetCoord(i,j);
                    Hex hex = OffsetCoord.qoffsetToCube(OffsetCoord.ODD,offsetCoord);
                    totalC.add(hex);
                }
            }

            // 画地图
            for (Hex hex : totalC) {
                drawByIndex(g2d, OffsetCoord.qoffsetFromCube(OffsetCoord.ODD,hex), new Color(255, 255, 0));
            }

            // 随机障碍物

            map[5][3] = 1;
            drawByIndex(g2d, new OffsetCoord(5,3), new Color(255, 0, 0));

            map[6][3] = 1;
            drawByIndex(g2d, new OffsetCoord(6,3), new Color(255, 0, 0));
            map[6][4] = 1;
            drawByIndex(g2d, new OffsetCoord(6,4), new Color(255, 0, 0));
            map[6][5] = 1;
            drawByIndex(g2d, new OffsetCoord(6,5), new Color(255, 0, 0));
            map[6][6] = 1;
            drawByIndex(g2d, new OffsetCoord(6,6), new Color(255, 0, 0));
            map[6][7] = 1;
            drawByIndex(g2d, new OffsetCoord(6,7), new Color(255, 0, 0));

            drawByIndex(g2d, new OffsetCoord(tx,ty), new Color(155,233,188));
            drawByIndex(g2d, new OffsetCoord(sx,sy), new Color(155,233,188));




            //for (Hex hex : totalC) {
            //    if (getFloat() < 0.2) {
            //        OffsetCoord offsetCoord = OffsetCoord.qoffsetFromCube(OffsetCoord.ODD,hex);
            //        if(offsetCoord.x == tx &&offsetCoord.y == ty){
            //            continue;
            //        }
            //        if(offsetCoord.x == sx &&offsetCoord.y == sy){
            //            continue;
            //        }
            //        map[offsetCoord.x][offsetCoord.y] = 1;
            //        //map.put(hex, (byte) 1);
            //        drawByIndex(g2d, offsetCoord, new Color(255, 0, 0));
            //    }
            //}

            OffsetCoord start = new OffsetCoord(sx,sy);
            OffsetCoord end = new OffsetCoord(tx,ty);

            long startTime = System.currentTimeMillis();
            //LinkedList<OffsetCoord> path = null;
            ////
            //AStarOffsetCoord aStarHex = new AStarOffsetCoord(map);
            //path = aStarHex.search(sx,sy,tx,ty,true);
            //if (path != null) {
            //    System.out.println("a* 耗时"+(System.currentTimeMillis()-startTime)+" 路径 "+path.size());
            //    List<Hex> aPath = path.stream().map(offsetCoord -> OffsetCoord.qoffsetToCube(OffsetCoord.ODD,offsetCoord)).collect(Collectors.toList());
            //    checkPoint(map,aPath);
            //     for (OffsetCoord coordinate : path) {
            //     drawByIndex(g2d,coordinate,new Color(155,233,188));
            //     }
            //}else{
            //    System.out.println("a* 终点没有找到"+end);
            //}
            //
            //
            //

            startTime = System.currentTimeMillis();
            JpsOffsetCoord jpsHex = new JpsOffsetCoord(map);
            List<OffsetCoord> jpsPath = jpsHex.search(sx,sy,tx,ty,false);
            if (jpsPath != null) {
                System.out.println("JPS 耗时"+(System.currentTimeMillis()-startTime)+" 路径 "+jpsPath.size());
                //checkPoint(map,jpsPath);
                // for (Coordinate coordinate : apath.openList) {
                // drawByIndex(g2d,coordinate,new Color(155,233,188));
                // }
//				for (Coordinate coordinate : apath.closeList) {
//					drawByIndex(g2d, coordinate, new Color(123, 70, 188));
//				}
                for (OffsetCoord node : jpsPath) {
                    drawByIndex(g2d, node, new Color(100, 50, 255));
                }

            }else{
                System.out.println("JPS 终点没有找到"+end+" 耗时 "+(System.currentTimeMillis()-startTime));
            }
        }





        private void checkPoint(byte[][] map,List<Hex> pathList){
            //判断阻挡
            for (Hex hex : pathList) {
                OffsetCoord offsetCoord = OffsetCoord.qoffsetFromCube(OffsetCoord.ODD,hex);
                byte block = map[offsetCoord.x][offsetCoord.y];
                if(block==1){
                    System.out.println("error");
                }
            }
            //检测连续性
            Hex parent = null;
            for (Hex hex : pathList) {
                if(parent==null){
                    parent = hex;
                    continue;
                }
                int distance = parent.distance(hex);
                if(distance>1||distance==0){
                    System.out.println("error distance"+distance);
                }
                parent = hex;
            }
        }


        void drawByIndex(Graphics2D g2d, OffsetCoord coordinate, Color color) {
            double x = outR + coordinate.x * (outR * 1.5);
            double y = outR + coordinate.y * (innerR * 2);
            if (coordinate.x % 2 == 1) {
                y += innerR;
            }
            drawHexSolid(g2d, new Point(x, y), color);
            //Font font = new Font("Courier", Font.PLAIN, (int) (outR / 2));
            //g2d.setFont(font);
            //g2d.drawString("(" + coordinate.x + "," + coordinate.y + ")", (int) (x - outR / 1.5), (int) (y + outR / 4));
            //g2d.drawString(coordinate.F + "-" + coordinate.G + "-" + coordinate.H, (int) (x - outR / 1.5),
            //        (int) (y + outR / 1.5));
        }

        // 画边
        void drawHexSide(Graphics2D g2d, Point mid) {
            Point point1 = new Point(mid.x + (outR / 2), mid.y + innerR);
            Point point2 = new Point(mid.x + outR, mid.y);
            Point point3 = new Point(mid.x + (outR / 2), mid.y - innerR);
            Point point4 = new Point(mid.x - (outR / 2), mid.y - innerR);
            Point point5 = new Point(mid.x - outR, mid.y);
            Point point6 = new Point(mid.x - (outR / 2), mid.y + innerR);

            g2d.drawLine(point1.getIntX(), point1.getIntY(), point2.getIntX(), point2.getIntY());
            g2d.drawLine(point2.getIntX(), point2.getIntY(), point3.getIntX(), point3.getIntY());
            g2d.drawLine(point3.getIntX(), point3.getIntY(), point4.getIntX(), point4.getIntY());
            g2d.drawLine(point4.getIntX(), point4.getIntY(), point5.getIntX(), point5.getIntY());
            g2d.drawLine(point5.getIntX(), point5.getIntY(), point6.getIntX(), point6.getIntY());
            g2d.drawLine(point6.getIntX(), point6.getIntY(), point1.getIntX(), point1.getIntY());

        }

        // 画实心
        void drawHexSolid(Graphics2D g2d, Point mid, Color color) {
            Point point1 = new Point(mid.x + (outR / 2), mid.y + innerR);
            Point point2 = new Point(mid.x + outR, mid.y);
            Point point3 = new Point(mid.x + (outR / 2), mid.y - innerR);
            Point point4 = new Point(mid.x - (outR / 2), mid.y - innerR);
            Point point5 = new Point(mid.x - outR, mid.y);
            Point point6 = new Point(mid.x - (outR / 2), mid.y + innerR);

            GeneralPath gp = new GeneralPath(); // shape的子类，表示一个形状
            gp.append(new Line2D.Double(point1.x, point1.y, point2.x, point2.y), true); // 在形状中添加一条线，即Line2D
            gp.lineTo(point3.x, point3.y); // 添加一个点,并和之前的线段相连
            gp.lineTo(point4.x, point4.y); // 同上
            gp.lineTo(point5.x, point5.y); // 同上
            gp.lineTo(point6.x, point6.y); // 同上
            gp.closePath(); // 关闭形状创建

            Color temp = g2d.getColor();
            g2d.setColor(color);
            g2d.fill(gp);
            g2d.setColor(temp);
            g2d.draw(gp);

        }

    }

    public class Point {
        public double x;
        public double y;

        Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public int getIntX() {
            return (int) Math.round(x);
        }

        public int getIntY() {
            return (int) Math.round(y);
        }
    }


    static Random random = new Random();
    public static int getIndex(int sizeY) {
        return random.nextInt(sizeY);
    }

    public static float getFloat() {
        return random.nextFloat();
    }
}