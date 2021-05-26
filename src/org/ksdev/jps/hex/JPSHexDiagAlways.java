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
    public JPSHexDiagAlways(Map<Hex, Byte> map) {
        super(map);
    }

    @Override
    protected Set<Integer> findNeighborsDirections(HexNode node, Map<HexNode, HexNode> parentMap) {
        //TODO 这里应该有邻居的裁剪
        Set<Integer> hexes = new HashSet<>();
        for (int direction = 0; direction < 6; direction++) {
            hexes.add(direction);
            //Hex hex = node.getHex().neighbor(direction);
            //hexes.add(hex);
        }
        return hexes;
    }

    @Override
    protected boolean jump(int direction, Hex current, Set<Hex> goals) {
        //根据 朝向计算是否有强迫邻居  如果有强迫邻居  则当前邻居是跳点
        //FACE_RIGHT_DOWN = 0,FACE_RIGHT_UP = 1,FACE_UP = 2,FACE_LEFT_UP = 3,FACE_LEFT_DOWN = 4,FACE_DOWN = 5,

        //                    FACE_UP
        //                 /-----------\
        //    FACE_LEFT_UP/-------------\FACE_RIGHT_UP
        //  FACE_LEFT_DOWN\-------------/FACE_RIGHT_DOWN
        //                 \-----------/
        //                   FACE_DOWN

       switch (direction){
           //右下
           case Hex.FACE_RIGHT_DOWN:
                if(!isWalkable(current.neighbor(Hex.FACE_RIGHT_UP))&&isWalkable(current.neighbor(Hex.FACE_RIGHT_DIAGONAL))){
                    return true;
                }
               if(!isWalkable(current.neighbor(Hex.FACE_DOWN))&&isWalkable(current.neighbor(Hex.FACE_RIGHT_DOWN_DIAGONAL))){
                   return true;
               }
               break;
           case Hex.FACE_RIGHT_UP:
               if(!isWalkable(current.neighbor(Hex.FACE_RIGHT_DOWN))&&isWalkable(current.neighbor(Hex.FACE_RIGHT_DIAGONAL))){
                   return true;
               }
               if(!isWalkable(current.neighbor(Hex.FACE_UP))&&isWalkable(current.neighbor(Hex.FACE_RIGHT_UP_DIAGONAL))){
                   return true;
               }
               break;
           case Hex.FACE_UP:
               if(!isWalkable(current.neighbor(Hex.FACE_LEFT_UP))&&isWalkable(current.neighbor(Hex.FACE_LEFT_UP_DIAGONAL))){
                   return true;
               }
               if(!isWalkable(current.neighbor(Hex.FACE_RIGHT_UP))&&isWalkable(current.neighbor(Hex.FACE_RIGHT_UP_DIAGONAL))){
                   return true;
               }
               break;
           case Hex.FACE_LEFT_UP:
               if(!isWalkable(current.neighbor(Hex.FACE_LEFT_DOWN))&&isWalkable(current.neighbor(Hex.FACE_LEFT_DIAGONAL))){
                   return true;
               }
               if(!isWalkable(current.neighbor(Hex.FACE_UP))&&isWalkable(current.neighbor(Hex.FACE_LEFT_UP_DIAGONAL))){
                   return true;
               }
               break;
           case Hex.FACE_LEFT_DOWN:
               if(!isWalkable(current.neighbor(Hex.FACE_LEFT_UP))&&isWalkable(current.neighbor(Hex.FACE_LEFT_DIAGONAL))){
                   return true;
               }
               if(!isWalkable(current.neighbor(Hex.FACE_DOWN))&&isWalkable(current.neighbor(Hex.FACE_LEFT_DOWN_DIAGONAL))){
                   return true;
               }
               break;
           case Hex.FACE_DOWN:
               if(!isWalkable(current.neighbor(Hex.FACE_LEFT_DOWN))&&isWalkable(current.neighbor(Hex.FACE_LEFT_DOWN_DIAGONAL))){
                   return true;
               }
               if(!isWalkable(current.neighbor(Hex.FACE_RIGHT_DOWN))&&isWalkable(current.neighbor(Hex.FACE_RIGHT_DOWN_DIAGONAL))){
                   return true;
               }
               break;
           default:
               System.err.println("错误朝向"+direction);
       }
        return false;
    }
}
