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

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WorkflowBundleReader;
import org.apache.taverna.scufl2.cwl.workflow.CWLWorkflow;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class CwlReader implements WorkflowBundleReader  {

	private static final WorkflowBundleIO WF_IO = new WorkflowBundleIO();
	private static final String MEDIA_TYPE = "text/x-common-workflow-language+yaml";

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
		CWLWorkflow user = mapper.readValue(inputStream, CWLWorkflow.class);
		System.out.println(user.id);
		
		WorkflowBundle wfb = WF_IO.createBundle();
		Workflow wf = wfb.getMainWorkflow();
		wf.setName("Hello");
		
		return wfb;
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
