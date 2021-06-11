package org.ksdev.jps.hexAstar;


import org.ksdev.jps.test.OffsetCoord;

/**
 * A*节点
 * @author chenqunhao
 * @createDate 2015-7-26 上午11:51:24
 */
public class NodeOffsetCoord implements Comparable<NodeOffsetCoord>{

	private OffsetCoord offsetCoord;
	private NodeOffsetCoord parentNode;
	
	private int F;
	private int G;
	private int H;

	public NodeOffsetCoord(OffsetCoord offsetCoord) {
		this.offsetCoord = offsetCoord;
	}

	public NodeOffsetCoord(OffsetCoord offsetCoord, NodeOffsetCoord parentNode) {
		this.offsetCoord = offsetCoord;
		this.parentNode = parentNode;
	}


	@Override
	public int hashCode() {
		return offsetCoord.hashCode();
	}

	@Override
	public String toString() {
		return "Node{" +
				"hex=" + offsetCoord +
				", parentNode=" + parentNode +
				", F=" + F +
				", G=" + G +
				", H=" + H +
				'}';
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof NodeOffsetCoord))
			return false;
		NodeOffsetCoord p = (NodeOffsetCoord) obj;
		return offsetCoord.x == p.offsetCoord.x && offsetCoord.y == p.offsetCoord.y;
	}


	public OffsetCoord getOffsetCoord() {
		return offsetCoord;
	}

	public NodeOffsetCoord getParentNode() {
		return parentNode;
	}

	public void setParentNode(NodeOffsetCoord parentNode) {
		this.parentNode = parentNode;
	}

	public int getF() {
		return F;
	}

	public void setF(int F) {
		this.F = F;
	}
	

	public int getG() {
		return G;
	}

	public void setG(int g) {
		G = g;
	}

	public int getH() {
		return H;
	}

	public void setH(int h) {
		H = h;
	}

	@Override
	public int compareTo(NodeOffsetCoord o) {
		return this.F - o.getF();
	}
	
}
