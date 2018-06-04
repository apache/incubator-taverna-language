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

import java.util.*;

import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.core.Processor;

import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;

import com.fasterxml.jackson.databind.JsonNode;

public class Parser {

    private JsonNode cwlFile;
    private YAMLHelper yamlHelper;
    private Workflow workflow;

    public Parser(JsonNode cwlFile) {
        this.cwlFile = cwlFile;
        this.yamlHelper = new YAMLHelper();
        this.workflow = new Workflow();
        this.workflow.setInputPorts(parseInputs());
        this.workflow.setOutputPorts(parseOutputs());
    }

    public Workflow getWorkflow() {
        return this.workflow;
    }

    public Set<Step> parseSteps() {
        return yamlHelper.processSteps(cwlFile);
    }

    public Processor convertStepToProcessor(Step step) {
        Processor processor = new Processor(null, step.getId());
        // Convert input ports
        Set<InputProcessorPort> processorInputs = new HashSet<>();
        Set<StepInput> inputs = step.getInputs();
        for(StepInput input: inputs) {
            InputProcessorPort port = new InputProcessorPort(processor, input.getId());
            processorInputs.add(port);
        }
        processor.setInputPorts(processorInputs);
        // Convert output ports
        Set<OutputProcessorPort> processorOutputs = new HashSet<>();
        Set<StepOutput> outputs = step.getOutputs();
        for(StepOutput output: outputs) {
            OutputProcessorPort port = new OutputProcessorPort(processor, output.getId());
            processorOutputs.add(port);
        }
        processor.setOutputPorts(processorOutputs);

        return processor;
    }

    public Set<InputWorkflowPort> parseInputs() {
        Map<String, PortDetail> inputs = yamlHelper.processInputDetails(cwlFile);
        Map<String, Integer> inputDepths = yamlHelper.processInputDepths(cwlFile);

        if(inputs == null || inputDepths == null) {
            return null;
        }
        Set<InputWorkflowPort> result = new HashSet<InputWorkflowPort>();
        for(String id: inputs.keySet()) {
            PortDetail detail = inputs.get(id);
            int depth = inputDepths.get(id);
            InputWorkflowPort port = new InputWorkflowPort();
            port.setName(id);
            port.setDepth(depth);
            result.add(port);
        }

        return result;
    }

    public Set<OutputWorkflowPort> parseOutputs() {
        Map<String, PortDetail> inputs = yamlHelper.processOutputDetails(cwlFile);

        if(inputs == null) {
            return null;
        }
        Set<OutputWorkflowPort> result = new HashSet<OutputWorkflowPort>();
        for(String id: inputs.keySet()) {
            PortDetail detail = inputs.get(id);
            OutputWorkflowPort port = new OutputWorkflowPort();
            port.setName(id);
            result.add(port);
        }

        return result;
    }

}
