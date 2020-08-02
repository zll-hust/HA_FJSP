package com.zll.FJSP.Data;

import java.util.ArrayList;

/**
 * Description:路径 存放关键路径/最长路径
 *
 * @author zll-hust E-mail:zh20010728@126.com
 * @date 创建时间：2020年6月11日 下午08:23:37
 */
public class Path {
	private ArrayList<Node> path;
	private int cost;

	public Path() {
		this.cost = 0;
		this.path = new ArrayList<>();
	}

	public void addNode(Node n) {
		path.add(n);
		cost += n.getCost();
	}

	public ArrayList<Node> getPath() {
		return path;
	}

	public int getCost() {
		return cost;
	}
}
