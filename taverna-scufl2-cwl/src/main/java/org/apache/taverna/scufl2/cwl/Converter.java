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

import org.apache.taverna.scufl2.api.common.NamedSet;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;

import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import org.apache.taverna.scufl2.api.port.ReceiverPort;
import org.apache.taverna.scufl2.api.port.SenderPort;
import org.apache.taverna.scufl2.cwl.components.Process;
import org.apache.taverna.scufl2.cwl.components.PortDetail;
import org.apache.taverna.scufl2.cwl.components.Step;
import org.apache.taverna.scufl2.cwl.components.InputPort;
import org.apache.taverna.scufl2.cwl.components.OutputPort;
import org.apache.taverna.scufl2.cwl.components.WorkflowProcess;
import org.apache.taverna.scufl2.cwl.components.CommandLineTool;
import org.apache.taverna.scufl2.cwl.components.Reference;

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
        Set<InputPort> inputs = step.getInputs();
        for(InputPort input: inputs) {
            InputProcessorPort port = new InputProcessorPort(processor, input.getName());
            processorInputs.add(port);
        }
        processor.setInputPorts(processorInputs);
        // Convert output ports
        Set<OutputProcessorPort> processorOutputs = new HashSet<>();
        Set<OutputPort> outputs = step.getOutputs();
        for(OutputPort output: outputs) {
            OutputProcessorPort port = new OutputProcessorPort(processor, output.getName());
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

    public WorkflowBundle buildWorkflowBundle(Process process) {
        WorkflowBundle bundle = new WorkflowBundle();
        if(!(process instanceof WorkflowProcess)) {
            throw new UnsupportedOperationException("WorkflowBundle is not created without an initial workflow yet");
        }
        Workflow workflow = convertWorkflowProcess((WorkflowProcess) process, bundle);
        workflow.setParent(bundle);

        return bundle;
    }

    public Workflow convertWorkflowProcess(WorkflowProcess workflowProcess, WorkflowBundle bundle) {
        Workflow workflow = new Workflow();
        Set<InputWorkflowPort> inputs = new HashSet<>(workflowProcess.getWorkflowInputs().values());
        Set<OutputWorkflowPort> outputs = new HashSet<>(workflowProcess.getWorkflowOutputs().values());
        workflow.setInputPorts(inputs);
        workflow.setOutputPorts(outputs);
        Set<InputPort> inputPorts = workflowProcess.getInsideInputPorts();
        Set<OutputPort> outputPorts = workflowProcess.getInsideOutputPorts();

        NamedSet<InputProcessorPort> inputProcessorPorts = new NamedSet<>();
        NamedSet<OutputProcessorPort> outputProcessorPorts = new NamedSet<>();

        for(Process process: workflowProcess.getProcesses()) {
            Processor processor;
            if(process instanceof WorkflowProcess) {
                Workflow childWorkflow = convertWorkflowProcess((WorkflowProcess) process, bundle); // TODO: Add nested relationship
                processor = new Processor(workflow, childWorkflow.getName()); // TODO: Check if we want the processor to have the same name as the childworkflow
                createProcessPortsFromWorkflow(processor, childWorkflow);
                bundle.getWorkflows().add(childWorkflow);
            } else if(process instanceof CommandLineTool) {
                processor = convertCommandLineTool((CommandLineTool) process);
                workflow.getProcessors().add(processor);
            } else {
                assert(process instanceof Reference);
                processor = convertReference((Reference) process);
                workflow.getProcessors().add(processor);
            }
            inputProcessorPorts.addAll(processor.getInputPorts());
            outputProcessorPorts.addAll(processor.getOutputPorts());
        }

        // DataLinks
        for(InputPort port: inputPorts) {
            String senderName = port.getSource();
            String destName = port.getName();
            SenderPort senderPort = outputProcessorPorts.getByName(senderName);
            if(senderPort == null) { // Source is one of the Workflow inputs
                senderPort = workflow.getInputPorts().getByName(senderName);
            }
            ReceiverPort receiverPort = inputProcessorPorts.getByName(destName);
            if(receiverPort == null) { // Destination is one of the Workflow outputs
                receiverPort = workflow.getOutputPorts().getByName(destName);
            }
            new DataLink(workflow, senderPort, receiverPort);
        }

        return workflow;
    }

    public void createProcessPortsFromWorkflow(Processor processor, Workflow workflow) {
        for(InputWorkflowPort inputWorkflowPort: workflow.getInputPorts()) {
            processor.getInputPorts().add(new InputProcessorPort(processor, inputWorkflowPort.getName()));
        }
        for(OutputWorkflowPort outputWorkflowPort: workflow.getOutputPorts()) {
            processor.getOutputPorts().add(new OutputProcessorPort(processor, outputWorkflowPort.getName()));
        }
    }

    public Processor convertCommandLineTool(CommandLineTool command) {
        Processor processor = new Processor(null, command.getBaseCommand());

        processor.setInputPorts(convertInputProcessPorts(command.getInputPorts()));
        processor.setOutputPorts(convertOutputProcessPorts(command.getOutputPorts()));

        return processor;
    }

    public Processor convertReference(Reference reference) {
        Processor processor = new Processor(null, reference.getSource());

        processor.setInputPorts(convertInputProcessPorts(reference.getInputPorts()));
        processor.setOutputPorts(convertOutputProcessPorts(reference.getOutputPorts()));

        return processor;
    }

    public Set<InputProcessorPort> convertInputProcessPorts(Set<InputPort> inputPorts) {
        Set<InputProcessorPort> processorInputPorts = new HashSet<>();

        for(InputPort port: inputPorts) {
            processorInputPorts.add(new InputProcessorPort(null, port.getName()));
        }

        return processorInputPorts;
    }

    public Set<OutputProcessorPort> convertOutputProcessPorts(Set<OutputPort> outputPorts) {
        Set<OutputProcessorPort> processorOutputPorts = new HashSet<>();

        for(OutputPort port: outputPorts) {
            processorOutputPorts.add(new OutputProcessorPort(null, port.getName()));
        }

        return processorOutputPorts;
    }
}