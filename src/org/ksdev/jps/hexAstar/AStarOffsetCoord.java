package org.ksdev.jps.hexAstar;



import org.ksdev.jps.test.OffsetCoord;

import java.util.*;

/**
 * A*算法
 * 路径不包含起点，包含终点
 */
public class AStarOffsetCoord {

	private PriorityQueue<NodeOffsetCoord> openList = new PriorityQueue<>();
	//private NodeOffsetCoord[][] openMap;

	private Map<Integer,NodeOffsetCoord> openMap = new HashMap<>();

	//地图阻挡信息
	private final byte[][] map;
	private final int width;
	private final int height;
	private NodeOffsetCoord endN;
	private long maxSearchTime = 2000;

	public AStarOffsetCoord(byte[][] map){
		this.map = map;
		this.width = map.length;
		this.height = map[0].length;
		//openMap = new NodeOffsetCoord[width][height];
	}




	

	public List<OffsetCoord> search(OffsetCoord start, OffsetCoord end, boolean incloudEndAround){
		NodeOffsetCoord n = searchPath(start,end,incloudEndAround);
		List<OffsetCoord> result = null;
		if(n!=null){
			result = new ArrayList<>();
			setResult(result, n);
		}
		return result;
	}

	public LinkedList<OffsetCoord> search(int x,int y,int tx,int ty,boolean incloudEndAround){
		LinkedList<OffsetCoord> result = new LinkedList<>();
		if(x==tx&&y==ty){
			return result;
		}
		long startMill = System.currentTimeMillis();
		OffsetCoord endOffsetCoord = new OffsetCoord(tx,ty);
		NodeOffsetCoord n = searchPath(new OffsetCoord(x,y),endOffsetCoord,incloudEndAround);
		if(n!=null){
			int closeCount = 0;
			for (NodeOffsetCoord value : openMap.values()) {
				if(value.getState()==2){
					closeCount++;
				}
			}
			System.out.println("a* 关闭列表长度"+closeCount);
			setResultOffSetCoord(result, n);
			if(!n.getOffsetCoord().equals(endOffsetCoord)&&!checkBound(endOffsetCoord,this.width,this.height)&&!isBlock(endOffsetCoord,this.map)){
				result.add(new OffsetCoord(tx,ty));
			}
		}else{
			//System.out.println("a*寻路没有找到路径 x "+ x+" y "+y+" tx "+tx+" ty "+ty);
			//SystemLogger.mapLogger.error("a*寻路没有找到路径 x {} y {} tx {} ty {}",x,y,tx,ty);
		}
		long useTime = System.currentTimeMillis()-startMill;
		if(useTime>100){
			//SystemLogger.mapLogger.info("a* 寻路结束 路径长度 {} 耗时 {}",result!=null?result.size():0,System.currentTimeMillis()-startMill);
		}
		if(!result.isEmpty()){
			//result = Floyd.floydSmooth(result,this.map);
			result.removeFirst();
		}
		//for (OffsetCoord offsetCoord : result) {
		//	System.out.println(" x "+offsetCoord.x+" y "+offsetCoord.y);
		//
		//}
		return result;
	}


	public List<OffsetCoord> searchOffsetCoord(OffsetCoord start,OffsetCoord end,boolean incloudEndAround){
		return search(start.x,start.y,end.x,end.y,incloudEndAround);
	}
	private void clear(){
		openList = new PriorityQueue<>();
		openMap = new HashMap<>();
				//new NodeOffsetCoord[this.width][this.height];
		endN = null;
	}


	private NodeOffsetCoord searchPath(OffsetCoord start, OffsetCoord end, boolean incloudEndAround){
		clear();
		NodeOffsetCoord startN =  new NodeOffsetCoord(start);
		NodeOffsetCoord endN = new NodeOffsetCoord(end);
		this.endN = endN;
		//起点先添加到开启列表中
		openList.add(startN);
		openMap.put(start.toInt(),startN);
		//openMap[start.x][start.y] = startN;
		//openMap.put(startN.getOffsetCoord(),startN);


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
			OffsetCoord curOffsetCoord = n.getOffsetCoord();
			NodeOffsetCoord offsetCoord = this.openMap.get(curOffsetCoord.toInt());
			//判断此节点是否是目标点，是则找到了，跳出
			if(endAround.contains(curOffsetCoord)){
				isFind = true;
				break;
			}
			offsetCoord.setState(2);
			//closeList.add(n);
			for (int i = 0; i < 6; i++) {
				OffsetCoord neighbor = curOffsetCoord.neighbor(i);
				checkNewNode(neighbor, n);
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
	public static final boolean isBlock(OffsetCoord OffsetCoord,byte[][] map)
	{
		return map[OffsetCoord.x][OffsetCoord.y]==1;
	}

	protected static final boolean checkBound(OffsetCoord OffsetCoord,int width,int height){
		if(OffsetCoord.x<0||OffsetCoord.x>=width||OffsetCoord.y<0||OffsetCoord.y>=height){
			return true;
		}
		return false;
	}


	
	private void checkNewNode(OffsetCoord neighbor, NodeOffsetCoord parentN){
		NodeOffsetCoord newNode = new NodeOffsetCoord(neighbor, parentN);
		if(checkBound(neighbor,this.width,this.height)){
			return;
		}
		NodeOffsetCoord existN = this.openMap.get(neighbor.toInt());
		//检查是否已在关闭列表中
		if(existN!=null&&existN.getState()==2){
			return;
		}
		//检查地图是否障碍点
		if(isBlock(neighbor,this.map)){
			newNode.setState(2);
			openMap.put(neighbor.toInt(),newNode);
			//this.openMap[neighbor.x][neighbor.y] = newNode ;
			return;
		}
		
		//计算G、H、F值
		calc(newNode);


		//如果已存在开启列表中，判断当前的G值是否更小，是则更新
		if(existN != null){
			if(newNode.getG() < existN.getG()){
				openList.remove(existN);
				existN.setG(newNode.getG());
				existN.setF(newNode.getF());
				existN.setParentNode(newNode.getParentNode());
				openList.add(existN);
				openMap.put(neighbor.toInt(),existN);
				//this.openMap[neighbor.x][neighbor.y] = existN;
			}
		}
		//不在开启列表中，则添加进去
		else{
			openList.add(newNode);
			openMap.put(neighbor.toInt(),newNode);
			//this.openMap[neighbor.x][neighbor.y] = newNode;
		}
		
	}
	
	private void calc(NodeOffsetCoord p){
		calcG(p);
		calcH(p);
		calcF(p);
	}
	
	// 计算G值
	private void calcG(NodeOffsetCoord p) {
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
			//		dist += 5;	//拐弯加权
			//	}
			//}
			p.setG(p.getParentNode().getG() + 1);
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

	private void setResultOffSetCoord(List<OffsetCoord> result, NodeOffsetCoord p){
		if(p.getParentNode() != null){
			setResultOffSetCoord(result, p.getParentNode());
		}
		result.add(p.getOffsetCoord());
	}
	
}