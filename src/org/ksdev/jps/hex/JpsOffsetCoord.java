package org.ksdev.jps.hex;

import org.ksdev.jps.hexAstar.NodeOffsetCoord;
import org.ksdev.jps.test.Hex;
import org.ksdev.jps.test.OffsetCoord;

import java.util.*;

/**
 * Created by JinMiao
 * 2022/1/14.
 */
public class JpsOffsetCoord {
    private PriorityQueue<NodeOffsetCoord> openList = new PriorityQueue<>();
    //private NodeOffsetCoord[][] openMap;

    private Map<Integer,NodeOffsetCoord> openMap = new HashMap<>();

    //地图阻挡信息
    private final byte[][] map;
    private final int width;
    private final int height;
    private NodeOffsetCoord endN;
    private long maxSearchTime = 2000;

    public JpsOffsetCoord(byte[][] map){
        this.map = map;
        this.width = map.length;
        this.height = map[0].length;
        //openMap = new NodeOffsetCoord[width][height];
    }



    public LinkedList<OffsetCoord> search(int x,int y,int tx,int ty,boolean incloudEndAround){
        LinkedList<OffsetCoord> result = new LinkedList<>();
        if(x==tx&&y==ty){
            return result;
        }
        long startMill = System.currentTimeMillis();
        OffsetCoord endOffsetCoord = new OffsetCoord(tx,ty);
        NodeOffsetCoord n = searchPath(new OffsetCoord(x,y),endOffsetCoord,incloudEndAround);
        if(n!=null){
            int closeCount = 0;
            for (NodeOffsetCoord value : openMap.values()) {
                if(value.getState()==2){
                    closeCount++;
                }
            }
            System.out.println("Jps 关闭列表长度"+closeCount);

            backtrace(result, n);

            if(!n.getOffsetCoord().equals(endOffsetCoord)&&!checkBound(endOffsetCoord,this.width,this.height)&&!isBlock(endOffsetCoord,this.map)){
                result.add(new OffsetCoord(tx,ty));
            }
        }else{
            //System.out.println("a*寻路没有找到路径 x "+ x+" y "+y+" tx "+tx+" ty "+ty);
            //SystemLogger.mapLogger.error("a*寻路没有找到路径 x {} y {} tx {} ty {}",x,y,tx,ty);
        }
        long useTime = System.currentTimeMillis()-startMill;
        if(useTime>100){
            //SystemLogger.mapLogger.info("a* 寻路结束 路径长度 {} 耗时 {}",result!=null?result.size():0,System.currentTimeMillis()-startMill);
        }
        if(!result.isEmpty()){
            //result = Floyd.floydSmooth(result,this.map);
            result.removeFirst();
        }
        //for (OffsetCoord offsetCoord : result) {
        //	System.out.println(" x "+offsetCoord.x+" y "+offsetCoord.y);
        //
        //}
        return result;
    }


    public NodeOffsetCoord searchPath(OffsetCoord start, OffsetCoord end, boolean incloudEndAround){
        NodeOffsetCoord startN =  new NodeOffsetCoord(start);
        NodeOffsetCoord endN = new NodeOffsetCoord(end);

        this.endN = endN;
        //起点先添加到开启列表中
        openList.add(startN);
        openMap.put(start.toInt(),startN);
        //开启列表中有节点的话，取出第一个节点，即最小F值的节点
        NodeOffsetCoord n;

        Set<OffsetCoord> endAround = new HashSet<>();
        if(incloudEndAround){
            for (int direction = 0; direction < 6; direction++) {
                endAround.add(endN.getOffsetCoord().neighbor(direction));
            }
        }
        endAround.add(endN.getOffsetCoord());
        boolean isFind = false;
        long startTime = System.currentTimeMillis();
        while((n = openList.poll()) != null){
            if(System.currentTimeMillis()-startTime>maxSearchTime){
                break;
            }
            OffsetCoord curOffsetCoord = n.getOffsetCoord();
            NodeOffsetCoord offsetCoord = this.openMap.get(curOffsetCoord.toInt());
            //判断此节点是否是目标点，是则找到了，跳出
            if(endAround.contains(curOffsetCoord)){
                isFind = true;
                break;
            }
            offsetCoord.setState(2);
            identifySuccessors(n, endAround);
        }

        if(isFind){
            return n;
        }else{
            return null;
        }
    }

