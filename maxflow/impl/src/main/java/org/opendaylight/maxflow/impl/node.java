/*
 * Copyright © 2017 Sammi, Inc and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.maxflow.impl;

public class node {
	private String NodeIp;
	private int index;
	private String Id = null;
	
	public void setIp(String ip) {
		this.NodeIp = ip;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getIp() {
		return NodeIp;
	}
	
	public void SetType(String id) {
		this.Id = id;
	}
	
	public String GetType() {
		return Id;
	}
}
