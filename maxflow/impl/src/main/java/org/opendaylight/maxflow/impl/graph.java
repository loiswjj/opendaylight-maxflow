/*
 * Copyright © 2017 Sammi, Inc and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.maxflow.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.opendaylight.maxflow.impl.Edge;
import java.util.Queue;
import java.util.LinkedList;

public class graph {
	private int Nodenum; //节点数目
	private int EdgeNum; //边的数目
	static int[] visited ;
	private List<Edge> edgelist = new ArrayList<Edge>();
	private float table[][]; //邻接表 cap
	private float RemainMartix[][]; //残留矩阵
	private float flow[]; //流表
	int[] previous;
	
	public void setNodenum(int NodeNum) {
		this.Nodenum = NodeNum;
	}
	
	public void init() {
		//initial remain matrix
		RemainMartix = new float[Nodenum][Nodenum];
		previous = new int[Nodenum];
		visited = new int[Nodenum];
		table = new float[Nodenum][Nodenum];
		flow = new float[Nodenum];
	}
	
	/**
	 * get number of node
	 */
	public int getNodenum() {
		return Nodenum;
	}
	
	/**
	 * get number of edge
	 */
	public int getEdgeNum() {
		return EdgeNum;
	}
	
	/**
	 * get table information
	 */
	public float[][] getTableInfo() {
		return table;
	}
	
	/**
	 * add edge for graph
	 */
	public void AddEdge(Edge e) {
		//table[e.getFrom()].add(e);
		edgelist.add(e);
		RemainMartix[e.getFrom()][e.getTo()] = e.getWeight();
		table[e.getFrom()][e.getTo()] = e.getWeight();
		EdgeNum++;
	}
	
	/**
	 * print each edge info of the graph
	 */
	public void DescribeGraph() {
		for(int i=0;i<EdgeNum;i++) {
			System.out.println(edgelist.get(i).DescribeEdge());
		}
	}
	
	/**
	 * by using bfs for find zengguang path from source node to destination node 
	 */
	public float BFSPath(int src,int dest) {
		float maxflow = Integer.MAX_VALUE * 1.0f;
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.add(src);
		Arrays.fill(previous, -1);
		previous[src] = -2;
		flow[src] = maxflow;
		
		while(!queue.isEmpty()) {
			int p = queue.poll();
			if(p == dest) {
				break;
			}
			
			for(int i=0;i<table.length;i++) {
				if(i!=src && previous[i] == -1&&table[p][i]>0) {
					previous[i] = p;
					flow[i] = Math.min(table[p][i], flow[p]);
					queue.add(i);
				}
			}
		}
		if(previous[dest] == -1) {
			return -1.0f;
		}
		return flow[dest];
	}
	
	/**
	 * used for calculate max flow between source node and destination node
	 */
	public float edmondsKarpMaxFlow(int src,int dest) {
		float increasement = 0.0f;
		float sum = 0.0f;
		while((increasement=BFSPath(src, dest))!=-1) {
			int index = dest;
			while(index != src) {
				table[previous[index]][index] -= increasement;
				table[index][previous[index]] += increasement;
				index = previous[index];
			}
			sum += increasement;
		}
		return sum;
	}
}
