package org.ksdev.jps.hex;

import org.ksdev.jps.Graph;
import org.ksdev.jps.Node;
import org.ksdev.jps.test.Hex;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Kevin
 */
class JPSHexDiagAlways extends JPSHex {
    public JPSHexDiagAlways(byte[][] map) {
        super(map);
    }

    @Override
    protected Set<Integer> findNeighborsDirections(HexNode node, Map<HexNode, HexNode> parentMap) {
        Set<Integer> hexes = new HashSet<>();
        //这里应该有邻居的裁剪
        HexNode parent = parentMap.get(node);
        Hex curHex = node.getHex();
        if(parent==null){
            for (int direction = 0; direction < 6; direction++) {
                hexes.add(direction);
            }
        }else{
            final int x = curHex.x;
            final int y = curHex.y;
            final int z = curHex.z;
            final int dx = (x - parent.getHex().x) / Math.max(Math.abs(x - parent.getHex().x), 1);
            final int dy = (y - parent.getHex().y) / Math.max(Math.abs(y - parent.getHex().y), 1);
            final int dz = (z - parent.getHex().z) / Math.max(Math.abs(z - parent.getHex().z), 1);

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

    @Override
    protected Hex jump(Hex neighbor, Hex current, Set<Hex> goals,int direction) {
        if (neighbor == null || !isWalkable(neighbor)) return null;
        if (goals.contains(neighbor)){
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
           //向上
           case Hex.FACE_UP:
               if(jump(neighbor.neighbor(Hex.FACE_LEFT_UP),neighbor,goals,Hex.FACE_LEFT_UP)!=null||jump(neighbor.neighbor(Hex.FACE_RIGHT_UP),neighbor,goals,Hex.FACE_RIGHT_UP)!=null){
                   return neighbor;
               }
               break;
           //右下
           case Hex.FACE_RIGHT_DOWN:
               if(jump(neighbor.neighbor(Hex.FACE_DOWN),neighbor,goals,Hex.FACE_DOWN)!=null||jump(neighbor.neighbor(Hex.FACE_RIGHT_UP),neighbor,goals,Hex.FACE_RIGHT_UP)!=null){
                   return neighbor;
               }
               break;
           //左下
           case Hex.FACE_LEFT_DOWN:
               if(jump(neighbor.neighbor(Hex.FACE_LEFT_UP),neighbor,goals,Hex.FACE_LEFT_UP)!=null||jump(neighbor.neighbor(Hex.FACE_DOWN),neighbor,goals,Hex.FACE_DOWN)!=null){
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
        return jump(neighbor.neighbor(direction),neighbor,goals,direction);
    }
}
