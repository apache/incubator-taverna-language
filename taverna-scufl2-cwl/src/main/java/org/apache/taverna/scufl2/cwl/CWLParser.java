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

import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.taverna.scufl2.cwl.components.Step;
import org.apache.taverna.scufl2.cwl.components.PortDetail;

public class CWLParser {

    private JsonNode cwlFile;
    private YAMLHelper yamlHelper = new YAMLHelper();

    public CWLParser(JsonNode cwlFile) {
        this.cwlFile = cwlFile;
    }

    public Set<Step> parseSteps() {
        return yamlHelper.processSteps(cwlFile);
    }

    public Set<PortDetail> parseInputs() {
        Map<String, PortDetail> inputs = yamlHelper.processInputDetails(cwlFile);
        Map<String, Integer> inputDepths = yamlHelper.processInputDepths(cwlFile);

        if(inputs == null || inputDepths == null) {
            return new HashSet<PortDetail>();
        }
        Set<PortDetail> result = new HashSet<PortDetail>();
        for(String id: inputs.keySet()) {
            PortDetail port = inputs.get(id);
            port.setId(id);
            int depth = inputDepths.get(id);
            port.setDepth(depth);
            result.add(port);
        }

        return result;
    }

    public Set<PortDetail> parseOutputs() {
        Map<String, PortDetail> outputs = yamlHelper.processOutputDetails(cwlFile);

        if(outputs == null) {
            return new HashSet<PortDetail>();
        }
        Set<PortDetail> result = new HashSet<PortDetail>();
        for(String id: outputs.keySet()) {
            PortDetail port = outputs.get(id);
            port.setId(id);
            result.add(port);
        }

        return result;
    }

}
