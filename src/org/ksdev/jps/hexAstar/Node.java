package org.ksdev.jps.hexAstar;


import org.ksdev.jps.test.Hex;

/**
 * A*节点
 * @author chenqunhao
 * @createDate 2015-7-26 上午11:51:24
 */
public class Node implements Comparable<Node>{

	private Hex hex;
	private Node parentNode;
	
	private int F;
	private int G;
	private int H;

	public Node(Hex hex) {
		this.hex = hex;
	}

	public Node(Hex hex, Node parentNode) {
		this.hex = hex;
		this.parentNode = parentNode;
	}


	@Override
	public int hashCode() {
		return hex.hashCode();
	}

	@Override
	public String toString() {
		return "Node{" +
				"hex=" + hex +
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
		if(!(obj instanceof Node))
			return false;
		Node p = (Node) obj;
		return hex.x == p.hex.x && hex.y == p.hex.y &&hex.z == p.hex.z;
	}


	public Hex getHex() {
		return hex;
	}

	public Node getParentNode() {
		return parentNode;
	}

	public void setParentNode(Node parentNode) {
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
	public int compareTo(Node o) {
		return this.F - o.getF();
	}
	
}
