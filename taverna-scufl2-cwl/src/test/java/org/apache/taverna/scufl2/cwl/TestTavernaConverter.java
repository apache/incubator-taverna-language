/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.taverna.scufl2.cwl;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.cwl.components.InputPort;
import org.apache.taverna.scufl2.cwl.components.Reference;
import org.apache.taverna.scufl2.cwl.components.Step;
import org.apache.taverna.scufl2.cwl.components.WorkflowProcess;
import org.junit.Test;
import static org.junit.Assert.assertEquals;


public class TestTavernaConverter {

    @Test
    public void testWorkflowToWorkflowProcess() {
        Workflow workflow = new Workflow();
        workflow.setName("workflowName");
        workflow.getInputPorts().add(new InputWorkflowPort(workflow, "input1"));
        workflow.getInputPorts().add(new InputWorkflowPort(workflow, "input2"));
        workflow.getOutputPorts().add(new OutputWorkflowPort(workflow, "output"));

        TavernaConverter converter = new TavernaConverter();

        WorkflowProcess workflowProcess = converter.convertWorkflow(workflow);
        assertEquals(workflow.getInputPorts().size(), workflowProcess.getInputPorts().size());
        assertEquals(workflow.getOutputPorts().size(), workflowProcess.getOutputPorts().size());

        Set<String> expectedInputNames = workflow.getInputPorts().stream().map(InputWorkflowPort::getName).collect(Collectors.toSet());
        Set<String> convertedInputNames = workflowProcess.getInputPorts().stream().map(InputPort::getName).collect(Collectors.toSet());

        assertEquals(expectedInputNames, convertedInputNames);

        assertEquals("output", workflowProcess.getOutputPorts().iterator().next().getName());
    }

    @Test
    public void testProcessorsToSteps() {
        Workflow workflow = new Workflow();
        workflow.setName("workflowName");
        workflow.getProcessors().add(new Processor(workflow, "processor1"));
        workflow.getProcessors().add(new Processor(workflow, "processor2"));

        Step step1 = new Step();
        step1.setRun(new Reference("processor1"));
        Step step2 = new Step();
        step2.setRun(new Reference("processor2"));
        Set<Step> steps = new HashSet<>();
        steps.add(step1);
        steps.add(step2);

        TavernaConverter converter = new TavernaConverter();
        Set<Step> convertedSteps = converter.convertProcessors(workflow);

        assertEquals(steps, convertedSteps);
    }
}
