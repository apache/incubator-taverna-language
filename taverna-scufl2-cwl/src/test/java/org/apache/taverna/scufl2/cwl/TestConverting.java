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


import org.apache.taverna.scufl2.api.core.DataLink;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.core.Processor;

import org.apache.taverna.scufl2.cwl.components.WorkflowProcess;
import org.apache.taverna.scufl2.cwl.components.ProcessFactory;

public class TestConverting {
    private static final String HELLO_WORLD_CWL = "/hello_world.cwl";
    private static final String WORKFLOW_WITH_COMMAND = "/workflow_with_command.cwl";
    private static final String WORKFLOW_WITH_WORKFLOW = "/workflow_with_workflow.cwl";

    private static JsonNode cwlFile;

    public JsonNode loadYamlFile(String filename) {

        Yaml reader = new Yaml();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.valueToTree(reader.load(TestWorkflowProcess.class.getResourceAsStream(filename)));

        return node;
    }

    @Test
    public void testWorkflowBundleWithOneWorkflow() {
        this.cwlFile = loadYamlFile(HELLO_WORLD_CWL);

        WorkflowProcess workflow = (WorkflowProcess) ProcessFactory.createProcess(cwlFile);
        Converter converter = new Converter();
        WorkflowBundle workflowBundle = converter.buildWorkflowBundle(workflow);

        assertEquals(workflowBundle.getWorkflows().size(), 1);
    }

    @Test
    public void testWorkflowBundleWithMultipleWorkflows() {
        this.cwlFile = loadYamlFile(WORKFLOW_WITH_WORKFLOW);

        WorkflowProcess workflow = (WorkflowProcess) ProcessFactory.createProcess(cwlFile);
        Converter converter = new Converter();
        WorkflowBundle workflowBundle = converter.buildWorkflowBundle(workflow);

        assertEquals(workflowBundle.getWorkflows().size(), 2);
    }

    @Test
    public void testConvertWorkflowProcessToWorkflow() {
        this.cwlFile = loadYamlFile(HELLO_WORLD_CWL);

        WorkflowProcess workflowProcess = (WorkflowProcess) ProcessFactory.createProcess(cwlFile);
        WorkflowBundle bundle = new WorkflowBundle();
        Converter converter = new Converter();
        Workflow workflow = converter.convertWorkflowProcess(workflowProcess, bundle);
        assertEquals(1, workflow.getProcessors().size());
        assertEquals(1, workflow.getInputPorts().size());
        assertEquals(1, workflow.getOutputPorts().size());

        Processor processor = workflow.getProcessors().iterator().next();
        assertEquals(1, processor.getInputPorts().size());
        assertEquals(0, processor.getOutputPorts().size());

        String processorPortName = processor.getInputPorts().iterator().next().getName();
        assertEquals("text", processorPortName);

        assertEquals(1, workflow.getDataLinks().size());
        DataLink dataLink = workflow.getDataLinks().iterator().next();
        assertEquals("name", dataLink.getReceivesFrom().getName());
        assertEquals("text", dataLink.getSendsTo().getName());
    }
}
