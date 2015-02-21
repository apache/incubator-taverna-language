package org.apache.taverna.scufl2.integration;
/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/


import static org.junit.Assert.*;

import org.junit.Test;

import org.apache.taverna.scufl2.api.common.AbstractCloneable;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyTopNode;

public class CloningIT {
    @Test
    public void cloneIterationStack() throws Exception {
        WorkflowBundleIO io = new WorkflowBundleIO();
        
        WorkflowBundle wf = io.readBundle(getClass().getResource("/clone-error.wfbundle"), null);

        Processor proc = wf.getMainWorkflow().getProcessors().getByName("Beanshell");
        IterationStrategyStack stack = proc.getIterationStrategyStack();
        IterationStrategyTopNode root = stack.get(0);
        assertNotSame(stack, root);
        assertNotEquals(stack, root);
        System.out.println(stack);
        System.out.println(root);
        @SuppressWarnings("unused")
		AbstractCloneable clone = wf.clone();
        
        
    }
}
