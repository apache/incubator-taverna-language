package org.apache.taverna.scufl2.cwl;


import java.util.*;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;


public class TestParser {
    private static final String SIMPLE_STRING_INPUT = "/simple_string_input.cwl";
    private static final String INT_INPUT = "/int_input.cwl";

    private static JsonNode cwlFile;

    @Test
    public void testStringInput() throws Exception {

        Yaml reader = new Yaml();
        ObjectMapper mapper = new ObjectMapper();
        cwlFile = mapper.valueToTree(reader.load(TestParser.class.getResourceAsStream(SIMPLE_STRING_INPUT)));
        System.out.println(cwlFile);
        Parser parser = new Parser(cwlFile);

        Set<InputWorkflowPort> result = parser.parseInputs();
        for(InputWorkflowPort port: result) {
            System.out.println(port.getName());
        }
        ArrayList<InputWorkflowPort> inputs = new ArrayList<>(result);
        assertEquals(inputs.get(0).getName(), "example_string");

        Set<OutputWorkflowPort> result2 = parser.parseOutputs();
        for(OutputWorkflowPort port: result2) {
            System.out.println(port.getName());
        }
        System.out.println("Showing steps:");

        Set<Step> steps = parser.parseSteps();
        for(Step step: steps) {
            System.out.println(step);
        }
    }
}
