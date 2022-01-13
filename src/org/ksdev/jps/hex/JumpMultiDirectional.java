package org.ksdev.jps.hex;

/**
 * Created by JinMiao
 * 2022/1/13.
 */
public class JumpMultiDirectional {
    //pair<Node*,Forced Neighbor Direction> JumpMultiDirectional(int row, int col, int rowDir, int colDir, bool even, float currentCost, float*givenCost) {
    //    int cRow = row + rowDir;
    //    int cCol = col + colDir;
    //    int cRowDir = rowDir;
    //    int cColDir = colDir;
    //    bool newEven = !(even);
    //    //check if it’s a wall
    //    if (IsWall(cRow, cCol)) {
    //        return <NULL, WALL >;
    //    }
    //    Node * node = GetNode(cRow, cCol);
    //    //check if it’s the goal node
    //    if (IsGoalNode(node)) {
    //                *givenCost = currentCost;
    //        return <node, GOAL >;
    //    }
    //    //update rowDir (alternate for southwest and southeast  更新 rowDir（西南和东南交替 方向）如果 cColDir 不为 0
    //    //directions) if cColDir is not 0
    //    if (cColDir != 0) {
    //        cRowDir = (rowDir + 1) % 2;
    //    }
    //    //if colDir is 0, let value of even remain unchanged
    //    else {
    //        newEven = even;
    //    }
    //    //First traverse in the uni-directional directions and 首先单向遍历和
    //    //then parse in the current direction 然后在当前方向解析
    //    //Check for a jump point 检查跳转点
    //    float notUsed;
    //    pair<Node*,Forced Neighbor Direction > uniDirectionalNode = NULL;
    //    switch (cColDir) {
    //        case 0:
    //    //North Jump Point travel 北跳点旅行
    //            if (even) {
    //        //Recursively travel in northwest direction 沿西北方向递归行进
    //                uniDirectionalNode =
    //                        JumpUniDirectional(cRow, cCol, 0, -1,
    //                                newEven, 0, & notUsed);
    //                if (uniDirectionalNode.first != NULL) {
    //                //Non-obvious! Discard unidirectional
    //                //Node and return the current node as the
    //                //jump Point
    //                *givenCost = currentCost;
    //                    return <node, FORCED NEIGHBOR >;
    //                }
    //                //Recursively travel in northeast direction 向东北方向递归行进
    //                uniDirectionalNode =  JumpUniDirectional(cRow, cCol, 0, 1, newEven,
    //                                0, & notUsed);
    //                if (uniDirectionalNode.first != NULL) {
    //        //Non-obvious! Discard unidirectional
    //        //Node and return the current node as the
    //        //jump Point
    //        *givenCost = currentCost;
    //                                return <node, FORCED NEIGHBOR >;
    //                }
    //            } else {
    //                 //Recursively travel in northwest direction  沿西北方向递归行进
    //                uniDirectionalNode = JumpUniDirectional(cRow, cCol, -1, -1,
    //                                newEven, 0, & notUsed);
    //                if (uniDirectionalNode.first != NULL) {
    //                    //Non-obvious! Discard unidirectional
    //                    //Node and return the current node as the
    //                    //jump Point
    //                    *givenCost = currentCost;
    //                    return <node, FORCED NEIGHBOR >;
    //                }
    //                //Recursively travel in northeast direction 向东北方向递归行进
    //                uniDirectionalNode = JumpUniDirectional(cRow, cCol, -1, 1,
    //                                newEven, 0, & notUsed);
    //                if (uniDirectionalNode.first != NULL) {
    //                    //Non-obvious! Discard unidirectional
    //                    //Node and return the current node as the
    //                    //jump Point
    //                    *givenCost = currentCost;
    //                    return <node, FORCED NEIGHBOR >;
    //                }
    //            }
    //            break;
    //        case -1:
    //        //SouthWest Jump Point travel  西南跳点旅行
    //        //Search and return the current node if there is  查找当前节点，如果有则返回
    //        //a forced neighbor, if any  强制邻居，如果有的话
    //            break;
    //        case 1:
    //        //SouthEast Jump Point travel  东南跳点旅行
    //        //Search and return the current node if there is  查找当前节点，如果有则返回
    //        //a forced neighbor, if any  强制邻居，如果有的话
    //            break;
    //    }
    //    //Recursively travel in the current direction 沿当前方向递归行进
    //    return JumpMultiDirectional(cRow, cCol, cRowDir, cColDir,
    //            newEven, currentCost + 1.0f,
    //            givenCost);
    //}
}
