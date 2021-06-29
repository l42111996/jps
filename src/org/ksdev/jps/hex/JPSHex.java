package org.ksdev.jps.hex;

import org.ksdev.jps.test.Hex;
import org.ksdev.jps.test.OffsetCoord;

import java.util.*;

/**
 * Created by JinMiao
 * 2021/5/21.
 */
public abstract class JPSHex {
    protected byte[][] map;
    protected PriorityQueue<HexNode> open = new PriorityQueue<>(Comparator.comparingDouble(a -> a.getF()));
    protected Map<Hex,HexNode> openMap = new HashMap<>();
    protected HashSet<HexNode> closed = new HashSet<>();
    protected Map<HexNode, HexNode> parentMap = new HashMap<>();

    public JPSHex(byte[][] map) {
        this.map = map;
    }



    public List<Hex> search(Hex start, Hex end){
        Set<Hex> goals = new HashSet<>();
        for (int direction = 0; direction < 6; direction++) {
            Hex neighbor = end.neighbor(direction);
            if(isWalkable(neighbor)){
                goals.add(neighbor);
            }
        }
        HexNode startHexNode = new HexNode(start);
        open.add(startHexNode);
        openMap.put(start,startHexNode);
        while (!open.isEmpty()) {
            //System.out.println(open.size());
            // pop the position of node which has the minimum `f` value.
            HexNode node = open.poll();
            openMap.remove(node);
            // mark the current node as checked
            closed.add(node);
            //已经找到终点路径
            if (goals.contains(node.getHex())) {
                return backtrace(node);
            }
            // add all possible next steps from the current node
            identifySuccessors(node, end, goals);
        }
        return null;
    }



    /**
     * 找到所有邻居，把邻居设置为当前点 根据当前点进行跳点搜索 找到所有的跳点
     * Identify successors for the given node. Runs a JPS in direction of each available neighbor, adding any open
     * nodes found to the open list.
     * @return All the nodes we have found jumpable from the current node
     */
    private void identifySuccessors(HexNode node, Hex goal, Set<Hex> goals) {
        //找到周围邻居
        Collection<Integer> neighborsDirections = findNeighborsDirections(node, parentMap);

        for (int direction : neighborsDirections) {
            // jump in the direction of our neighbor
            //根据朝向搜索跳点
            Hex jumpNode = node.getHex();
            boolean isJumpPoint = false;
            for(;;){
                if(jumpNode==null){
                    break;
                }
                if (!isWalkable(jumpNode)){
                    break;
                }
                if (goals.contains(jumpNode)){
                    isJumpPoint = true;
                    break;
                }
                if(jump(direction, jumpNode, goals)){
                    jumpNode = jumpNode.neighbor(direction);
                    isJumpPoint = true;
                    break;
                }
                jumpNode = jumpNode.neighbor(direction);
            }


            if(!isJumpPoint){
                continue;
            }

            // don't add a node we have already gotten to quicker
            if (jumpNode == null ||!isWalkable(jumpNode)|| closed.contains(jumpNode)) {
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

        for(;;){
            HexNode parent = node.getParentNode();
            if(parent==null){
                break;
            }
            steps = node.getHex().distance(parent.getHex());
            dx = Integer.compare(node.getHex().x, parent.getHex().x);
            dy = Integer.compare(node.getHex().y, parent.getHex().y);
            dz = Integer.compare(node.getHex().z, parent.getHex().z);
            temp = node.getHex();
            for (int i = 0; i < steps; i++) {
                temp = temp.subtract(new Hex(dx, dy, dz));
                path.addFirst(temp);
            }
            node = parent;

        }
        return path;
    }

    protected boolean isWalkable(Hex hex){
        OffsetCoord offsetCoord = OffsetCoord.qoffsetFromCube(OffsetCoord.ODD,hex);
        if(offsetCoord.x<0||offsetCoord.y<0){
            return false;
        }
        if(offsetCoord.x>=map.length||offsetCoord.y>=map[0].length)
        {
            return false;
        }

        byte b = map[offsetCoord.x][offsetCoord.y];
        if(b == 1){
            return false;
        }
        return true;
    }


    /**
     * Find all neighbors for a given node. If node has a parent then prune neighbors based on JPS algorithm,
     * otherwise return all neighbors.
     */
    protected abstract Set<Integer> findNeighborsDirections(HexNode node, Map<HexNode, HexNode> parentMap);

    /**
     * Search towards the child from the parent, returning when a jump point is found.
     * 从父级向子级搜索，找到跳转点后返回。
     */
    protected abstract boolean jump(int direction, Hex current, Set<Hex> goals);

}
