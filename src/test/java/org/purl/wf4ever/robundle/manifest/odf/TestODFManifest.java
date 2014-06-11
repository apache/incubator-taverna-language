package org.purl.wf4ever.robundle.manifest.odf;

import static org.junit.Assert.*;

import java.net.URL;
import java.nio.file.Path;

import org.junit.Test;
import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.Bundles;
import org.purl.wf4ever.robundle.manifest.Manifest;
import org.purl.wf4ever.robundle.manifest.PathMetadata;

public class TestODFManifest {
	@Test
	public void openHelloWorld() throws Exception {
		URL helloworld = getClass().getResource("/helloworld.wfbundle");
		assertNotNull(helloworld);
		try (Bundle bundle = Bundles.openBundle(helloworld)) {
			assertEquals("application/vnd.taverna.scufl2.workflow-bundle",
					Bundles.getMimeType(bundle));
			Path t2flow = bundle
					.getPath("history/8781d5f4-d0ba-48a8-a1d1-14281bd8a917.t2flow");
			assertEquals("application/vnd.taverna.t2flow+xml", bundle
					.getManifest().getAggregation(t2flow).getMediatype());
		}
	}

	@Test
	public void openODTDocument() throws Exception {
		URL url = getClass().getResource("/document.odt");
		assertNotNull(url);
		try (Bundle bundle = Bundles.openBundle(url)) {
			assertEquals("application/vnd.oasis.opendocument.text",
					Bundles.getMimeType(bundle));

			Path contentXml = bundle.getPath("content.xml");
			Manifest manifest = bundle.getManifest();
			assertEquals("text/xml", manifest.getAggregation(contentXml)
					.getMediatype());
			PathMetadata rootMeta = manifest.getAggregation(bundle.getRoot());
			assertEquals("1.2", rootMeta.getConformsTo() + "");
			assertEquals("application/vnd.oasis.opendocument.text",
					rootMeta.getMediatype());
		}
	}

}
