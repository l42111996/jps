package org.ksdev.jps.hexAstar;

import org.ksdev.jps.test.Hex;
import org.ksdev.jps.test.OffsetCoord;

import java.util.*;

/**
 * A*算法
 */
public class AStarHex {

	public static final int DIST_STRAIGHT = 10;

	private PriorityQueue<Node> openList = new PriorityQueue<>();
	private Map<Hex, Node> openMap = new HashMap<>();
	private HashSet<Node> closeList = new HashSet<>();
	private final Map<Hex,Byte> map;
	private Node endN;
	
	public AStarHex(Map<Hex,Byte> map, Hex start, Hex end){
		this(map, new Node(start), new Node(end));
	}
	
	public AStarHex(Map<Hex,Byte> map, Node startN, Node endN){
		this.map = map;
		this.endN = endN;
		
		//起点先添加到开启列表中
		openList.add(startN);
		openMap.put(startN.getHex(),startN);
	}
	
	public List<Node> search(){
		//开启列表中有节点的话，取出第一个节点，即最小F值的节点
		Node n;
		boolean isFind = false;
		while((n = openList.poll()) != null){
			this.openMap.remove(n.getHex(),n);
			//判断此节点是否是目标点，是则找到了，跳出
			if(n.equals(endN)){
				isFind = true;
				break;
			}
			closeList.add(n);
			Hex curHex = n.getHex();
			for (int i = 0; i < 6; i++) {
				Hex neighbor = curHex.neighbor(i);
				checkNewNode(neighbor, n, DIST_STRAIGHT);
			}
		}
		
		List<Node> result = null;
		if(isFind){
			result = new ArrayList<>();
			setResult(result, n);
		}
		return result;
	}
	
	private void checkNewNode(Hex neighbor, Node parentN, int dist){
		Node newNode = new Node(neighbor, parentN);
		//检查是否已在关闭列表中
		if(closeList.contains(newNode)){
			return;
		}

		Byte block = map.get(neighbor);
		//检查地图是否障碍点
		if(block==null ||map.get(neighbor) == 1){
			closeList.add(newNode);
			return;
		}
		
		//计算G、H、F值
		calc(newNode, dist);

		Node existN = this.openMap.get(neighbor);

		//如果已存在开启列表中，判断当前的G值是否更小，是则更新
		if(existN != null){
			if(newNode.getG() < existN.getG()){
				openList.remove(existN);
				existN.setG(newNode.getG());
				existN.setF(newNode.getF());
				existN.setParentNode(newNode.getParentNode());
				openList.add(existN);
				this.openMap.put(neighbor,existN);
			}
		}
		//不在开启列表中，则添加进去
		else{
			openList.add(newNode);
			this.openMap.put(neighbor,newNode);
		}
		
	}
	
	private void calc(Node p, int dist){
		calcG(p, dist);
		calcH(p);
		calcF(p);
	}
	
	// 计算G值
	private void calcG(Node p, int dist) {
		if (p.getParentNode() == null) {
			p.setG(dist);
		} else {
			p.setG(p.getParentNode().getG() + dist);
		}
	}

	// 计算H值
	private void calcH(Node n) {
		int num = n.getHex().distance(endN.getHex());
				//Math.abs(n.getX() - endN.getX()) + Math.abs(n.getY() - endN.getY());
		n.setH(num * 10);
	}

	// 计算F值
	private void calcF(Node p) {
		p.setF(p.getG() + p.getH());
	}
	
	private void setResult(List<Node> result, Node p){
		if(p.getParentNode() != null){
			setResult(result, p.getParentNode());
		}
		result.add(p);
	}
	
}