package org.ksdev.jps.hexAstar;



import com.sun.scenario.effect.Offset;
import org.ksdev.jps.test.Hex;
import org.ksdev.jps.test.OffsetCoord;

import java.util.*;

/**
 * A*算法
 */
public class AStarOffsetCoord {

	public static final int DIST_STRAIGHT = 10;

	private PriorityQueue<NodeOffsetCoord> openList = new PriorityQueue<>();
	private Map<OffsetCoord, NodeOffsetCoord> openMap = new HashMap<>();
	private HashSet<NodeOffsetCoord> closeList = new HashSet<>();
	//地图阻挡信息
	private final byte[][] map;
	private NodeOffsetCoord endN;
	private long maxSearchTime = 2000;

	public AStarOffsetCoord(byte[][] map){
		this.map = map;
	}
	

	public List<OffsetCoord> search(OffsetCoord start,OffsetCoord end,boolean incloudEndAround){
		NodeOffsetCoord n = searchPath(start,end,incloudEndAround);
		List<OffsetCoord> result = null;
		if(n!=null){
			result = new ArrayList<>();
			setResult(result, n);
		}
		return result;
	}

	public LinkedList<OffsetCoord> search(int x,int y,int tx,int ty,boolean incloudEndAround){
		long startMill = System.currentTimeMillis();
		OffsetCoord endOffsetCoord = new OffsetCoord(tx,ty);
		NodeOffsetCoord n = searchPath(new OffsetCoord(x,y),endOffsetCoord,incloudEndAround);
		LinkedList<OffsetCoord> result = new LinkedList<>();
		if(n!=null){
			setResultOffSetCoord(result, n);
			if(!n.getOffsetCoord().equals(endOffsetCoord)&&!isBlock(endOffsetCoord)){
				result.add(new OffsetCoord(tx,ty));
			}
		}else{
		}
		return result;
	}


	public List<OffsetCoord> searchOffsetCoord(OffsetCoord start,OffsetCoord end,boolean incloudEndAround){
		return search(start.x,start.y,end.x,end.y,incloudEndAround);
	}
	private void clear(){
		openList = new PriorityQueue<>();
		openMap = new HashMap<>();
		closeList = new HashSet<>();
		endN = null;
	}


	private NodeOffsetCoord searchPath(OffsetCoord start, OffsetCoord end, boolean incloudEndAround){
		clear();
		NodeOffsetCoord startN =  new NodeOffsetCoord(start);
		NodeOffsetCoord endN = new NodeOffsetCoord(end);
		this.endN = endN;
		//起点先添加到开启列表中
		openList.add(startN);
		openMap.put(startN.getOffsetCoord(),startN);


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
			//SystemLogger.exceptionLogger.error("f {} g {} h {} distance {}",n.getF(),n.getG(),n.getH(),n.getOffsetCoord().distance(end));
			this.openMap.remove(n.getOffsetCoord());
			//判断此节点是否是目标点，是则找到了，跳出
			if(endAround.contains(n.getOffsetCoord())){
				isFind = true;
				break;
			}
			closeList.add(n);
			OffsetCoord curOffsetCoord = n.getOffsetCoord();
			for (int i = 0; i < 6; i++) {
				OffsetCoord neighbor = curOffsetCoord.neighbor(i);
				checkNewNode(neighbor, n, DIST_STRAIGHT);
			}
		}
		if(isFind){
			return n;
		}else{
			return null;
		}
	}

	/**
	 * 判断地图是否阻挡
	 * @param OffsetCoord
	 * @return true是阻挡
	 */
	protected boolean isBlock(OffsetCoord OffsetCoord)
	{
		if(OffsetCoord.x<0||OffsetCoord.x>=this.map.length||OffsetCoord.y<0||OffsetCoord.y>=this.map[0].length){
			return true;
		}
		byte block = map[OffsetCoord.x][OffsetCoord.y];
		if(block==1){
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		OffsetCoord offsetCoord = new OffsetCoord(1,-1);
		Hex hex = OffsetCoord.qoffsetToCube(OffsetCoord.ODD,offsetCoord);

		//for (int i = 0; i < 6; i++) {
		//	OffsetCoord offsetCoordNei = offsetCoord.neighbor(i);
		//	Hex hexNei = hex.neighbor(i);
		//	System.out.println("offsetCoordNei" +offsetCoordNei);
		//	System.out.println("hexNei" +OffsetCoord.qoffsetFromCube(OffsetCoord.ODD,hexNei));
		//	System.out.println("hex" +hexNei);
		//}

		Hex hexNei = new Hex(2,-1,-1);
		OffsetCoord offsetCoord1 = OffsetCoord.qoffsetFromCube(OffsetCoord.ODD,hexNei);
		Hex hex1 = OffsetCoord.qoffsetToCube(OffsetCoord.ODD,offsetCoord1);
		System.out.println();



	}


	
	private void checkNewNode(OffsetCoord neighbor, NodeOffsetCoord parentN, int dist){
		NodeOffsetCoord newNode = new NodeOffsetCoord(neighbor, parentN);
		//检查是否已在关闭列表中
		if(closeList.contains(newNode)){
			return;
		}
		//检查地图是否障碍点
		if(isBlock(neighbor)){
			closeList.add(newNode);
			return;
		}
		
		//计算G、H、F值
		calc(newNode, dist);

		NodeOffsetCoord existN = this.openMap.get(neighbor);

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
	
	private void calc(NodeOffsetCoord p, int dist){
		calcG(p, dist);
		calcH(p);
		calcF(p);
	}
	
	// 计算G值
	private void calcG(NodeOffsetCoord p, int dist) {
		if (p.getParentNode() == null) {
			p.setG(dist);
		} else {
			NodeOffsetCoord pp = p.getParentNode().getParentNode();
			if(pp!=null){
				OffsetCoord currPoint = p.getOffsetCoord();
				OffsetCoord preOffsetCoord = pp.getOffsetCoord();
				OffsetCoord note = p.getParentNode().getOffsetCoord();
				if(!(preOffsetCoord.x <<1 == currPoint.x +note.x && preOffsetCoord.y <<1 == currPoint.y +note.y)){
					dist += 5;	//拐弯加权
				}
			}
			p.setG(p.getParentNode().getG() + dist);
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
		n.setH(num * 10);
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

	private void setResultOffSetCoord(List<OffsetCoord> result, NodeOffsetCoord p){
		if(p.getParentNode() != null){
			setResultOffSetCoord(result, p.getParentNode());
		}
		result.add(p.getOffsetCoord());
	}
	
}