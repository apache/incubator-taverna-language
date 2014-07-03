package org.purl.wf4ever.robundle.manifest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;
import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.Bundles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestManifestJSON {
	@Test
	public void createBundle() throws Exception {
		// Create bundle as in Example 3 of the specification
		// http://wf4ever.github.io/ro/bundle/2013-05-21/
		try (Bundle bundle = Bundles.createBundle()) {
			Calendar createdOnCal = Calendar.getInstance(TimeZone.getTimeZone("Z"), Locale.ENGLISH);
			// "2013-03-05T17:29:03Z"
			// Remember months are 0-based in java.util.Calendar!
			createdOnCal.set(2013, 3-1, 5, 17, 29, 03);			
			createdOnCal.set(Calendar.MILLISECOND, 0);
			FileTime createdOn = FileTime.fromMillis(createdOnCal.getTimeInMillis());			
			Manifest manifest = bundle.getManifest();
			manifest.setCreatedOn(createdOn);
			Agent createdBy = new Agent("Alice W. Land");
			createdBy.setUri(URI.create("http://example.com/foaf#alice"));
			createdBy.setOrcid(URI.create("http://orcid.org/0000-0002-1825-0097"));
			
			manifest.setCreatedBy(createdBy);
			
			Path evolutionPath = bundle.getPath(".ro/evolution.ttl");
			Files.createDirectories(evolutionPath.getParent());
			Bundles.setStringValue(evolutionPath, "<manifest.json> < http://purl.org/pav/retrievedFrom> " +
					"<http://wf4ever.github.io/ro/bundle/2013-05-21/example/.ro/manifest.json> .");
			manifest.getHistory().add(evolutionPath);
			
			
			Path jpeg = bundle.getPath("folder/soup.jpeg");
			Files.createDirectory(jpeg.getParent());
			Files.createFile(jpeg);
			// register in manifest first
			bundle.getManifest().getAggregation(jpeg);

			URI blog = URI.create("http://example.com/blog/");
			bundle.getManifest().getAggregation(blog);

			Path readme = bundle.getPath("README.txt");
			Files.createFile(readme);
			PathMetadata readmeMeta = bundle.getManifest().getAggregation(readme);
			readmeMeta.setMediatype("text/plain");
			Agent readmeCreatedby = new Agent("Bob Builder");
			readmeCreatedby.setUri(URI.create("http://example.com/foaf#bob"));
			readmeMeta.setCreatedBy(readmeCreatedby);
			
			// 2013-02-12T19:37:32.939Z
			createdOnCal.set(2013, 2-1, 12, 19, 37, 32);
			createdOnCal.set(Calendar.MILLISECOND, 939);
			createdOn = FileTime.fromMillis(createdOnCal.getTimeInMillis());
			Files.setLastModifiedTime(readme, createdOn);
			readmeMeta.setCreatedOn(createdOn);

			PathMetadata comments = bundle.getManifest().getAggregation(URI.create("http://example.com/comments.txt"));
			comments.getOrCreateBundledAs().setProxy(URI.create("urn:uuid:a0cf8616-bee4-4a71-b21e-c60e6499a644"));
			comments.getOrCreateBundledAs().setFolder(bundle.getPath("/folder/"));
			comments.getOrCreateBundledAs().setFilename("external.txt");
			
            PathAnnotation jpegAnn = new PathAnnotation();
            jpegAnn.setAbout(jpeg);
            Path soupProps = Bundles.getAnnotations(bundle).resolve("soup-properties.ttl");
            Bundles.setStringValue(soupProps, "</folder/soup.jpeg> <http://xmlns.com/foaf/0.1/depicts> " +
            		"<http://example.com/menu/tomato-soup> .");
            jpegAnn.setContent(soupProps);
//            jpegAnn.setContent(URI.create("annotations/soup-properties.ttl"));
            jpegAnn.setAnnotation(URI.create("urn:uuid:d67466b4-3aeb-4855-8203-90febe71abdf"));
            manifest.getAnnotations().add(jpegAnn);
			
			
            PathAnnotation proxyAnn = new PathAnnotation();
            proxyAnn.setAbout(comments.getBundledAs().getProxy());
			proxyAnn.setContent(URI.create("http://example.com/blog/they-aggregated-our-file"));
            manifest.getAnnotations().add(proxyAnn);
            
            Path metaAnn = Bundles.getAnnotations(bundle).resolve("a-meta-annotation-in-this-ro.txt");
            Bundles.setStringValue(metaAnn, "This bundle contains an annotation about /folder/soup.jpeg");
            
            PathAnnotation metaAnnotation = new PathAnnotation();
            metaAnnotation.setAbout(bundle.getRoot());
            metaAnnotation.getAboutList().add(URI.create("urn:uuid:d67466b4-3aeb-4855-8203-90febe71abdf"));
            
            metaAnnotation.setContent(metaAnn);
			manifest.getAnnotations().add(metaAnnotation);
            
			Path jsonPath = bundle.getManifest().writeAsJsonLD();
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonStr = Bundles.getStringValue(jsonPath);
			System.out.println(jsonStr);
			JsonNode json = objectMapper.readTree(jsonStr);
			
			checkManifestJson(json);
				
			
		}
	}
	
	public void checkManifestJson(JsonNode json) {
		JsonNode context = json.get("@context");
		assertNotNull("Could not find @context", context);
		assertTrue("@context SHOULD be an array", context.isArray());
		assertTrue("@context SHOULD include a context", context.size() > 0);
		JsonNode lastContext = context.get(context.size() -1);
		assertEquals("@context SHOULD include https://w3id.org/bundle/context as last item", 
				"https://w3id.org/bundle/context",
				lastContext.asText());
		
		assertEquals("/", json.get("id").asText());
		
		JsonNode manifest = json.get("manifest");
		if (manifest.isValueNode()) {
			assertEquals("manifest SHOULD be literal value \"manifest.json\" or list", 
					"manifest.json", manifest.asText());
		} else {
			assertTrue("manifest is neither literal or list", manifest.isArray());
			boolean found = false;
			for (JsonNode n : manifest) {
				found = n.asText().equals("manifest.json");
				if (found) {
					break;
				}
			};
			assertTrue("Could not find 'manifest.json' in 'manifest' list: " + manifest, found);
		}
		
		assertEquals("2013-03-05T17:29:03Z", json.get("createdOn").asText());
		JsonNode createdBy = json.get("createdBy");
		assertNotNull("Could not find createdBy", createdBy);
		assertEquals("http://example.com/foaf#alice", createdBy.get("uri").asText());
		assertEquals("http://orcid.org/0000-0002-1825-0097", createdBy.get("orcid").asText());
		assertEquals("Alice W. Land", createdBy.get("name").asText());

		JsonNode history = json.get("history");
		if (history.isValueNode()) {
			assertEquals("evolution.ttl", history.asText());
		} else {
			assertEquals("evolution.ttl", history.get(0).asText());
		}
		
		JsonNode aggregates = json.get("aggregates");
		assertTrue("aggregates not a list", aggregates.isArray());
		JsonNode soup = aggregates.get(0);
		if (soup.isValueNode()) { 
			assertEquals("/folder/soup.jpeg", soup.asText());
		} else {
			assertEquals("/folder/soup.jpeg", soup.get("file").asText());
		}
		
		JsonNode blog = aggregates.get(1);
		if (blog.isValueNode()) {
			assertEquals("http://example.com/blog/", blog.asText());
		} else { 
			assertEquals("http://example.com/blog/", blog.get("uri").asText());
		}
		
		JsonNode readme = aggregates.get(2);
		assertEquals("/README.txt", readme.get("file").asText());
		assertEquals("text/plain", readme.get("mediatype").asText());
		assertEquals("2013-02-12T19:37:32.939Z", readme.get("createdOn").asText());
		JsonNode readmeCreatedBy = readme.get("createdBy");
		assertEquals("http://example.com/foaf#bob", readmeCreatedBy.get("uri").asText());
		assertEquals("Bob Builder", readmeCreatedBy.get("name").asText());
		
		JsonNode comments = aggregates.get(3);
		assertEquals("http://example.com/comments.txt", comments.get("uri").asText());
		JsonNode bundledAs = comments.get("bundledAs");
		assertEquals("urn:uuid:a0cf8616-bee4-4a71-b21e-c60e6499a644", bundledAs.get("proxy").asText());
		assertEquals("/folder/", bundledAs.get("folder").asText());
		assertEquals("external.txt", bundledAs.get("filename").asText());
		
		JsonNode annotations = json.get("annotations");
		assertTrue("annotations MUST be a list", annotations.isArray());
		
		JsonNode ann0 = annotations.get(0);
		assertEquals("urn:uuid:d67466b4-3aeb-4855-8203-90febe71abdf", ann0.get("annotation").asText());
		assertEquals("/folder/soup.jpeg", ann0.get("about").asText());
		assertEquals("annotations/soup-properties.ttl", ann0.get("content").asText());
		
		JsonNode ann1 = annotations.get(1);
		assertNull(ann1.get("annotation"));
		assertEquals("urn:uuid:a0cf8616-bee4-4a71-b21e-c60e6499a644", ann1.get("about").asText());
		assertEquals("http://example.com/blog/they-aggregated-our-file", ann1.get("content").asText());

		JsonNode ann2 = annotations.get(2);
		assertNull(ann2.get("annotation"));
		JsonNode about = ann2.get("about");
		assertTrue("about was not a list", about.isArray());
		assertEquals("/", about.get(0).asText());
		assertEquals("urn:uuid:d67466b4-3aeb-4855-8203-90febe71abdf", about.get(1).asText());
		assertEquals("annotations/a-meta-annotation-in-this-ro.txt", ann2.get("content").asText());

		
	}

	@Test
	public void checkJsonFromSpec() throws Exception {
		// Verify that our test confirms the existing spec example
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode json = objectMapper.readTree(getClass().getResource("/manifest.json"));
		checkManifestJson(json);
		
		
	}
}
