package org.ksdev.jps.swing;

/**
 * Created by JinMiao
 * 2021/5/20.
 */

import org.ksdev.jps.Graph;
import org.ksdev.jps.JPS;
import org.ksdev.jps.Tile;
import org.ksdev.jps.astar.AStar;
import org.ksdev.jps.astar.Point;
import org.ksdev.jps.astar2.Node;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 自动寻路示例
 *
 * @author tiger
 * <p>
 * 鼠标点到哪里，则寻路到哪里。
 * 点到人物，则清理掉路径。
 * 红色方块为行动路径。绿色方块为人物。
 * 灰色方块可通行。黑色方块不可通行。
 */
public class FindPathTest extends JPanel implements MouseListener {

    private int size = 1;

    /**
     * 0为可通过点， 1为不可通过点
     */
    private byte[][] map ;

    private List<List<Tile>> tileList = new ArrayList<>();

    private JPS<Tile> jps;

    private AStar aStar;

    private org.ksdev.jps.astar2.AStar aStar2;
    /**
     * 人物位置
     */
    private int playerX, playerY = 2;


    private List<int[]> pathList = new LinkedList();

    /**
     * 目标位置
     */
    private int targetX, targetY;


    public FindPathTest() {
        int row, column;
        buildMap();;
        row = map.length;
        column = map[0].length;
        this.setPreferredSize(new Dimension(column * size, row * size));
        this.setFocusable(true);
        this.addMouseListener(this);

    }

    public void buildMap(){
        //构建地图信息
        int weith = 1600;
        int heith = 1600;
        map = new byte[heith][weith];
        //构建阻挡
        Random random = new Random();
        for (int i = 0; i < 500000; i++) {
            int x = random.nextInt(weith);
            int y = random.nextInt(heith);
            if(x==playerX&&y==playerY){
                continue;
            }
            map[x][y] = 1;
        }
        //playerX = random.nextInt(weith);
        //playerY = random.nextInt(heith);


        for (int y = 0; y < map.length; y++) {
            List<Tile> tiles = new ArrayList<>();
            for (int x = 0; x < map[y].length; x++) {
                Tile tile = new Tile(x,y);
                if(map[y][x]==1){
                    tile.setWalkable(false);
                }
                tiles.add(tile);
            }
            tileList.add(tiles);
        }

        jps = JPS.JPSFactory.getJPS(new Graph<>(tileList), Graph.Diagonal.ALWAYS);
        aStar = new AStar(map,Integer.MAX_VALUE);
        //固定阻挡
        //for (int i = 0; i < 4; i++) {
        //    map[2][i] = 1;
        //}
        //map[1][4] = 1;
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);

        //地图
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 0) {
                    g.setColor(Color.gray);
                } else if (map[i][j] == 1) {
                    g.setColor(Color.black);
                }
                g.fillRect(j * size, i * size, size, size);
            }
        }

        //演示路径
        g.setColor(Color.red);
        for (int i = 0; i < pathList.size(); i++) {
            int[] obj = (int[]) pathList.get(i);
            g.fillRect(obj[0] * size, obj[1] * size, size, size);
        }
        //人物
        g.setColor(Color.green);
        g.fillRect(playerX * size, playerY * size, size, size);
    }


    @Override
    public void mousePressed(MouseEvent e) {
        targetX = e.getX() / size;
        targetY = e.getY() / size;

        try {
            pathList.clear();
            if (targetX == playerX && targetY == playerY) //点击为当前位置，不做处理
            {
                this.repaint();
                return;
            }

            playerMove(); //人物运动到目标点
            repaint();
        } catch (Exception e2) {
            e2.printStackTrace();
        }


    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * 人物运动
     */
    @SuppressWarnings("unchecked")
    private void playerMove() {

        //System.out.println(targetX+"  "+targetY);
        //targetX = 1182;
        //targetY=1124;
        //Tile start = tileList.get(0).get(0);
        //Tile end = tileList.get(4).get(4);
        //playerY = 0;playerY = 0;
        //targetX = 4;targetY = 4;

        long startTime = System.currentTimeMillis();


        ////A* 寻路
        //List<Point> points = aStar.find(playerX,playerY,targetX,targetY);
        //pathList.clear();
        //for (Point point : points) {
        //    pathList.add(new int[]{point.getX(),point.getY()});
        //}
        //checkPoint();
        //System.out.println("A* 耗时:" +(System.currentTimeMillis() - startTime) +"路径"+points.size());
        //startTime = System.currentTimeMillis();

        if(map[targetY][targetX]==1){
            return;
        }
        //jps寻路
        Tile start = tileList.get(playerY).get(playerX);
        Tile end = tileList.get(targetY).get(targetX);
        Queue<Tile> path = null;
        try {
            path =jps.findPath(start,end,true,true).get();
            pathList.clear();
            for (Tile tile : path) {
                pathList.add(new int[]{tile.getX(),tile.getY()});
            }
            checkPoint();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("jps 耗时:" +(System.currentTimeMillis() - startTime)+"路径"+path.size());
        startTime = System.currentTimeMillis();

        //A82 寻路
        aStar2 = new org.ksdev.jps.astar2.AStar(map,playerX,playerY,targetX,targetY);
        List<Node> nodes = aStar2.search();
        pathList.clear();
        for (Node node : nodes) {
            pathList.add(new int[]{node.getX(),node.getY()});
        }
        checkPoint();
        System.out.println("A*2 耗时:" +(System.currentTimeMillis() - startTime) +"路径"+nodes.size());






    }

    private void checkPoint(){
        for (int[] ints : pathList) {
            int x = ints[0];
            int y = ints[1];
            if(map[y][x]==1){
                System.out.println("error");
            }
        }
    }



    public static void main(String[] args) {
        JFrame frame = new JFrame("A星寻路示例");
        JPanel panel = new FindPathTest();
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


}