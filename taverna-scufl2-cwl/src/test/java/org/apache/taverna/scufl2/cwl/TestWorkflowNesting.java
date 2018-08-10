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

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.taverna.scufl2.cwl.components.Process;
import org.apache.taverna.scufl2.cwl.components.ProcessFactory;
import org.apache.taverna.scufl2.cwl.components.CommandLineTool;
import org.apache.taverna.scufl2.cwl.components.WorkflowProcess;
import org.apache.taverna.scufl2.cwl.components.Reference;


public class TestWorkflowNesting {
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
    public void testWorkflowWithProcessor() {
        JsonNode cwlFile = loadYamlFile(WORKFLOW_WITH_COMMAND);

        WorkflowProcess workflow = (WorkflowProcess) ProcessFactory.createProcess(cwlFile);
        Set<Process> processes = workflow.getProcesses();
        assertEquals(processes.size(), 1);
        Process child = processes.iterator().next();
        assert(child instanceof CommandLineTool);
        assertEquals(((CommandLineTool) child).getBaseCommand(), "echo");
    }

    @Test
    public void testWorkflowWithWorkflow() {
        JsonNode cwlFile = loadYamlFile(WORKFLOW_WITH_WORKFLOW);

        WorkflowProcess workflow = (WorkflowProcess) ProcessFactory.createProcess(cwlFile);
        Set<Process> processes = workflow.getProcesses();
        assert(processes.size() == 1);
        Process child = processes.iterator().next();
        assert(child instanceof WorkflowProcess);
        WorkflowProcess childWorkflow = (WorkflowProcess) child;
        assertEquals(childWorkflow.getProcesses().size(), 1);

        Process reference = childWorkflow.getProcesses().iterator().next();

        assert(reference instanceof Reference);
        assertEquals(((Reference) reference).toString(), "example.cwl");
    }
}
