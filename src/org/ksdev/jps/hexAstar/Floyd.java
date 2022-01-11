package org.ksdev.jps.hexAstar;


import org.ksdev.jps.test.Hex;
import org.ksdev.jps.test.OffsetCoord;

import java.util.LinkedList;

/**
 * 弗洛伊德平滑算法
 * Created by JinMiao
 * 2021/7/5.
 */
public class Floyd {


    /**
     * 平滑算法
     * 去掉直线点 只留拐点
     * @param path
     * @return
     */
    public static LinkedList<OffsetCoord> smooth(LinkedList<OffsetCoord> path){
        LinkedList<OffsetCoord> temp = new LinkedList<>();
        int size = path.size();
        if(size<3){
            temp.addAll(path);
            return temp;
        }
        //去掉方向一致的点  只留拐点
        OffsetCoord frontFront = null;
        OffsetCoord front = null;
        for (OffsetCoord offsetCoord : path) {
            if(front==null){
                front = offsetCoord;
                temp.add(front);
                continue;
            }
            if(frontFront==null){
                frontFront = front;
                front = offsetCoord;
                continue;
            }
            Hex frontFrontHex = frontFront.getHex();
            Hex frontHex = front.getHex();
            Hex nowHex = offsetCoord.getHex();
            //判断是否是拐点
            if(frontHex.x!=(frontFrontHex.x+ nowHex.x)>>1||frontHex.y!=(frontFrontHex.y+ nowHex.y)>>1||frontHex.z!=(frontFrontHex.z+ nowHex.z)>>1){
                temp.add(front);
            }
            frontFront = front;
            front = offsetCoord;
        }
        temp.add(path.getLast());
        //for (OffsetCoord offsetCoord : temp) {
        //    System.out.println("2 sx "+ offsetCoord.x +" y "+offsetCoord.y);
        //}
        //System.out.println();
        return temp;
    }

    /**
     * 移动6方向优化为12方向
     * @param path
     * @param map
     * @return
     */
    public static LinkedList<OffsetCoord> floydSmooth(LinkedList<OffsetCoord> path, byte[][] map) {

        //path = smooth(path);

        int size = path.size();
        if (size < 3) {
            return path;
        }
        OffsetCoord front = null;
        OffsetCoord frontFront = null;
        //优化6方向为12方向
        LinkedList<OffsetCoord> result = new LinkedList<>();
        result.add(path.getFirst());
        int width = map.length;
        int height = map[0].length;
        for (OffsetCoord offsetCoord : path) {
            if (front == null) {
                front = offsetCoord;
                continue;
            }
            if (frontFront == null) {
                frontFront = front;
                front = offsetCoord;
                continue;
            }
            Hex frontFrontHex = frontFront.getHex();
            Hex frontHex = front.getHex();
            Hex nowHex = offsetCoord.getHex();
            if (frontFrontHex.distance(frontHex) > 1) {
                result.add(front);
                frontFront = front;
                front = offsetCoord;
                continue;
            }
            if (frontHex.distance(nowHex) > 1) {
                result.add(front);
                result.add(offsetCoord);
                frontFront = offsetCoord;
                front = null;
                continue;
            }
            //判断两点是否可以直达
            Hex[] crossHexs = Hex.crossHexs(frontFrontHex, nowHex);
            if(crossHexs==null){
                result.add(front);
                frontFront = front;
                front = offsetCoord;
                continue;
            }
            boolean isBlock = false;
            for (Hex crossHex : crossHexs) {
                OffsetCoord crossoOffsetCoord = OffsetCoord.qoffsetFromCube(OffsetCoord.ODD, frontFrontHex.add(crossHex));
                if (AStarOffsetCoord.checkBound(crossoOffsetCoord, width, height)) {
                    isBlock = true;
                    break;
                }
                if (AStarOffsetCoord.isBlock(crossoOffsetCoord, map)) {
                    isBlock = true;
                    break;
                }
            }
            if (isBlock) {
                result.add(front);
                frontFront = front;
                front = offsetCoord;
                continue;
            }
            result.add(offsetCoord);
            frontFront = offsetCoord;
            front = null;
        }
        if(result.getFirst()!=path.getFirst()){
            result.addFirst(path.getFirst());
        }

        if(result.getLast()!=path.getLast()){
            result.addLast(path.getLast());
        }
        //result = smooth(result);
        return result;
    }
}
