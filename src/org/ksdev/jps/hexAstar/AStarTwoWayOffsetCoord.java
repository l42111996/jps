package org.ksdev.jps.hexAstar;

import org.ksdev.jps.test.OffsetCoord;

import java.util.*;

/**
 * 双向a*寻路
 * Created by JinMiao
 * 2021/6/29.
 */
public class AStarTwoWayOffsetCoord {

    //地图阻挡信息
    private final byte[][] map;


    private PriorityQueue<NodeOffsetCoord> startOpenList = new PriorityQueue<>();
    private NodeOffsetCoord[][] startNodeState;

    private PriorityQueue<NodeOffsetCoord> endOpenList = new PriorityQueue<>();
    private NodeOffsetCoord[][] endNodeState;

    private long maxSearchTime = 2000;

    public AStarTwoWayOffsetCoord(byte[][] map){
        this.map = map;
        startNodeState = new NodeOffsetCoord[map.length][map[0].length];
        endNodeState = new NodeOffsetCoord[map.length][map[0].length];
    }


    //public List<OffsetCoord> search(OffsetCoord start, OffsetCoord end, boolean incloudEndAround){
    //    NodeOffsetCoord n = searchPath(start,end,incloudEndAround);
    //    List<OffsetCoord> result = null;
    //    if(n!=null){
    //        result = new ArrayList<>();
    //        setResult(result, n);
    //    }
    //    return result;
    //}
    //
    public LinkedList<OffsetCoord> search(int x, int y, int tx, int ty){
        LinkedList<OffsetCoord> result = new LinkedList<>();
        if(x==tx&&y==ty){
            return result;
        }
        long startMill = System.currentTimeMillis();
        OffsetCoord endOffsetCoord = new OffsetCoord(tx,ty);
        searchPath(new OffsetCoord(x,y),endOffsetCoord,result);
        if(result.isEmpty()){
            System.out.println("a*寻路没有找到路径 x "+ x+" y "+y+" tx "+tx+" ty "+ty);
            //SystemLogger.mapLogger.error("a*寻路没有找到路径 x {} y {} tx {} ty {}",x,y,tx,ty);
        }
        long useTime = System.currentTimeMillis()-startMill;
        if(useTime>100){
            //SystemLogger.mapLogger.info("a* 寻路结束 路径长度 {} 耗时 {}",result!=null?result.size():0,System.currentTimeMillis()-startMill);
        }
        if(!result.isEmpty()){
            result.removeFirst();
        }
        //System.out.println(openList.size());
        return result;
    }

    //public List<OffsetCoord> searchOffsetCoord(OffsetCoord start,OffsetCoord end,boolean incloudEndAround){
    //    return search(start.x,start.y,end.x,end.y,incloudEndAround);
    //}


    private void clear(){
        startOpenList = new PriorityQueue<>();
        startNodeState = new NodeOffsetCoord[map.length][map[0].length];
        endOpenList = new PriorityQueue<>();
        endNodeState = new NodeOffsetCoord[map.length][map[0].length];
    }


    private NodeOffsetCoord checkSearchNode(NodeOffsetCoord n, NodeOffsetCoord[][] curNodeState, NodeOffsetCoord[][] reverseNodeState, PriorityQueue<NodeOffsetCoord> curOpenList, OffsetCoord end){
        //SystemLogger.exceptionLogger.error("f {} g {} h {} distance {}",n.getF(),n.getG(),n.getH(),n.getOffsetCoord().distance(end));
        OffsetCoord curOffsetCoord = n.getOffsetCoord();
        //判断此节点是否是目标点，是则找到了，跳出
        if(end.equals(curOffsetCoord)){
            return n;
        }
        NodeOffsetCoord offsetCoord = curNodeState[curOffsetCoord.x][curOffsetCoord.y];
        offsetCoord.setState(2);

        for (int i = 0; i < 6; i++) {
            OffsetCoord neighbor = curOffsetCoord.neighbor(i);

            NodeOffsetCoord newNode = new NodeOffsetCoord(neighbor, n);
            if(checkBound(neighbor)){
                continue;
            }
            NodeOffsetCoord existN = curNodeState[neighbor.x][neighbor.y];
            //检查是否已在关闭列表中
            if(existN!=null&&existN.getState()==2){
                continue;
            }
            //检查地图是否障碍点
            if(isBlock(neighbor)){
                newNode.setState(2);
                curNodeState[neighbor.x][neighbor.y] = newNode;
                continue;
            }
            //计算G、H、F值
            calc(newNode, end);
            //如果已存在开启列表中，判断当前的G值是否更小，是则更新
            if(existN != null){
                if(newNode.getG() < existN.getG()){
                    curOpenList.remove(existN);
                    existN.setG(newNode.getG());
                    existN.setF(newNode.getF());
                    existN.setParentNode(newNode.getParentNode());
                    curOpenList.add(existN);
                    curNodeState[neighbor.x][neighbor.y] = existN;
                }
            }
            //不在开启列表中，则添加进去
            else{
                //判断新加入的点是否在反向openlist里面存在  如果存在则路径已经找到
                NodeOffsetCoord reverseNode= reverseNodeState[neighbor.x][neighbor.y];
                if(reverseNode!=null&&!isBlock(reverseNode.getOffsetCoord())){
                    return newNode;
                }
                //if(reverseNode!=null&&reverseNode.getState()==1){
                //    return newNode;
                //}
                curOpenList.add(newNode);
                curNodeState[neighbor.x][neighbor.y] = newNode;
            }
        }
        return null;
    }

