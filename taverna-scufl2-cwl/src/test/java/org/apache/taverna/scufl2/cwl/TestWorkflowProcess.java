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


import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.taverna.scufl2.api.core.Workflow;

import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;

import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WriterException;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

import org.apache.taverna.scufl2.cwl.components.Process;
import org.apache.taverna.scufl2.cwl.components.ProcessFactory;
import org.apache.taverna.scufl2.cwl.components.WorkflowProcess;


public class TestWorkflowProcess {
    private static final String HELLO_WORLD_CWL = "/hello_world.cwl";
    private static final String WORKFLOW_WITH_COMMAND = "/workflow_with_command.cwl";

    private static JsonNode cwlFile;

    public JsonNode loadYamlFile(String filename) {

        Yaml reader = new Yaml();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.valueToTree(reader.load(TestWorkflowProcess.class.getResourceAsStream(filename)));

        return node;
    }

    @Test
    public void testWorkflowInputs() {
        cwlFile = loadYamlFile(HELLO_WORLD_CWL);
        WorkflowProcess workflow = new WorkflowProcess(cwlFile);
        workflow.parseInputs();

        Map<String, InputWorkflowPort> expected = new HashMap<>();
        InputWorkflowPort expectedInput = new InputWorkflowPort();
        expectedInput.setName("name");
        expected.put("name", expectedInput);

        assertEquals(expected, workflow.getWorkflowInputs());
    }

    @Test
    public void testWorkflowOutputs() {
        cwlFile = loadYamlFile(HELLO_WORLD_CWL);
        WorkflowProcess workflow = new WorkflowProcess(cwlFile);
        workflow.parseOutputs();

        Map<String, OutputWorkflowPort> expected = new HashMap<>();
        OutputWorkflowPort expectedOutput = new OutputWorkflowPort();
        expectedOutput.setName("output1");
        expected.put("output1", expectedOutput);

        assertEquals(expected, workflow.getWorkflowOutputs());
    }

    @Test
    public void testProcessType() {
        cwlFile = loadYamlFile(WORKFLOW_WITH_COMMAND);
        Process workflow = ProcessFactory.createProcess(cwlFile);

        assert(workflow instanceof WorkflowProcess);
    }

    @Test
    public void testConvertWorkflowProcess() {
        cwlFile = loadYamlFile(WORKFLOW_WITH_COMMAND);

        Process workflow = ProcessFactory.createProcess(cwlFile);
        workflow.parse();
        Converter converter = new Converter();
        JsonNode node = converter.convertWorkflowProcessToJsonNode((WorkflowProcess) workflow);
        printAsYaml(node);
    }

    private static void printAsYaml(JsonNode node) {
        try {
            String yaml = new YAMLMapper().writeValueAsString(node);
            System.out.println("YAML DATA");
            System.out.println(yaml);
        } catch (JsonProcessingException e) {
            System.err.println("Error writing JsonNode to YAML");
        }
    }

    public void writeWorkflowToFile(Workflow workflow) {
        try {
            WorkflowBundleIO io = new WorkflowBundleIO();
            File scufl2File = new File("workflow.wfbundle");
            WorkflowBundle bundle = io.createBundle();
            Set<Workflow> workflowSet = new HashSet<>();
            workflowSet.add(workflow);
            bundle.setWorkflows(workflowSet);
            bundle.setMainWorkflow(workflow);
            io.writeBundle(bundle, scufl2File, "text/vnd.taverna.scufl2.structure");
        } catch(WriterException e) {
            System.out.println("Exception writing the workflow bundle");
        } catch(IOException e) {
            System.out.println("IOException");
        }
    }

}
