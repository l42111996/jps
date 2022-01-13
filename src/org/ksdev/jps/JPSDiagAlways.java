package org.ksdev.jps;

import java.util.*;

/**
 * @author Kevin
 */
public class JPSDiagAlways<T extends Node> extends JPS<T> {
    public JPSDiagAlways(Graph<T> graph) { super(graph); }

    @Override
    protected Set<T> findNeighbors(T node, Map<T, T> parentMap) {
        Set<T> neighbors = new HashSet<>();

        Node parent = parentMap.get(node);

        // directed pruning: can ignore most neighbors, unless forced. 定向修剪：可以忽略大多数邻居，除非强制。
        if (parent != null) {
            final int x = node.x;
            final int y = node.y;
            // get normalized direction of travel 获得归一化的行进方向
            final int dx = (x - parent.x) / Math.max(Math.abs(x - parent.x), 1);
            final int dy = (y - parent.y) / Math.max(Math.abs(y - parent.y), 1);

            // search diagonally
            if (dx != 0 && dy != 0) {
                if (graph.isWalkable(x, y + dy)) 
                    neighbors.add(graph.getNode(x, y + dy));
                if (graph.isWalkable(x + dx, y)) 
                    neighbors.add(graph.getNode(x + dx, y));
                if (graph.isWalkable(x + dx, y + dy)) 
                    neighbors.add(graph.getNode(x + dx, y + dy));
                if (!graph.isWalkable(x - dx, y)) 
                    neighbors.add(graph.getNode(x - dx, y + dy));
                if (!graph.isWalkable(x, y - dy)) 
                    neighbors.add(graph.getNode(x + dx, y - dy));
            } else { // search horizontally/vertically
                if (dx == 0) {
                    //上下可以走
                    if (graph.isWalkable(x, y + dy)) 
                        neighbors.add(graph.getNode(x, y + dy));
                    //右边不可走，走右上
                    if (!graph.isWalkable(x + 1, y)) 
                        neighbors.add(graph.getNode(x + 1, y + dy));
                    //左边不可走  走左上
                    if (!graph.isWalkable(x - 1, y)) 
                        neighbors.add(graph.getNode(x - 1, y + dy));
                } else {
                    //左右可以走
                    if (graph.isWalkable(x + dx, y)) 
                        neighbors.add(graph.getNode(x + dx, y));
                    //上面不可走  走左右上
                    if (!graph.isWalkable(x, y + 1)) 
                        neighbors.add(graph.getNode(x + dx, y + 1));
                    //下面不可走 走左右下
                    if (!graph.isWalkable(x, y - 1)) 
                        neighbors.add(graph.getNode(x + dx, y - 1));
                }
            }
        } else {
            // no parent, return all neighbors
            neighbors.addAll(graph.getNeighborsOf(node, Graph.Diagonal.ALWAYS));
        }

        return neighbors;
    }

    @Override
    protected T jump(T neighbor, T current, Set<T> goals) {
        if (neighbor == null || !neighbor.walkable) return null;
        if (goals.contains(neighbor)) return neighbor;

        int dx = neighbor.x - current.x;
        int dy = neighbor.y - current.y;

        // check for forced neighbors
        // check along diagonal
        if (dx != 0 && dy != 0) {
            //判断对角线的点是否是跳点  原理  当前点到对角线方向的 垂直和水平下一个点都是阻挡  下下个点不是阻挡  则对角线为跳点
            //向垂直水平方向判断是否是跳点 w:可走 c:current b:阻挡 n:邻居
            //  w
            //
            //  b   n
            //
            //  c   b   w
            if ((graph.isWalkable(neighbor.x - dx, neighbor.y + dy) && !graph.isWalkable(neighbor.x - dx, neighbor.y)) ||
                    (graph.isWalkable(neighbor.x + dx, neighbor.y - dy) && !graph.isWalkable(neighbor.x, neighbor.y - dy))) {
                return neighbor;
            }
            // when moving diagonally, must check for vertical/horizontal jump points  对角线移动时，必须检查垂直/水平跳跃点
            if (jump(graph.getNode(neighbor.x + dx, neighbor.y), neighbor, goals) != null ||
                    jump(graph.getNode(neighbor.x, neighbor.y + dy), neighbor, goals) != null) {
                return neighbor;
            }
        } else { // check horizontally/vertically
            if (dx != 0) {
            //水平方向检测  原理 当前点到邻居垂直方向 邻居上或下有阻挡  且阻挡的垂直方向的下一个点没有阻挡
            //      b   w
            //
            //  c   n
            //
            //      b   w
            if ((graph.isWalkable(neighbor.x + dx, neighbor.y + 1) && !graph.isWalkable(neighbor.x, neighbor.y + 1)) ||
                    (graph.isWalkable(neighbor.x + dx, neighbor.y - 1) && !graph.isWalkable(neighbor.x, neighbor.y - 1))) {
                return neighbor;
            }
            } else {
                //垂直方向检测
                if ((graph.isWalkable(neighbor.x + 1, neighbor.y + dy) && !graph.isWalkable(neighbor.x + 1, neighbor.y)) ||
                        (graph.isWalkable(neighbor.x - 1, neighbor.y + dy) && !graph.isWalkable(neighbor.x - 1, neighbor.y))) {
                    return neighbor;
                }
            }
        }

        // jump diagonally towards our goal
        //沿着对角线继续向下找邻居
        return jump(graph.getNode(neighbor.x + dx, neighbor.y + dy), neighbor, goals);
    }
}