    private void searchPath(OffsetCoord start, OffsetCoord end,LinkedList<OffsetCoord> result){
        clear();
        NodeOffsetCoord startN =  new NodeOffsetCoord(start);
        NodeOffsetCoord endN = new NodeOffsetCoord(end);
        //从起点开始将起点放入起点开放列表
        startOpenList.add(startN);
        startNodeState[start.x][start.y] = startN;
        //从终点开始将重点放入终点开放列表
        endOpenList.add(endN);
        endNodeState[end.x][end.y] = endN;

        //开启列表中有节点的话，取出第一个节点，即最小F值的节点
        long startTime = System.currentTimeMillis();
        NodeOffsetCoord searchedEnd;
        for(;;){
            //计算超时
            if(System.currentTimeMillis()-startTime>maxSearchTime){
                break;
            }
            NodeOffsetCoord n;
            //取起点最小值
            if((n = startOpenList.poll())==null){
                break;
            }
            searchedEnd = checkSearchNode(n,this.startNodeState,this.endNodeState,this.startOpenList, end);
            if(searchedEnd!=null){
                setResultOffSetCoord(result,searchedEnd,false);
                searchedEnd = this.endNodeState[searchedEnd.getOffsetCoord().x][searchedEnd.getOffsetCoord().y];
                setReverseResultOffSetCoord(result,searchedEnd,true);
                return;
            }
            if((n = endOpenList.poll())==null){
                break;
            }
            searchedEnd = checkSearchNode(n,this.endNodeState,this.startNodeState,this.endOpenList, start);
            if(searchedEnd!=null){
                NodeOffsetCoord temp = this.startNodeState[searchedEnd.getOffsetCoord().x][searchedEnd.getOffsetCoord().y];
                setResultOffSetCoord(result,temp,false);
                setReverseResultOffSetCoord(result,searchedEnd,true);
                return;
            }
        }
    }

    /**
     * 判断地图是否阻挡
     * @param OffsetCoord
     * @return true是阻挡
     */
    protected boolean isBlock(OffsetCoord OffsetCoord)
    {
        return map[OffsetCoord.x][OffsetCoord.y]==1;
    }

    protected boolean checkBound(OffsetCoord OffsetCoord){
        if(OffsetCoord.x<0||OffsetCoord.x>=this.map.length||OffsetCoord.y<0||OffsetCoord.y>=this.map[0].length){
            return true;
        }
        return false;
    }




    private void calc(NodeOffsetCoord p, OffsetCoord endNode){
        calcG(p);
        calcH(p,endNode);
        calcF(p);
    }

    // 计算G值
    private void calcG(NodeOffsetCoord p) {
        int dist = 1;
        if (p.getParentNode() == null) {
            p.setG(1);
        } else {
            //这里挺耗性能
            //NodeOffsetCoord pp = p.getParentNode().getParentNode();
            //if(pp!=null){
            //	OffsetCoord currPoint = p.getOffsetCoord();
            //	OffsetCoord preOffsetCoord = pp.getOffsetCoord();
            //	OffsetCoord note = p.getParentNode().getOffsetCoord();
            //	if(!(preOffsetCoord.x <<1 == currPoint.x +note.x && preOffsetCoord.y <<1 == currPoint.y +note.y)){
            //		dist += 1;	//拐弯加权
            //	}
            //}
            p.setG(p.getParentNode().getG()+dist);
        }
    }

    // 计算H值
    private void calcH(NodeOffsetCoord n,OffsetCoord endOffsetCoord) {
        final OffsetCoord nowOffsetCoord = n.getOffsetCoord();
        int endY = endOffsetCoord.y;
        int endX = endOffsetCoord.x;

        int nowX = nowOffsetCoord.x;
        int nowY = nowOffsetCoord.y;
        int num =
                //nowOffsetCoord.getHex().distance(endOffsetCoord.getHex());
                Math.abs(nowX - endX) + Math.abs(nowY - endY);


        n.setH(num);
    }

    // 计算F值
    private void calcF(NodeOffsetCoord p) {
        p.setF(p.getG() + p.getH());
    }

    private void setResult(List<OffsetCoord> result, NodeOffsetCoord p){
        if(p.getParentNode() != null){
            setResult(result, p.getParentNode());
        }
        result.add(p.getOffsetCoord());
    }

    private void setResultOffSetCoord(List<OffsetCoord> result, NodeOffsetCoord p,boolean ignoreFirstNode){
        if(p.getParentNode() != null){
            setResultOffSetCoord(result, p.getParentNode(),ignoreFirstNode);
        }
        //忽略第一个元素
        else if(ignoreFirstNode){
            return;
        }
        result.add(p.getOffsetCoord());
    }
    private void setReverseResultOffSetCoord(List<OffsetCoord> result, NodeOffsetCoord p,boolean ignoreFirstNode){
        if(ignoreFirstNode){
            p = p.getParentNode();
        }
        for(;;){
            result.add(p.getOffsetCoord());
            p = p.getParentNode();
            if(p==null){
                break;
            }
        }
    }
}
