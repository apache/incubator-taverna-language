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

import com.fasterxml.jackson.databind.JsonNode;

public class ProcessFactory {

    private final static String RUN = "run";
    private final static String CLASS = "class";

    public static Process createProcess(JsonNode node) {

        Process process = null;

        if(node.isValueNode()) {
            return new Reference(node.asText());
        }

        JsonNode className = node.get(CLASS);
        if(className != null) {
            if(className.asText().equals("Workflow")) {
                process = new WorkflowProcess(node);
            } else if(className.asText().equals("CommandLineTool")) {
                process = new CommandLineTool(node);
            }
        } else {
            JsonNode runNode = node.get(RUN);
            if(runNode.isValueNode()) {
                process = new Reference(runNode.asText());
            } else {
                String runClass = runNode.get(CLASS).asText();
                switch(runClass) {
                    case "CommandLineTool":
                        process = new CommandLineTool(runNode);
                        break;
                    case "Workflow":
                        process = new WorkflowProcess(runNode);
                        break;
                }
            }
        }


        return process;
    }

}