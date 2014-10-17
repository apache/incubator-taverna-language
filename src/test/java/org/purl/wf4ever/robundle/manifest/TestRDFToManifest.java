package org.purl.wf4ever.robundle.manifest;

import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.Map;

import org.junit.Test;

import com.github.jsonldjava.core.DocumentLoader;

//import com.github.jsonldjava.core.DocumentLoader;

public class TestRDFToManifest {
	private static final String CONTEXT = "https://w3id.org/bundle/context";

	@Test
	public void contextLoadedFromJarCache() throws Exception {
		// RDFToManifest.makeBaseURI(); // trigger static{} block
		Map<String, Object> context = (Map<String, Object>) new DocumentLoader()
				.fromURL(new URL(CONTEXT));
		// FIXME: jsonld-java 0.3 and later uses DocumentLoader instead of
		// JSONUtils
		// Map<String, Object> context = (Map<String, Object>)
		// JSONUtils.fromURL(new URL(CONTEXT));
		Object retrievedFrom = context.get("http://purl.org/pav/retrievedFrom");
		assertNotNull("Did not load context from cache: " + CONTEXT,
				retrievedFrom);

	}
}
