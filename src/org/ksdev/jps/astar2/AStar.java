package org.ksdev.jps.astar2;

import java.util.*;

/**
 * A*算法
 */
public class AStar {

	public static final int DIST_STRAIGHT = 10;
	public static final int DIST_DIAGONAL = 14;
	
	private PriorityQueue<Node> openList = new PriorityQueue<>();
	private Map<Integer,Node> openMap = new HashMap<>();
	private HashSet<Node> closeList = new HashSet<>();
	private byte[][] map;
	private Node endN;
	private long maxTimeMill;
	
	public AStar(byte[][] map, int startX, int startY, int endX, int endY){
		this(map, new Node(startX, startY), new Node(endX, endY));
	}
	
	public AStar(byte[][] map, Node startN, Node endN){
		this.map = map;
		this.endN = endN;
		
		//起点先添加到开启列表中
		openList.add(startN);
		openMap.put(startN.toInteger(),startN);
	}
	
	public List<Node> search(){
		//开启列表中有节点的话，取出第一个节点，即最小F值的节点
		Node n;
		boolean isFind = false;

		long startTime = System.currentTimeMillis();
		while((n = openList.poll()) != null){
			this.openMap.remove(n.toInteger(),n);
			//判断此节点是否是目标点，是则找到了，跳出
			if(n.equals(endN)){
				isFind = true;
				break;
			}

			if(this.maxTimeMill>0){
				long now = System.currentTimeMillis();
				if(now-startTime>this.maxTimeMill){
					return null;
				}
			}


			closeList.add(n);
			
			//上
			if(n.getY() - 1 >= 0){
				checkNewNode(n.getX(), n.getY() - 1, n, DIST_STRAIGHT);
			}
			//下
			if(n.getY() + 1 < map.length){
				checkNewNode(n.getX(), n.getY() + 1, n, DIST_STRAIGHT);
			}
			//左
			if(n.getX() - 1 >= 0){
				checkNewNode(n.getX() - 1, n.getY(), n, DIST_STRAIGHT);
			}
			//右
			if(n.getX() + 1 < map[0].length){
				checkNewNode(n.getX() + 1, n.getY(), n, DIST_STRAIGHT);
			}
			
			//下面斜角方向注释掉，就表示寻路不能走斜线了。
			
			//左上
			if(n.getY() - 1 >= 0 && n.getX() - 1 >= 0){
				checkNewNode(n.getX() - 1, n.getY() - 1, n, DIST_DIAGONAL);
			}
			//右上
			if(n.getY() - 1 >= 0 && n.getX() + 1 < map[0].length){
				checkNewNode(n.getX() + 1, n.getY() - 1, n, DIST_DIAGONAL);
			}
			//左下
			if(n.getY() + 1 < map.length && n.getX() - 1 >= 0){
				checkNewNode(n.getX() - 1, n.getY() + 1, n, DIST_DIAGONAL);
			}
			//右下
			if(n.getY() + 1 < map.length && n.getX() + 1 < map[0].length){
				checkNewNode(n.getX() + 1, n.getY() + 1, n, DIST_DIAGONAL);
			}
			
		}
		
		List<Node> result = null;
		if(isFind){
			result = new ArrayList<Node>();
			setResult(result, n);
		}
		return result;
	}
	
	private void checkNewNode(int x, int y, Node parentN, int dist){
		Node newNode = new Node(x, y, parentN);
		//检查是否已在关闭列表中
		if(closeList.contains(newNode))
			return;
		
		//检查地图是否障碍点
		if(map[y][x] == 1){
			closeList.add(newNode);
			return;
		}
		
		//计算G、H、F值
		calc(newNode, dist);
		
		Node existN = this.openMap.get(newNode.toInteger());
		//for (Node n : openList) {
		//	if(n.equals(newNode)){
		//		existN = n;
		//		break;
		//	}
		//}
		
		//如果已存在开启列表中，判断当前的G值是否更小，是则更新
		if(existN != null){
			if(newNode.getG() < existN.getG()){
				openList.remove(existN);
				existN.setG(newNode.getG());
				existN.setF(newNode.getF());
				existN.setParentNode(newNode.getParentNode());
				openList.add(existN);
				this.openMap.put(existN.toInteger(),existN);
			}
		}
		//不在开启列表中，则添加进去
		else{
			openList.add(newNode);
			this.openMap.put(newNode.toInteger(),newNode);
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
		int num = Math.abs(n.getX() - endN.getX()) + Math.abs(n.getY() - endN.getY());
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


	public void setMaxTimeMill(long maxTimeMill) {
		this.maxTimeMill = maxTimeMill;
	}
}