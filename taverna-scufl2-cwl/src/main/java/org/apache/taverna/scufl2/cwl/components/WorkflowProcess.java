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

package org.apache.taverna.scufl2.cwl.components;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.DataLink;

import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.port.SenderPort;
import org.apache.taverna.scufl2.api.port.ReceiverPort;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.taverna.scufl2.cwl.*;

public class WorkflowProcess extends Process {

    private CWLParser cwlParser;

    private Map<String, InputWorkflowPort> workflowInputs = new HashMap<>();
    private Map<String, OutputWorkflowPort> workflowOutputs = new HashMap<>();
    private Map<String, Processor> workflowProcessors = new HashMap<>();
    private Map<String, InputProcessorPort> processorInputs = new HashMap<>();
    private Map<String, OutputProcessorPort> processorOutputs = new HashMap<>();
    private Set<DataLink> dataLinks = new HashSet<>();

    private Set<Process> processes = new HashSet<>();

    private Converter converter = new Converter();

    public WorkflowProcess() {
        this.name = "";
    }

    public WorkflowProcess(InputStream stream) {
        this();
        Yaml reader = new Yaml();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.valueToTree(reader.load(stream));

        cwlParser = new CWLParser(node);
        this.parse();
    }

    public WorkflowProcess(JsonNode node) {
        this();
        cwlParser = new CWLParser(node);
        this.parse();
    }

    public void parse() {
        parseInputs();
        parseOutputs();
        Set<Step> cwlSteps = cwlParser.parseSteps();
        this.processes = parseProcessors(cwlSteps);
    }

    public void parseInputs() {
        Set<PortDetail> cwlInputs = cwlParser.parseInputs();
        for (PortDetail port: cwlInputs) {
            String portId = port.getId();
            InputWorkflowPort workflowPort = converter.convertInputWorkflowPort(port);
            workflowInputs.put(portId, workflowPort);
        }
    }

    public void parseOutputs() {
        Set<PortDetail> cwlOutputs = cwlParser.parseOutputs();
        for(PortDetail port: cwlOutputs) {
            String portId = port.getId();
            OutputWorkflowPort workflowPort = converter.convertOutputWorkflowPort(port);
            workflowOutputs.put(portId, workflowPort);
        }
    }

    public Set<Process> parseProcessors(Set<Step> cwlSteps) {
        Set<Process> result = new HashSet<>();

        for(Step step: cwlSteps) {
            Process process = step.getRun();
            process.setInputPorts(step.getInputs());
            process.setOutputPorts(step.getOutputs());
            insideInputPorts.addAll(step.getInputs());
            insideOutputPorts.addAll(step.getOutputs());
            result.add(process);
        }

        return result;
    }

//    public void parseProcessors(Set<Step> cwlSteps) {
//        for(Step step: cwlSteps) {
//
//            Processor processor = converter.convertStepToProcessor(step);
//            workflowProcessors.put(step.getId(), processor);
//
//            // TODO: Add only receiver and sender ports from the Process interface
//            for(StepInput stepInput: step.getInputs()) {
//                InputProcessorPort processorPort = new InputProcessorPort(processor, stepInput.getId());
//                processorInputs.put(stepInput.getId(), processorPort);
//            }
//            for(StepOutput stepOutput: step.getOutputs()) {
//                OutputProcessorPort processorPort = new OutputProcessorPort(processor, stepOutput.getId());
//                processorOutputs.put(stepOutput.getId(), processorPort);
//            }
//        }
//    }

    public void parseDataLinks(Set<Step> cwlSteps) {
        for(Step step: cwlSteps) {
            for(InputPort stepInput: step.getInputs()) {
                String[] sourcePath = stepInput.getSource().split("/");
                String source = sourcePath[sourcePath.length-1];
                source = source.replace("#", "");

                DataLink dataLink = new DataLink();
                SenderPort sender = workflowInputs.get(source);
                if(sender == null) {
                    sender = processorOutputs.get(source);
                }
                if(sender == null) {
                    throw new NullPointerException("Cannot find sender port with name: " + source);
                }
                String receiverId = stepInput.getName();
                ReceiverPort receiver = workflowOutputs.get(receiverId);
                if(receiver == null) {
                    receiver = processorInputs.get(receiverId);
                }
                if(receiver == null) {
                    throw new NullPointerException("Cannot find receiver port with name: " + receiverId);
                }
                dataLink.setSendsTo(receiver);
                dataLink.setReceivesFrom(sender);
                dataLinks.add(dataLink);
            }
        }
    }

    public Map<String, InputWorkflowPort> getWorkflowInputs() {
        return workflowInputs;
    }

    public void setWorkflowInputs(Map<String, InputWorkflowPort> workflowInputs) {
        this.workflowInputs = workflowInputs;
    }

    public Map<String, OutputWorkflowPort> getWorkflowOutputs() {
        return workflowOutputs;
    }

    public void setWorkflowOutputs(Map<String, OutputWorkflowPort> workflowOutputs) {
        this.workflowOutputs = workflowOutputs;
    }

    public Map<String, Processor> getWorkflowProcessors() {
        return workflowProcessors;
    }

    public void setWorkflowProcessors(Map<String, Processor> workflowProcessors) {
        this.workflowProcessors = workflowProcessors;
    }

    public Map<String, InputProcessorPort> getProcessorInputs() {
        return processorInputs;
    }

    public void setProcessorInputs(Map<String, InputProcessorPort> processorInputs) {
        this.processorInputs = processorInputs;
    }

    public Map<String, OutputProcessorPort> getProcessorOutputs() {
        return processorOutputs;
    }

    public void setProcessorOutputs(Map<String, OutputProcessorPort> processorOutputs) {
        this.processorOutputs = processorOutputs;
    }

    public Set<DataLink> getDataLinks() {
        return dataLinks;
    }

    public void setDataLinks(Set<DataLink> dataLinks) {
        this.dataLinks = dataLinks;
    }

    public Set<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(Set<Process> processes) {
        this.processes = processes;
    }
}