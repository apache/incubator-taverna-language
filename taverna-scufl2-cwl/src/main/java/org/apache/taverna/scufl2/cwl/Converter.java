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
import java.util.Map;
import java.util.HashMap;

import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.core.Processor;

import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class Converter {

    private JsonNodeFactory jsonNodeFactory;
    public Converter() {
        jsonNodeFactory = JsonNodeFactory.instance;
    }

    public InputWorkflowPort convertInputWorkflowPort(PortDetail input) {
        InputWorkflowPort port = new InputWorkflowPort();
        port.setName(input.getId());
        port.setDepth(input.getDepth());

        return port;
    }

    public OutputWorkflowPort convertOutputWorkflowPort(PortDetail input) {
        OutputWorkflowPort port = new OutputWorkflowPort();
        port.setName(input.getId());

        return port;
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

    public JsonNode convertWorkflowProcessToJsonNode(WorkflowProcess workflow) {
        ObjectNode result = jsonNodeFactory.objectNode();
        ObjectNode inputs = convertInputWorkflows(workflow.getWorkflowInputs());
        ObjectNode outputs = convertOutputWorkflows(workflow.getWorkflowOutputs());
        ObjectNode steps = convertProcessors(workflow);
        result.put("inputs", inputs);
        result.put("outputs", outputs);
        result.put("steps", steps);

        return result;
    }

    private ObjectNode convertInputWorkflows(Map<String, InputWorkflowPort> workflowInputs) {
        ObjectNode node = jsonNodeFactory.objectNode();
        for(Map.Entry<String, InputWorkflowPort> entry: workflowInputs.entrySet()) {
            String name = entry.getKey();
            node.put(name, "string");  // TODO: Put the correct input type and not just string
        }

        return node;
    }

    private ObjectNode convertOutputWorkflows(Map<String, OutputWorkflowPort> workflowOutputs) {
        ObjectNode node = jsonNodeFactory.objectNode();
        for(Map.Entry<String, OutputWorkflowPort> entry: workflowOutputs.entrySet()) {
            String name = entry.getKey();
            node.put(name, "string");  // TODO: Put the correct input type and not just string
        }

        return node;
    }

    private ObjectNode convertProcessors(WorkflowProcess workflow) {
        ObjectNode node = jsonNodeFactory.objectNode();
        Map<String, Processor> processors = workflow.getWorkflowProcessors();

        for(Map.Entry<String, Processor> entry: processors.entrySet()) {
            Processor processor = entry.getValue();
            ObjectNode step = jsonNodeFactory.objectNode();
            ArrayNode inputs = jsonNodeFactory.arrayNode();
            ArrayNode outputs = jsonNodeFactory.arrayNode();
            for(InputProcessorPort port: processor.getInputPorts()) {
                ObjectNode input = jsonNodeFactory.objectNode();
                input.put(port.getName(), "string");
                input.put("source", "");
                inputs.add(input);
            }
            for(OutputProcessorPort port: processor.getOutputPorts()) {
                ObjectNode output = jsonNodeFactory.objectNode();
                output.put(port.getName(), "string");
                outputs.add(output);
            }
            step.put("run", "NotImplemented");
            step.put("inputs", inputs);
            step.put("outputs", outputs);
            node.put(processor.getName(), step);
        }

        return node;
    }

    public PortDetail convertToPortDetail(InputWorkflowPort inPort) {
        int depth = inPort.getDepth();
        String id = inPort.getName();
        PortDetail port = new PortDetail();
        port.setId(id);
        port.setDepth(depth);

        return port;
    }

    public PortDetail convertToPortDetail(OutputWorkflowPort outPort) {
        String id = outPort.getName();
        PortDetail port = new PortDetail();
        port.setId(id);

        return port;
    }
}