    /**
     * Find all neighbors for a given node. If node has a parent then prune neighbors based on JPS algorithm,
     * otherwise return all neighbors.
     */
    protected Set<Integer> findNeighborsDirections(NodeOffsetCoord node) {
        Set<Integer> hexes = new HashSet<>();
        //这里应该有邻居的裁剪
        final NodeOffsetCoord parent = node.getParentNode();
        final OffsetCoord curOffsetCoord = node.getOffsetCoord();
        final Hex curHex = curOffsetCoord.getHex();

        if(parent==null){
            for (int direction = 0; direction < 6; direction++) {
                hexes.add(direction);
            }
        }else{
            final Hex parentHex = parent.getOffsetCoord().getHex();;

            final int x = curHex.x;
            final int y = curHex.y;
            final int z = curHex.z;
            final int dx = (x - parentHex.x) / Math.max(Math.abs(x - parentHex.x), 1);
            final int dy = (y - parentHex.y) / Math.max(Math.abs(y - parentHex.y), 1);
            final int dz = (z - parentHex.z) / Math.max(Math.abs(z - parentHex.z), 1);

            Hex directionHex = new Hex(dx,dy,dz);
            int direction = directionHex.getDirection();
            switch (direction){
                //右下
                case Hex.FACE_RIGHT_DOWN:
                    hexes.add(Hex.FACE_RIGHT_UP);
                    hexes.add(Hex.FACE_DOWN);
                    break;
                case Hex.FACE_UP:
                    hexes.add(Hex.FACE_LEFT_UP);
                    hexes.add(Hex.FACE_RIGHT_UP);
                    break;
                case Hex.FACE_LEFT_DOWN:
                    hexes.add(Hex.FACE_LEFT_UP);
                    hexes.add(Hex.FACE_DOWN);
                    break;
                //单向没问题
                case Hex.FACE_RIGHT_UP:
                    if(!isWalkable(curHex.neighbor(Hex.FACE_DOWN))&&isWalkable(curHex.neighbor(Hex.FACE_RIGHT_DOWN))){
                        hexes.add(Hex.FACE_RIGHT_DOWN);
                    }
                    if(!isWalkable(curHex.neighbor(Hex.FACE_LEFT_UP))&&isWalkable(curHex.neighbor(Hex.FACE_UP))){
                        hexes.add(Hex.FACE_UP);
                    }
                    break;
                case Hex.FACE_LEFT_UP:
                    if(!isWalkable(curHex.neighbor(Hex.FACE_RIGHT_UP))&&isWalkable(curHex.neighbor(Hex.FACE_UP))){
                        hexes.add(Hex.FACE_UP);
                    }
                    if(!isWalkable(curHex.neighbor(Hex.FACE_DOWN))&&isWalkable(curHex.neighbor(Hex.FACE_LEFT_DOWN))){
                        hexes.add(Hex.FACE_LEFT_DOWN);
                    }
                    break;
                case Hex.FACE_DOWN:
                    if(!isWalkable(curHex.neighbor(Hex.FACE_LEFT_UP))&&isWalkable(curHex.neighbor(Hex.FACE_LEFT_DOWN))){
                        hexes.add(Hex.FACE_LEFT_DOWN);
                    }
                    if(!isWalkable(curHex.neighbor(Hex.FACE_RIGHT_UP))&&isWalkable(curHex.neighbor(Hex.FACE_RIGHT_DOWN))){
                        hexes.add(Hex.FACE_RIGHT_DOWN);
                    }
                    break;
                default:
                    System.err.println("错误朝向"+direction);
            }
            hexes.add(direction);
        }
        return hexes;
    }


    /**
     * 找到所有邻居，把邻居设置为当前点 根据当前点进行跳点搜索 找到所有的跳点
     * Identify successors for the given node. Runs a JPS in direction of each available neighbor, adding any open
     * nodes found to the open list.
     * @return All the nodes we have found jumpable from the current node
     */
    private void identifySuccessors(NodeOffsetCoord node, Set<OffsetCoord> endAround) {
        //找到周围邻居
        Collection<Integer> neighborsDirections = findNeighborsDirections(node);

        for (int direction : neighborsDirections) {
            // jump in the direction of our neighbor
            //根据朝向搜索跳点
            final OffsetCoord nodeOffsetCoord = node.getOffsetCoord();
            OffsetCoord neighbor = nodeOffsetCoord.neighbor(direction);

            OffsetCoord jumpNode = jump(neighbor, endAround,direction);
            // don't add a node we have already gotten to quicker
            if (jumpNode == null) {
                continue;
            }
            checkNewNode(jumpNode,node);
        }
    }


