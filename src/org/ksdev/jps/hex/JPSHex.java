package org.ksdev.jps.hex;

import org.ksdev.jps.test.Hex;

import java.util.*;

/**
 * Created by JinMiao
 * 2021/5/21.
 */
public abstract class JPSHex {
    protected Map<Hex,Byte> map = new HashMap<>();
    private PriorityQueue<HexNode> open = new PriorityQueue<>();
    private Map<Hex,HexNode> openMap = new HashMap<>();
    private HashSet<HexNode> closed = new HashSet<>();
    private Map<HexNode, HexNode> parentMap = new HashMap<>();

    public List<Hex> find(Hex start,Hex end){
        Set<Hex> goals = new HashSet<>();
        for (int direction = 0; direction < 6; direction++) {
            Hex neighbor = end.neighbor(direction);
            if(isWalkable(neighbor)){
                goals.add(neighbor);
            }
        }

        open.add(new HexNode(start));
        while (!open.isEmpty()) {
            //System.out.println(open.size());
            // pop the position of node which has the minimum `f` value.
            HexNode node = open.poll();
            // mark the current node as checked
            closed.add(node);
            //已经找到终点路径
            if (goals.contains(node)) {
                return backtrace(node);
            }
            // add all possible next steps from the current node
            identifySuccessors(node, end, goals, open, closed, parentMap);
        }
        return null;

    }



    /**
     * 找到所有邻居，把邻居设置为当前点 根据当前点进行跳点搜索 找到所有的跳点
     * Identify successors for the given node. Runs a JPS in direction of each available neighbor, adding any open
     * nodes found to the open list.
     * @return All the nodes we have found jumpable from the current node
     */
    private void identifySuccessors(HexNode node, Hex goal, Set<Hex> goals, Queue<HexNode> open, Set<HexNode> closed, Map<HexNode, HexNode> parentMap) {
        // get all neighbors to the current node
        Collection<Hex> neighbors = findNeighbors(node, parentMap);

        double d;
        double ng;
        for (Hex neighbor : neighbors) {
            // jump in the direction of our neighbor
            Hex jumpNode = jump(neighbor, node, goals);

            // don't add a node we have already gotten to quicker
            if (jumpNode == null || closed.contains(jumpNode)) {
                continue;
            }
            //计算当前阶段的 g h f
            HexNode newNode = new HexNode(node,jumpNode);
            newNode.calc(goal,10);


            HexNode existN = this.openMap.get(jumpNode);

            if(existN!=null){
                if(newNode.getG() < existN.getG()){
                    open.remove(existN);
                    existN.setG(newNode.getG());
                    existN.setF(newNode.getF());
                    existN.setH(newNode.getH());
                    existN.setParentNode(newNode.getParentNode());
                    open.add(existN);
                    this.openMap.put(existN.getHex(),existN);
                }
            }//不在开启列表中，则添加进去
            else{
                open.add(newNode);
                this.openMap.put(newNode.getHex(),newNode);
            }
        }
    }


    /**
     * 算出从终点到起始点的路径点集合
     * 因为parent都是跳点不连续的所以需要算出来
     * Returns a path of the parent nodes from a given node.
     */
    private List<Hex> backtrace(HexNode node) {
        LinkedList<Hex> path = new LinkedList<>();
        path.add(node.getHex());

        int dx, dy,dz;
        int steps;
        Hex temp;
        HexNode parent = node.getParentNode();
        while (parent!=null) {
            steps = node.getHex().distance(parent.getHex());
            dx = Integer.compare(node.getHex().x, parent.getHex().x);
            dy = Integer.compare(node.getHex().y, parent.getHex().y);
            dz = Integer.compare(node.getHex().z, parent.getHex().z);
            temp = node.getHex();
            for (int i = 0; i < steps; i++) {
                temp = temp.add(new Hex(dx,dy,dz));
                path.addFirst(temp);
            }
            node = parent;
        }
        return path;
    }

    private boolean isWalkable(Hex hex){
        Byte b = map.get(Hex.tolong(hex));
        if(b==null ||b==1){
            return false;
        }
        return true;
    }


    /**
     * Find all neighbors for a given node. If node has a parent then prune neighbors based on JPS algorithm,
     * otherwise return all neighbors.
     */
    protected abstract Set<Hex> findNeighbors(HexNode node, Map<HexNode, HexNode> parentMap);

    /**
     * Search towards the child from the parent, returning when a jump point is found.
     * 从父级向子级搜索，找到跳转点后返回。
     */
    protected abstract Hex jump(Hex neighbor, HexNode current, Set<Hex> goals);

}
