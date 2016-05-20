package org.apache.taverna.scufl2.cwl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import org.apache.taverna.scufl2.api.common.Named;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WorkflowBundleReader;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.cwl.workflow.CWLInputParameter;
import org.apache.taverna.scufl2.cwl.workflow.CWLWorkflow;
import org.apache.taverna.scufl2.cwl.workflow.CWLWorkflowOutputParameter;
import org.apache.taverna.scufl2.cwl.workflow.CWLWorkflowStep;
import org.apache.taverna.scufl2.cwl.workflow.CWLWorkflowStepInput;
import org.apache.taverna.scufl2.cwl.workflow.CWLWorkflowStepOutput;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class CwlReader implements WorkflowBundleReader  {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final WorkflowBundleIO WF_IO = new WorkflowBundleIO();
	public static final String MEDIA_TYPE = "text/vnd.commonwf.workflow+yaml";

	@Override
	public Set<String> getMediaTypes() {
		return Collections.singleton(MEDIA_TYPE);
		
	}

	@Override
	public WorkflowBundle readBundle(File bundleFile, String mediaType) throws ReaderException, IOException {
		if (! MEDIA_TYPE.equals(mediaType)) { 
			throw new ReaderException("Unsupport mediaType " + mediaType);
		}
		try (FileInputStream in = new FileInputStream(bundleFile)) {
			return readBundle(in, bundleFile.toURI());			
		}
	}

	@Override
	public WorkflowBundle readBundle(InputStream inputStream, String mediaType) throws ReaderException, IOException {
		if (! MEDIA_TYPE.equals(mediaType)) { 
			throw new ReaderException("Unsupport mediaType " + mediaType);
		}
		return readBundle(inputStream, URI.create("urn:uuid:" + UUID.randomUUID()));
	}

	private WorkflowBundle readBundle(InputStream inputStream, URI base) throws JsonParseException, JsonMappingException, IOException {
		
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		CWLWorkflow cwlWf = mapper.readValue(inputStream, CWLWorkflow.class);		
		WorkflowBundle wfb = WF_IO.createBundle();
		
		parseWorkflow(cwlWf, wfb.getMainWorkflow());
		
		return wfb;
	}

	private void parseWorkflow(CWLWorkflow src, Workflow dest) {
		if (src.id != null) {
			// TODO: Make id absolute
			dest.setIdentifier(URI.create(src.id));
			// TODO: Generate local name from ID, if possible
			//dest.setName(src.id);
		}		
		
		// TODO: Check compatibility and store as annotations
		//src.cwlVersion;
		//src.requirements;
		//src.klass;
		
		// TODO: Store as annotations
		//src.label;
		//src.description;				
		//src.hints;

		
		if (src.inputs != null) {
			for (CWLInputParameter input : src.inputs) {
				InputWorkflowPort p = new InputWorkflowPort();
				p.setParent(dest);
				parseInputPort(input, p);
				
			}
		}
		if (src.outputs != null) { 
			for (CWLWorkflowOutputParameter out : src.outputs) {
				OutputWorkflowPort p = new OutputWorkflowPort();
				p.setParent(dest);
				parseOutputPort(out, p);
				
			}
		}
		
		if (src.steps != null) {
			for (CWLWorkflowStep step : src.steps) {
				Processor p = new Processor();
				p.setParent(dest);
				parseWorkflowStep(step, p);					

			}
		}
		
	}

	private void parseWorkflowStep(CWLWorkflowStep step, Processor p) {
		if (step.id != null && ! Named.INVALID_NAME.matcher(step.id).matches()) {
			p.setName(step.id);
		}		
		// TODO step.run
		
		if (step.in != null) { 
			for (CWLWorkflowStepInput s : step.in) {
				InputProcessorPort inp = new InputProcessorPort();
				inp.setParent(p);
				parseWorkflowStepInput(s, inp);
			}
		}
		if (step.out != null) { 
			for (JsonNode s : step.out) {
				OutputProcessorPort outp = new OutputProcessorPort();
				outp.setParent(p);
				parseWorkflowStepOutput(s, outp);
			}
		}
		
		// TODO: Check compatibility
		//step.requirements
		
		
		// TODO: Annotations
		//step.label;
		//step.description;
		//step.hints;

		// TODO: Iteration strategy	
		//step.scatter;
		
		// TODO: Handle run
		// TODO: Handle nested workflows
		//step.run;
	}

	private void parseWorkflowStepOutput(JsonNode s, OutputProcessorPort outp) {
		if (s.isTextual() && ! Named.INVALID_NAME.matcher(s.asText()).matches()) { 
			outp.setName(s.asText());
		}
		CWLWorkflowStepOutput out = OBJECT_MAPPER.convertValue(s, CWLWorkflowStepOutput.class);
		if (out.id != null && ! Named.INVALID_NAME.matcher(out.id).matches()) {
			outp.setName(out.id);
		}
		
	}

	private void parseWorkflowStepInput(CWLWorkflowStepInput s, InputProcessorPort inp) {
		// TODO Auto-generated method stub
		
	}

	private void parseOutputPort(CWLWorkflowOutputParameter out, OutputWorkflowPort p) {
		if (out.id != null && ! Named.INVALID_NAME.matcher(out.id).matches()) {
			p.setName(out.id);
		}
				
	}

	private void parseInputPort(CWLInputParameter input, InputWorkflowPort p) {
		if (input.id != null && ! Named.INVALID_NAME.matcher(input.id).matches()) {
			p.setName(input.id);
		}
		// TODO: Depth from input.type Array
		// TODO: if (input.defaultValue
		//input.
		
		
	}

	@Override
	public String guessMediaTypeForSignature(byte[] firstBytes) {
		String s = new String(firstBytes, StandardCharsets.ISO_8859_1);
		if (s.contains("??")) {
			return MEDIA_TYPE;
		}
		return null;
	}

	public WorkflowBundle readBundle(URL revsort) throws JsonParseException, JsonMappingException, IOException, URISyntaxException {
		try (InputStream in = revsort.openStream()) {
			return readBundle(in, revsort.toURI());
		}
	}

}