    /**
     * 算出从终点到起始点的路径点集合
     * 因为parent都是跳点不连续的所以需要算出来
     * Returns a path of the parent nodes from a given node.
     */
    private void backtrace(LinkedList<OffsetCoord> result,NodeOffsetCoord node) {
        result.add(node.getOffsetCoord());

        int dx, dy,dz;
        int steps;
        Hex temp;

        for(;;){
            final NodeOffsetCoord parent = node.getParentNode();
            if(parent==null){
                break;
            }
            final Hex parentHex = parent.getOffsetCoord().getHex();
            final Hex curHex = node.getOffsetCoord().getHex();


            steps = curHex.distance(parentHex);
            dx = Integer.compare(curHex.x, parentHex.x);
            dy = Integer.compare(curHex.y, parentHex.y);
            dz = Integer.compare(curHex.z, parentHex.z);
            temp = curHex;
            for (int i = 0; i < steps; i++) {
                temp = temp.subtract(new Hex(dx, dy, dz));
                result.addFirst(node.getOffsetCoord());
            }
            node = parent;

        }
    }


    private void checkNewNode(OffsetCoord neighbor, NodeOffsetCoord parentN){
        NodeOffsetCoord existN = this.openMap.get(neighbor.toInt());
        //检查是否已在关闭列表中
        if(existN!=null&&existN.getState()==2){
            return;
        }
        NodeOffsetCoord newNode = new NodeOffsetCoord(neighbor, parentN);
        //检查地图是否障碍点
        if(isBlock(neighbor,this.map)){
            newNode.setState(2);
            openMap.put(neighbor.toInt(),newNode);
            //this.openMap[neighbor.x][neighbor.y] = newNode ;
            return;
        }

        //计算G、H、F值
        calc(newNode);

        //如果已存在开启列表中，判断当前的G值是否更小，是则更新
        if(existN != null){
            if(newNode.getG() < existN.getG()){
                openList.remove(existN);
                existN.setG(newNode.getG());
                existN.setF(newNode.getF());
                existN.setParentNode(newNode.getParentNode());
                openList.add(existN);
                //openMap.put(neighbor.toInt(),existN);
                //this.openMap[neighbor.x][neighbor.y] = existN;
            }
        }
        //不在开启列表中，则添加进去
        else{
            openList.add(newNode);
            openMap.put(neighbor.toInt(),newNode);
            //this.openMap[neighbor.x][neighbor.y] = newNode;
        }
    }

    //TODO 可以把地图进行 位运算优化
    protected OffsetCoord jump(OffsetCoord neighbor, Set<OffsetCoord> endAround, int direction) {
        if (neighbor == null || !isWalkable(neighbor)) return null;
        if (endAround.contains(neighbor)){
            return neighbor;
        }

        //int direction = neighbor.subtract(current).getDirection();
        //if(direction!=d){
        //    System.out.println();
        //}

        //根据 朝向计算是否有强迫邻居  如果有强迫邻居  则当前邻居是跳点
        //FACE_RIGHT_DOWN = 0,FACE_RIGHT_UP = 1,FACE_UP = 2,FACE_LEFT_UP = 3,FACE_LEFT_DOWN = 4,FACE_DOWN = 5,

        //                    FACE_UP
        //                 /-----------\
        //    FACE_LEFT_UP/-------------\FACE_RIGHT_UP
        //  FACE_LEFT_DOWN\-------------/FACE_RIGHT_DOWN
        //                 \-----------/
        //                   FACE_DOWN

        switch (direction){
            //TODO 这里多向跳点判断可以进行 剪枝 优化 参考 https://runzhiwang.github.io/2019/06/21/jps/#more  3.1.2.1 剪枝的优化效率
            //向上
            case Hex.FACE_UP:
                if(jump(neighbor.neighbor(Hex.FACE_LEFT_UP), endAround,Hex.FACE_LEFT_UP)!=null||jump(neighbor.neighbor(Hex.FACE_RIGHT_UP), endAround,Hex.FACE_RIGHT_UP)!=null){
                    return neighbor;
                }
                break;
            //右下
            case Hex.FACE_RIGHT_DOWN:
                if(jump(neighbor.neighbor(Hex.FACE_DOWN), endAround,Hex.FACE_DOWN)!=null||jump(neighbor.neighbor(Hex.FACE_RIGHT_UP), endAround,Hex.FACE_RIGHT_UP)!=null){
                    return neighbor;
                }
                break;
            //左下
            case Hex.FACE_LEFT_DOWN:
                if(jump(neighbor.neighbor(Hex.FACE_LEFT_UP), endAround,Hex.FACE_LEFT_UP)!=null||jump(neighbor.neighbor(Hex.FACE_DOWN), endAround,Hex.FACE_DOWN)!=null){
                    return neighbor;
                }
                break;

            //单向没问题
            case Hex.FACE_RIGHT_UP:
                if(!isWalkable(neighbor.neighbor(Hex.FACE_DOWN))&&isWalkable(neighbor.neighbor(Hex.FACE_RIGHT_DOWN))){
                    return neighbor;
                }
                if(!isWalkable(neighbor.neighbor(Hex.FACE_LEFT_UP))&&isWalkable(neighbor.neighbor(Hex.FACE_UP))){
                    return neighbor;
                }
                break;
            case Hex.FACE_LEFT_UP:
                if(!isWalkable(neighbor.neighbor(Hex.FACE_DOWN))&&isWalkable(neighbor.neighbor(Hex.FACE_LEFT_DOWN))){
                    return neighbor;
                }
                if(!isWalkable(neighbor.neighbor(Hex.FACE_RIGHT_UP))&&isWalkable(neighbor.neighbor(Hex.FACE_UP))){
                    return neighbor;
                }
                break;
            case Hex.FACE_DOWN:
                if(!isWalkable(neighbor.neighbor(Hex.FACE_LEFT_UP))&&isWalkable(neighbor.neighbor(Hex.FACE_LEFT_DOWN))){
                    return neighbor;
                }
                if(!isWalkable(neighbor.neighbor(Hex.FACE_RIGHT_UP))&&isWalkable(neighbor.neighbor(Hex.FACE_RIGHT_DOWN))){
                    return neighbor;
                }
                break;
            default:
                System.err.println("错误朝向"+direction);
        }
        return jump(neighbor.neighbor(direction), endAround,direction);
    }

