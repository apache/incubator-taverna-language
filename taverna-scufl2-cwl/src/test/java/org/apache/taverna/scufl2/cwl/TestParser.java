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


import java.util.Set;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.DataLink;

import org.apache.taverna.scufl2.api.common.NamedSet;

import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;


public class TestParser {
    private static final String HELLO_WORLD_CWL = "/hello_world.cwl";

    private static JsonNode cwlFile;
    private WorkflowParser parser;
    private Workflow workflow;

    @Before
    public void initialize() {

        Yaml reader = new Yaml();
        ObjectMapper mapper = new ObjectMapper();
        cwlFile = mapper.valueToTree(reader.load(TestParser.class.getResourceAsStream(HELLO_WORLD_CWL)));
        System.out.println(cwlFile);
        this.parser = new WorkflowParser(cwlFile);

        this.workflow = parser.buildWorkflow();
    }

    @Test
    public void testParseInputs() throws Exception {

        workflow.setParent(null);
        NamedSet<InputWorkflowPort> workflowInputs = workflow.getInputPorts();

        Workflow expectedWorkflow = new Workflow(workflow.getName());
        NamedSet<InputWorkflowPort> expectedInputs = expectedWorkflow.getInputPorts();
        expectedInputs.add(new InputWorkflowPort(expectedWorkflow, "name"));

        assertEquals(expectedInputs, workflowInputs);
    }

    @Test
    public void testParseProcessors() throws Exception {

        workflow.setParent(null);
        NamedSet<Processor> workflowProcessors = workflow.getProcessors();

        Workflow expectedWorkflow = new Workflow(workflow.getName());
        NamedSet<Processor> expectedProcessors = expectedWorkflow.getProcessors();
        expectedProcessors.add(new Processor(expectedWorkflow, "step1"));

        assertEquals(expectedProcessors, workflowProcessors);
    }

    @Test
    public void testParseDataLinks() throws Exception {

        Set<DataLink> workflowDataLinks = workflow.getDataLinks();
        Set<DataLink> expectedDataLinks = new HashSet<>();
        NamedSet<Processor> processorSet = workflow.getProcessors();
        // processorSet has one processor
        Processor processor = processorSet.getByName("step1");
        expectedDataLinks.add(
                new DataLink(
                        workflow,
                        new InputWorkflowPort(workflow, "name"),
                        new InputProcessorPort(processor, "text")
                )
        );

        assertEquals(1, workflowDataLinks.size());
        assertEquals(expectedDataLinks, workflowDataLinks);
    }
}
