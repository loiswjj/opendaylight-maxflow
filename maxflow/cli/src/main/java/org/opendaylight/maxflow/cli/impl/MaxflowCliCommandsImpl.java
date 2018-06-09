/*
 * Copyright © 2017 Sammi, Inc and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.maxflow.cli.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opendaylight.maxflow.cli.api.MaxflowCliCommands;

public class MaxflowCliCommandsImpl implements MaxflowCliCommands {

    private static final Logger LOG = LoggerFactory.getLogger(MaxflowCliCommandsImpl.class);
    private final DataBroker dataBroker;

    public MaxflowCliCommandsImpl(final DataBroker db) {
        this.dataBroker = db;
        LOG.info("MaxflowCliCommandImpl initialized");
    }

    @Override
    public Object testCommand(Object testArgument) {
        return "This is a test implementation of test-command";
    }
}