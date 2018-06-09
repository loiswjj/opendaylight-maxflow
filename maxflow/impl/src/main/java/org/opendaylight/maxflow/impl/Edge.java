/*
 * Copyright © 2017 Sammi, Inc and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.maxflow.impl;

import org.opendaylight.maxflow.impl.node;

public class Edge {
	private float Maxweight = 10.0f;
	private float weight; //权重
	private node from; //边的起点
	private node to; //边的终点
	private String link_id;
	boolean isVisited = false;
	
	public void setId(String id) {
		this.link_id = id;
	}
	
	public void setFrom(node from) {
		this.from = from;
	}
	
	public void setTo(node to) {
		this.to = to;
	}
	
	public String getId() {
		return link_id;
	}
	
	public int getFrom() {
		return from.getIndex();
	}
	
	public int getTo() {
		return to.getIndex();
	}
	
	public float getWeight() {
		return weight;
	}
	
	public void setMaxWeight(float weight) {
		this.Maxweight = weight;
	}
	
	public float getMaxWeight() {
		return Maxweight;
	}
	
	public void setWeight(float weight) {
		this.weight = this.getMaxWeight() - weight;
	}
	
	public String DescribeEdge() {
		String s = from.GetType() + "->" +to.GetType()+",weight:"+weight;
		//String s = from.getIndex() + "->" +to.getIndex()+",weight:"+weight;
		return s;
	}
	
	public void setVisited(boolean visited) {
		this.isVisited = visited;
	}
	
	public boolean getIsVisited() {
		return isVisited;
	}
}
