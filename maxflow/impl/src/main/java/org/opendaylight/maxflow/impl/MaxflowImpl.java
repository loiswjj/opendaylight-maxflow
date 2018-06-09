/*
 * Copyright © 2017 Sammi, Inc and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.maxflow.impl;

import java.util.concurrent.Future;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.maxflow.rev150105.MaxflowService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.maxflow.rev150105.MaxFlowInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.maxflow.rev150105.MaxFlowOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.maxflow.rev150105.MaxFlowOutputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

public class MaxflowImpl implements MaxflowService{
	@Override
	public Future<RpcResult<MaxFlowOutput>> maxFlow(MaxFlowInput input){
		//init
		TopoInfo info = new TopoInfo();
		//setQos
		info.setQosForNetwork(Float.valueOf(input.getMaxBandwidth()));
		info.setRemainBw();
		info.init(Float.valueOf(input.getMaxBandwidth()));
		info.createGraph();
		float maxflow = info.getMaxFlow(input.getSource(), input.getDestination());
		//model set
		MaxFlowOutputBuilder maxflowBuilder = new MaxFlowOutputBuilder();
		maxflowBuilder.setResult(maxflow+"");
		return RpcResultBuilder.success(maxflowBuilder.build()).buildFuture();
	}
}
