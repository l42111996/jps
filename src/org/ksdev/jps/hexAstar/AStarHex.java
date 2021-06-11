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
	//地图阻挡信息
	private final Map<Hex,Byte> map;
	private Node endN;
	private final int weith;
	private final int height;
	
	public AStarHex(Map<Hex, Byte> map, int weith, int height){
		this.map = map;
		this.weith = weith;
		this.height = height;
	}
	

	public List<Hex> search(Hex start,Hex end,boolean incloudEndAround){
		Node n = searchPath(start,end,incloudEndAround);
		List<Hex> result = null;
		if(n!=null){
			result = new ArrayList<>();
			setResult(result, n);
		}
		return result;
	}

	public LinkedList<OffsetCoord> search(int x, int y, int tx, int ty, boolean incloudEndAround){
		long startMill = System.currentTimeMillis();
		Hex endHex = OffsetCoord.qoffsetToCube(OffsetCoord.ODD,tx,ty);
		Node n = searchPath(OffsetCoord.qoffsetToCube(OffsetCoord.ODD,x,y),endHex,incloudEndAround);
		LinkedList<OffsetCoord> result = null;
		if(n!=null){
			result = new LinkedList<>();
			setResultOffSetCoord(result, n);
			if(!n.getHex().equals(endHex)&&!isBlock(endHex)){
				result.add(new OffsetCoord(tx,ty));
			}
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


	private Node searchPath(Hex start,Hex end,boolean incloudEndAround){
		clear();
		Node startN =  new Node(start);
		Node endN = new Node(end);
		this.endN = endN;
		//起点先添加到开启列表中
		openList.add(startN);
		openMap.put(startN.getHex(),startN);


		//开启列表中有节点的话，取出第一个节点，即最小F值的节点
		Node n;

		Set<Hex> endAround = new HashSet<>();
		if(incloudEndAround){
			for (int direction = 0; direction < 6; direction++) {
				endAround.add(endN.getHex().neighbor(direction));
			}
		}
		endAround.add(endN.getHex());

		boolean isFind = false;
		while((n = openList.poll()) != null){
			//SystemLogger.exceptionLogger.error("f {} g {} h {} distance {}",n.getF(),n.getG(),n.getH(),n.getHex().distance(end));
			this.openMap.remove(n.getHex());
			//判断此节点是否是目标点，是则找到了，跳出
			if(endAround.contains(n.getHex())){
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
		if(isFind){
			return n;
		}else{
			return null;
		}
	}

	/**
	 * 判断地图是否阻挡
	 * @param hex
	 * @return true是阻挡
	 */
	protected boolean isBlock(Hex hex)
	{
		Byte block = map.get(hex);
		if(block!=null){
			return block==1;
		}
		else{
			//超出地图边界
			OffsetCoord offsetCoord = OffsetCoord.qoffsetFromCube(OffsetCoord.ODD,hex);
			if(offsetCoord.x<0||offsetCoord.x>this.weith||offsetCoord.y<0||offsetCoord.y>this.height){
				return true;
			}
		}
		return false;
	}


	
	private void checkNewNode(Hex neighbor, Node parentN, int dist){
		Node newNode = new Node(neighbor, parentN);
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
			//Node pp = p.getParentNode().getParentNode();
			//if(pp!=null){
			//	Hex currPoint = p.getHex();
			//	Hex preHex = pp.getHex();
			//	Hex note = p.getParentNode().getHex();
			//	if(!(preHex.x <<1 == currPoint.x +note.x && preHex.z <<1 == currPoint.z +note.z &&preHex.y <<1 == currPoint.y +note.y)){
			//		dist += 5;	//拐弯加权
			//	}
			//}
			p.setG(p.getParentNode().getG() + dist);
		}
	}

	// 计算H值
	private void calcH(Node n) {
		final Hex endHex = endN.getHex();
		final Hex nowHex = n.getHex();
		int endY = OffsetCoord.qoffsetYFromCube(endHex.x,endHex.z);
		int endX = OffsetCoord.qoffsetXFromCube(endHex.x);

		int nowX = OffsetCoord.qoffsetYFromCube(nowHex.x,nowHex.z);
		int nowY = OffsetCoord.qoffsetXFromCube(nowHex.x);
		int num =
				//n.getHex().distance(endN.getHex());
				Math.abs(nowX - endX) + Math.abs(nowY - endY);
		n.setH(num * 10);
	}

	// 计算F值
	private void calcF(Node p) {
		p.setF(p.getG() + p.getH());
	}
	
	private void setResult(List<Hex> result, Node p){
		if(p.getParentNode() != null){
			setResult(result, p.getParentNode());
		}
		result.add(p.getHex());
	}

	private void setResultOffSetCoord(List<OffsetCoord> result, Node p){
		if(p.getParentNode() != null){
			setResultOffSetCoord(result, p.getParentNode());
		}
		result.add(OffsetCoord.qoffsetFromCube(OffsetCoord.ODD,p.getHex()));
	}
	
}