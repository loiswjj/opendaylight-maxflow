/*
 * Copyright © 2017 Sammi, Inc and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.maxflow.impl;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TopoInfo info = new TopoInfo();
		info.setRemainBw();
		//System.out.println(info.ConnectorInfo());
		info.setQosForNetwork(100);
		info.init(100.0f);
		info.createGraph();
		float maxflow = info.getMaxFlow("10.0.0.1", "10.0.0.2");
		System.out.print(maxflow);
		
		
	}

}
