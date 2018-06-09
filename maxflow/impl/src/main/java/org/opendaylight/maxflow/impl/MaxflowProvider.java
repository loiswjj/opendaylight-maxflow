/*
 * Copyright © 2017 Sammi, Inc and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.maxflow.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.RpcRegistration;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.maxflow.rev150105.MaxflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaxflowProvider {

    private static final Logger LOG = LoggerFactory.getLogger(MaxflowProvider.class);

    private final DataBroker dataBroker;
    private final RpcProviderRegistry rpcProviderReistry;

    public MaxflowProvider(final DataBroker dataBroker,RpcProviderRegistry rpcProviderReistry) {
        this.dataBroker = dataBroker;
        this.rpcProviderReistry = rpcProviderReistry;
    }
    
    private RpcRegistration<MaxflowService> serviceRegistration;

    /**
     * Method called when the blueprint container is created.
     */
    public void init() {
    	serviceRegistration = rpcProviderReistry.addRpcImplementation(
    			MaxflowService.class, new MaxflowImpl());
        LOG.info("MaxflowProvider Session Initiated");
    }

    /**
     * Method called when the blueprint container is destroyed.
     */
    public void close() {
    	serviceRegistration.close();
        LOG.info("MaxflowProvider Closed");
    }
}