    private boolean isWalkable(OffsetCoord offsetCoord){
        if (checkBound(offsetCoord, this.width, this.height))
        {
            return false;
        }
        if(isBlock(offsetCoord,this.map)){
            return false;
        }
        return true;
    }


    private boolean isWalkable(Hex hex){
        OffsetCoord offsetCoord = OffsetCoord.qoffsetFromCube(OffsetCoord.ODD,hex);
        return isWalkable(offsetCoord);
    }


    /**
     * 判断地图是否阻挡
     * @param OffsetCoord
     * @return true是阻挡
     */
    private static final boolean isBlock(OffsetCoord OffsetCoord,byte[][] map)
    {
        return map[OffsetCoord.x][OffsetCoord.y]==1;
    }

    private static final boolean checkBound(OffsetCoord OffsetCoord,int width,int height){
        if(OffsetCoord.x<0||OffsetCoord.x>=width||OffsetCoord.y<0||OffsetCoord.y>=height){
            return true;
        }
        return false;
    }

    private void calc(NodeOffsetCoord p){
        calcG(p);
        calcH(p);
        calcF(p);
    }

    // 计算G值
    private void calcG(NodeOffsetCoord p) {
        final NodeOffsetCoord parentNodeOffsetCoord = p.getParentNode();

        if (parentNodeOffsetCoord == null) {
            p.setG(1);
        } else {
            //这里挺耗性能
            //NodeOffsetCoord pp = p.getParentNode().getParentNode();
            //if(pp!=null){
            //	OffsetCoord currPoint = p.getOffsetCoord();
            //	OffsetCoord preOffsetCoord = pp.getOffsetCoord();
            //	OffsetCoord note = p.getParentNode().getOffsetCoord();
            //	if(!(preOffsetCoord.x <<1 == currPoint.x +note.x && preOffsetCoord.y <<1 == currPoint.y +note.y)){
            //		dist += 5;	//拐弯加权
            //	}
            //}
            p.setG(parentNodeOffsetCoord.getG() +parentNodeOffsetCoord.getOffsetCoord().getHex().distance(p.getOffsetCoord().getHex()));
        }
    }

    // 计算H值
    private void calcH(NodeOffsetCoord n) {
        final OffsetCoord endOffsetCoord = endN.getOffsetCoord();
        final OffsetCoord nowOffsetCoord = n.getOffsetCoord();
        int endY = endOffsetCoord.y;
        int endX = endOffsetCoord.x;

        int nowX = nowOffsetCoord.x;
        int nowY = nowOffsetCoord.y;
        int num =
                //n.getOffsetCoord().distance(endN.getOffsetCoord());
                Math.abs(nowX - endX) + Math.abs(nowY - endY);
        n.setH(num);
    }

    // 计算F值
    private void calcF(NodeOffsetCoord p) {
        p.setF(p.getG() + p.getH());
    }




}
