package org.ksdev.jps.astar2;


/**
 * A*节点
 * @author chenqunhao
 * @createDate 2015-7-26 上午11:51:24
 */
public class Node implements Comparable<Node>{

	private int x;
	private int y;
	private Node parentNode;
	
	private int F;
	private int G;
	private int H;
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Node(int x, int y, Node parentNode) {
		this.x = x;
		this.y = y;
		this.parentNode = parentNode;
	}

	public int toInteger(){
		return x*100000+y;
	}
	
	@Override
	public int hashCode() {
		int h = 31 + this.getX();
		h = 31 * h + this.getY();
		return h;
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
		return p.getX() == this.getX() && p.getY() == this.getY();
	}

	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
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
	public String toString() {
		return "Point [x=" + x + ", y=" + y + ", F=" + F + ", G=" + G + ", H="
				+ H + "]";
	}

	@Override
	public int compareTo(Node o) {
		return this.F - o.getF();
	}
	
}
