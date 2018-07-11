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

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Main {

    private static final String HELLO_WORLD_CWL = "/hello_world.cwl";
    private static final String WORKFLOW_WITH_COMMAND = "/workflow_with_command.cwl";
    private static JsonNode cwlFile;

    public static void main(String[] args) {

        Yaml reader = new Yaml();
        ObjectMapper mapper = new ObjectMapper();
        cwlFile = mapper.valueToTree(reader.load(Main.class.getResourceAsStream(WORKFLOW_WITH_COMMAND)));
        System.out.println(cwlFile);

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
}