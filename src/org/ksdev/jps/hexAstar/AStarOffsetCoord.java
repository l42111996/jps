package org.ksdev.jps.hexAstar;



import org.ksdev.jps.test.OffsetCoord;

import java.util.*;

/**
 * A*算法
 * 路径不包含起点，包含终点
 */
public class AStarOffsetCoord {

	public static final int DIST_STRAIGHT = 10;

	private PriorityQueue<NodeOffsetCoord> openList = new PriorityQueue<>();
	private NodeOffsetCoord[][] openMap;
	//地图阻挡信息
	private final byte[][] map;
	private NodeOffsetCoord endN;
	private long maxSearchTime = 2000;

	public AStarOffsetCoord(byte[][] map){
		this.map = map;
		openMap = new NodeOffsetCoord[map.length][map[0].length];
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
		LinkedList<OffsetCoord> result = new LinkedList<>();
		if(x==tx&&y==ty){
			return result;
		}
		long startMill = System.currentTimeMillis();
		OffsetCoord endOffsetCoord = new OffsetCoord(tx,ty);
		NodeOffsetCoord n = searchPath(new OffsetCoord(x,y),endOffsetCoord,incloudEndAround);
		if(n!=null){
			setResultOffSetCoord(result, n);
			if(!n.getOffsetCoord().equals(endOffsetCoord)&&!checkBound(endOffsetCoord)&&!isBlock(endOffsetCoord)){
				result.add(new OffsetCoord(tx,ty));
			}
		}else{
			System.out.println("a*寻路没有找到路径 x "+ x+" y "+y+" tx "+tx+" ty "+ty);
			//SystemLogger.mapLogger.error("a*寻路没有找到路径 x {} y {} tx {} ty {}",x,y,tx,ty);
		}
		long useTime = System.currentTimeMillis()-startMill;
		if(useTime>100){
			//SystemLogger.mapLogger.info("a* 寻路结束 路径长度 {} 耗时 {}",result!=null?result.size():0,System.currentTimeMillis()-startMill);
		}
		if(!result.isEmpty()){
			result.removeFirst();
		}
		System.out.println(openList.size());
		return result;
	}


	public List<OffsetCoord> searchOffsetCoord(OffsetCoord start,OffsetCoord end,boolean incloudEndAround){
		return search(start.x,start.y,end.x,end.y,incloudEndAround);
	}
	private void clear(){
		openList = new PriorityQueue<>();
		openMap = new NodeOffsetCoord[map.length][map[0].length];
		//closeList = new HashSet<>();
		endN = null;
	}


	private NodeOffsetCoord searchPath(OffsetCoord start, OffsetCoord end, boolean incloudEndAround){
		clear();
		NodeOffsetCoord startN =  new NodeOffsetCoord(start);
		NodeOffsetCoord endN = new NodeOffsetCoord(end);
		this.endN = endN;
		//起点先添加到开启列表中
		openList.add(startN);
		openMap[start.x][start.y] = startN;
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
			NodeOffsetCoord offsetCoord = this.openMap[curOffsetCoord.x][curOffsetCoord.y];
			//判断此节点是否是目标点，是则找到了，跳出
			if(endAround.contains(curOffsetCoord)){
				isFind = true;
				break;
			}
			offsetCoord.setState(2);
			//closeList.add(n);
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
		return map[OffsetCoord.x][OffsetCoord.y]==1;
	}

	protected boolean checkBound(OffsetCoord OffsetCoord){
		if(OffsetCoord.x<0||OffsetCoord.x>=this.map.length||OffsetCoord.y<0||OffsetCoord.y>=this.map[0].length){
			return true;
		}
		return false;
	}


	
	private void checkNewNode(OffsetCoord neighbor, NodeOffsetCoord parentN, int dist){
		NodeOffsetCoord newNode = new NodeOffsetCoord(neighbor, parentN);
		if(neighbor.x<0||neighbor.y<0||neighbor.x>=this.openMap.length||neighbor.y>=this.openMap[0].length){
			return;
		}
		NodeOffsetCoord existN = this.openMap[neighbor.x][neighbor.y];
		//检查是否已在关闭列表中
		if(existN!=null&&existN.getState()==2){
			return;
		}
		//检查地图是否障碍点
		if(isBlock(neighbor)){
			newNode.setState(2);
			this.openMap[neighbor.x][neighbor.y] = newNode ;
			return;
		}
		
		//计算G、H、F值
		calc(newNode, dist);


		//如果已存在开启列表中，判断当前的G值是否更小，是则更新
		if(existN != null){
			if(newNode.getG() < existN.getG()){
				openList.remove(existN);
				existN.setG(newNode.getG());
				existN.setF(newNode.getF());
				existN.setParentNode(newNode.getParentNode());
				openList.add(existN);
				this.openMap[neighbor.x][neighbor.y] = existN;
			}
		}
		//不在开启列表中，则添加进去
		else{
			openList.add(newNode);
			this.openMap[neighbor.x][neighbor.y] = newNode;
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