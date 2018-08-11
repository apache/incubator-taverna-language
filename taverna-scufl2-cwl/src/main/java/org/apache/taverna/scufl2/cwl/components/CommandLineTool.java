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
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;


import org.apache.taverna.scufl2.cwl.*;

public class CommandLineTool extends Process {

    private final static String BASE_COMMAND = "baseCommand";
    private final static String ID = "id";
    private final static String INPUT_BINDINDGS = "inputBinding";

    private CWLParser cwlParser;

    private JsonNode node;

    private String baseCommand = null;
    private Map<String, InputProcessorPort> processorInputs = new HashMap<>();
    private Map<String, OutputProcessorPort> processorOutputs = new HashMap<>();

    public CommandLineTool(JsonNode node) {
        this.name = "";
        this.node = node;
        this.cwlParser = new CWLParser(node);
        this.parse();
    }

    public void parse() {
        baseCommand = node.get(BASE_COMMAND).asText();
        parseInputs();
        parseOutputs();
    }

    public void parseInputs() {
        // TODO: Set the processor port depth from the CWL type
        Set<PortDetail> cwlInputs = cwlParser.parseInputs();
        for(PortDetail detail: cwlInputs) {
            String portId = detail.getId();
            InputProcessorPort port = new InputProcessorPort();
            port.setName(portId);
            processorInputs.put(portId, port);
        }
    }

    public void parseOutputs() {
        Set<PortDetail> cwlOutputs = cwlParser.parseOutputs();
        for(PortDetail detail: cwlOutputs) {
            String portId = detail.getId();
            OutputProcessorPort port = new OutputProcessorPort();
            port.setName(portId);
            processorOutputs.put(portId, port);
        }
    }

    public String getBaseCommand() {
        return baseCommand;
    }
}