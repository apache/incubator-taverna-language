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
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import java.lang.NullPointerException;

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.DataLink;

import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.port.SenderPort;
import org.apache.taverna.scufl2.api.port.ReceiverPort;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;

import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WriterException;

import org.apache.taverna.scufl2.cwl.components.Step;
import org.apache.taverna.scufl2.cwl.components.PortDetail;
import org.apache.taverna.scufl2.cwl.components.InputPort;
import org.apache.taverna.scufl2.cwl.components.OutputPort;


public class WorkflowParser {

    private static final String FILE_NAME = "/hello_world.cwl";
    private CWLParser cwlParser;
    private Converter converter = new Converter();

    private Map<String, InputWorkflowPort> workflowInputs = new HashMap<>();
    private Map<String, OutputWorkflowPort> workflowOutputs = new HashMap<>();
    private Map<String, Processor> workflowProcessors = new HashMap<>();
    private Map<String, InputProcessorPort> processorInputs = new HashMap<>();
    private Map<String, OutputProcessorPort> processorOutputs = new HashMap<>();
    private Set<DataLink> dataLinks = new HashSet<DataLink>();

    public WorkflowParser() {
        Yaml reader = new Yaml();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode cwlFile = mapper.valueToTree(reader.load(WorkflowParser.class.getResourceAsStream(FILE_NAME)));
        this.cwlParser = new CWLParser(cwlFile);
    }

    public WorkflowParser(JsonNode cwlFile) {
        this.cwlParser = new CWLParser(cwlFile);
    }

    public Workflow buildWorkflow() {
        parseInputs();
        parseOutputs();
        Set<Step> cwlSteps = cwlParser.parseSteps();
        parseProcessors(cwlSteps);
//        parseDataLinks(cwlSteps);

        Workflow workflow = new Workflow();
        Set<InputWorkflowPort> inputs = new HashSet<>(workflowInputs.values());
        Set<OutputWorkflowPort> outputs = new HashSet<>(workflowOutputs.values());
        Set<Processor> processors = new HashSet<>(workflowProcessors.values());

        workflow.setInputPorts(inputs);
        workflow.setOutputPorts(outputs);
        workflow.setProcessors(processors);
        workflow.setDataLinks(dataLinks);

//        writeWorkflowToFile(workflow);

        return workflow;
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
            io.writeBundle(bundle, scufl2File, "application/vnd.taverna.scufl2.workflow-bundle");
        } catch(WriterException e) {
            System.out.println("Exception writing the workflow bundle");
        } catch(IOException e) {
            System.out.println("IOException");
        }
    }

    public void parseInputs() {
        Set<PortDetail> cwlInputs = cwlParser.parseInputs();
        if(cwlInputs != null) {
            for (PortDetail port : cwlInputs) {
                String portId = port.getId();
                InputWorkflowPort workflowPort = converter.convertInputWorkflowPort(port);
                workflowInputs.put(portId, workflowPort);
            }
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

    public void parseProcessors(Set<Step> cwlSteps) {
        for(Step step: cwlSteps) {
            Processor processor = converter.convertStepToProcessor(step);
            workflowProcessors.put(step.getId(), processor);

            for(InputPort stepInput: step.getInputs()) {
                InputProcessorPort processorPort = new InputProcessorPort(processor, stepInput.getName());
                processorInputs.put(stepInput.getName(), processorPort);
            }
            for(OutputPort stepOutput: step.getOutputs()) {
                OutputProcessorPort processorPort = new OutputProcessorPort(processor, stepOutput.getName());
                processorOutputs.put(stepOutput.getName(), processorPort);
            }
        }
    }

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
}