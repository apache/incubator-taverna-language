package org.apache.taverna.scufl2.cwl;


import java.util.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.DataLink;

import org.apache.taverna.scufl2.api.common.NamedSet;

import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;


public class TestParser {
    private static final String HELLO_WORLD_CWL = "/hello_world.cwl";

    private static JsonNode cwlFile;
    private WorkflowParser parser;
    private Workflow workflow;

    @Before
    public void initialize() {

        Yaml reader = new Yaml();
        ObjectMapper mapper = new ObjectMapper();
        cwlFile = mapper.valueToTree(reader.load(TestParser.class.getResourceAsStream(HELLO_WORLD_CWL)));
        System.out.println(cwlFile);
        this.parser = new WorkflowParser(cwlFile);

        this.workflow = parser.buildWorkflow();
    }

    @Test
    public void testParseInputs() throws Exception {

        NamedSet<InputWorkflowPort> workflowInputs = workflow.getInputPorts();
        NamedSet<InputWorkflowPort> expectedInputs = new NamedSet<>();
        expectedInputs.add(new InputWorkflowPort(workflow, "name"));

        assertEquals(expectedInputs, workflowInputs);
    }

    @Test
    public void testParseOutputs() throws Exception {

        NamedSet<OutputWorkflowPort> workflowOutputs = workflow.getOutputPorts();
        NamedSet<OutputWorkflowPort> expectedOutputs = new NamedSet<>();

        assertEquals(expectedOutputs, workflowOutputs);
    }

    @Test
    public void testParseProcessors() throws Exception {

        NamedSet<Processor> workflowProcessors = workflow.getProcessors();
        NamedSet<Processor> expectedProcessors = new NamedSet<>();
        expectedProcessors.add(new Processor(workflow, "step1"));

        assertEquals(expectedProcessors, workflowProcessors);
    }

    @Test
    public void testParseDataLinks() throws Exception {

        Set<DataLink> workflowDataLinks = workflow.getDataLinks();
        Set<DataLink> expectedDataLinks = new HashSet<>();
        Set<Processor> processorSet = workflow.getProcessors();
        // processorSet has one processor
        Processor processor = processorSet.iterator().next();

        expectedDataLinks.add(
                new DataLink(
                        workflow,
                        new InputWorkflowPort(workflow, "name"),
                        new InputProcessorPort(processor, "text")
                )
        );

        assertEquals(expectedDataLinks, workflowDataLinks);
    }
}
