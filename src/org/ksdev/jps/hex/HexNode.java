package org.ksdev.jps.hex;

import org.ksdev.jps.test.Hex;
import org.ksdev.jps.test.OffsetCoord;

import java.util.Comparator;

/**
 * Created by JinMiao
 * 2021/5/21.
 */
public class HexNode{
    private double f;
    private double g;
    private double h;
    private HexNode parentNode;

    private Hex hex;



    public void calc(Hex end){
        calcG();
        calcH(end);
        calcF();
    }

    // 计算G值
    private void calcG() {
        if(parentNode==null){
            this.g = 1;
        }else{
            this.g = parentNode.g+parentNode.getHex().distance(this.hex);
        }
    }

    // 计算H值
    private void calcH(Hex end) {

        //final OffsetCoord endOffsetCoord = OffsetCoord.qoffsetFromCube(OffsetCoord.ODD,end);
        ////end.getOffsetCoord();
        //final OffsetCoord nowOffsetCoord = OffsetCoord.qoffsetFromCube(OffsetCoord.ODD,this.hex);
        //int endY = endOffsetCoord.y;
        //int endX = endOffsetCoord.x;
        //
        //int nowX = nowOffsetCoord.x;
        //int nowY = nowOffsetCoord.y;
        //int num =
        //        //n.getOffsetCoord().distance(endN.getOffsetCoord());
        //        Math.abs(nowX - endX) + Math.abs(nowY - endY);
        //this.setH(num);


        int num =
        // hex.distance(end);
                Math.abs(getHex().x - end.x) + Math.abs(getHex().y - end.y);
        this.h = num;
    }

    // 计算F值
    private void calcF() {
        this.f = this.g+this.h;
    }



    public HexNode(Hex hex) {
        this.hex = hex;
    }

    public HexNode(HexNode parentNode, Hex hex) {
        this.parentNode = parentNode;
        this.hex = hex;
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public HexNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(HexNode parentNode) {
        this.parentNode = parentNode;
    }

    public Hex getHex() {
        return hex;
    }

    public void setHex(Hex hex) {
        this.hex = hex;
    }


}
