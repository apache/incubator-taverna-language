package org.apache.taverna.scufl2.ucfpackage;
/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.taverna.scufl2.ucfpackage.UCFPackage;
import org.apache.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("resource")
public class TestUCFPackage {

	private static final int MIME_OFFSET = 30;
	private static final boolean DELETE_FILES = false;
	private static final Namespace MANIFEST_NS = Namespace.getNamespace("manifest", 
			"urn:oasis:names:tc:opendocument:xmlns:manifest:1.0");
	private static final Namespace EXAMPLE_NS = Namespace.getNamespace("ex", "http://example.com/");
	private static final Namespace CONTAINER_NS = Namespace.getNamespace("c", "urn:oasis:names:tc:opendocument:xmlns:container");
	private File tmpFile;
	

	protected Document parseXml(InputStream stream)
			throws JDOMException, IOException {
		SAXBuilder saxBuilder = new SAXBuilder();
		return saxBuilder.build(stream);
	}

	protected void assertXpathEquals(String expected, Element element,
			String xpath) throws JDOMException {
		Object o = xpathSelectElement(element, xpath);
		if (o == null) {
			fail("Can't find " + xpath  + " in " + element);
			return;
		}
		String text;
		if (o instanceof Attribute) {
			text = ((Attribute)o).getValue();
		} else {
			text = ((Element)o).getValue();
		}
		assertEquals(expected, text);		
	}


	protected Object xpathSelectElement(Element element, String xpath) throws JDOMException {
		XPath x = XPath.newInstance(xpath);
		x.addNamespace(MANIFEST_NS);
		x.addNamespace(CONTAINER_NS);
		x.addNamespace(EXAMPLE_NS);

		return x.selectSingleNode(element);
	}

	@Test(expected = IllegalArgumentException.class)
	public void mimeTypeInvalidCharset() throws Exception {
		UCFPackage container = new UCFPackage();
		// Not even Latin-1 chars allowed
		container.setPackageMediaType("food/brød");
	}

	@Test(expected = IllegalArgumentException.class)
	public void mimeTypeEmpty() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void mimeTypeNoSlash() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType("nonvalid");
	}

	@Test
	public void defaultMimeType() throws Exception {
		UCFPackage container = new UCFPackage();
		container.save(tmpFile);
		assertEquals("application/vnd.wf4ever.robundle+zip", 
				container.getPackageMediaType());
	}

	@Before
	public void createTempFile() throws IOException {
		tmpFile = File.createTempFile("scufl2-test", ".bundle");
		assertTrue(tmpFile.delete());
		if (DELETE_FILES) {
			tmpFile.deleteOnExit();
		} else {
			System.out.println(tmpFile);
		}
	}

	@Test
	public void workflowBundleMimeType() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		assertEquals(UCFPackage.MIME_WORKFLOW_BUNDLE,
				container.getPackageMediaType());
		container.save(tmpFile);
		ZipFile zipFile = new ZipFile(tmpFile);
		ZipEntry mimeEntry = zipFile.getEntry("mimetype");
		assertEquals("mimetype", mimeEntry.getName());
		assertEquals("Wrong mimetype", UCFPackage.MIME_WORKFLOW_BUNDLE,
				IOUtils.toString(zipFile.getInputStream(mimeEntry), "ASCII"));

	}

	@Test
	public void fileEntryFromString() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);

		container
				.addResource("Hello there þĸł", "helloworld.txt", "text/plain");

		container.save(tmpFile);
		ZipFile zipFile = new ZipFile(tmpFile);
		ZipEntry manifestEntry = zipFile.getEntry("META-INF/manifest.xml");
		assertNotNull("could not find manifest.xml", manifestEntry);
		InputStream manifestStream = zipFile.getInputStream(manifestEntry);
		//System.out.println(IOUtils.toString(manifestStream, "UTF-8"));
		/*
<?xml version="1.0" encoding="UTF-8"?>
<manifest:manifest xmlns:manifest="urn:oasis:names:tc:opendocument:xmlns:manifest:1.0">
 <manifest:file-entry manifest:media-type="text/plain" manifest:full-path="helloworld.txt" manifest:size="18"/>
</manifest:manifest>
		 */
		Document doc = parseXml(manifestStream);
		assertEquals(MANIFEST_NS, doc.getRootElement().getNamespace());
		assertEquals("manifest", doc.getRootElement().getNamespacePrefix());
		assertEquals("manifest:manifest", doc.getRootElement().getQualifiedName());
		assertXpathEquals("text/plain", doc.getRootElement(), "/manifest:manifest/manifest:file-entry/@manifest:media-type");
		assertXpathEquals("helloworld.txt", doc.getRootElement(), "/manifest:manifest/manifest:file-entry/@manifest:full-path");
/*
 * Different platforms encode UTF8 in different ways
 * 		assertXpathEquals("18", doc.getRootElement(), "/manifest:manifest/manifest:file-entry/@manifest:size");
 */
		
		InputStream io = zipFile.getInputStream(zipFile
				.getEntry("helloworld.txt"));
		assertEquals("Hello there þĸł", IOUtils.toString(io, "UTF-8"));
	}

	@Test
	public void retrieveStringLoadedFromFile() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		container
				.addResource("Hello there þĸł", "helloworld.txt", "text/plain");
		container.save(tmpFile);

		UCFPackage loaded = new UCFPackage(tmpFile);
		String s = loaded.getResourceAsString("helloworld.txt");
		assertEquals("Hello there þĸł", s);
	}
	
	@Test
	public void doubleSave() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		container
				.addResource("Hello there þĸł", "helloworld.txt", "text/plain");
		container.save(tmpFile);
		container.addResource("Hello again", "again.txt", "text/plain");
		container.save(tmpFile);
		ZipFile zipFile = new ZipFile(tmpFile);
		assertNotNull(zipFile.getEntry("helloworld.txt"));

		assertNotNull(zipFile.getEntry("again.txt"));
	}

	@Test
	public void doubleSaveOutputStream() throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		container
				.addResource("Hello there þĸł", "helloworld.txt", "text/plain");
		container.save(outStream);
		container.addResource("Hello again", "again.txt", "text/plain");
		outStream.close();

		outStream = new ByteArrayOutputStream();
		container.save(outStream);
		outStream.close();

		FileUtils.writeByteArrayToFile(tmpFile, outStream.toByteArray());
		ZipFile zipFile = new ZipFile(tmpFile);
		assertNotNull(zipFile.getEntry("helloworld.txt"));
		assertNotNull(zipFile.getEntry("again.txt"));
	}

	@Test
	public void retrieveBytesLoadedFromFile() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		byte[] bytes = makeBytes(2048);
		container.addResource(bytes, "randomBytes", "application/octet-stream");
		container.save(tmpFile);

		UCFPackage loaded = new UCFPackage(tmpFile);
		byte[] loadedBytes = loaded.getResourceAsBytes("randomBytes");
		assertArrayEquals(bytes, loadedBytes);
	}
	
	@Test
	public void addResourceOutputStream() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		byte[] bytes = makeBytes(2048);
		
		OutputStream outStream = container.addResourceUsingOutputStream("randomBytes", "application/octet-stream");
		IOUtils.write(bytes, outStream);

		assertFalse(container.listResources().isEmpty());
		
		outStream.close();		
		assertFalse(container.listResources().isEmpty());
		
		container.save(tmpFile);
		UCFPackage loaded = new UCFPackage(tmpFile);
		byte[] loadedBytes = loaded.getResourceAsBytes("randomBytes");
		assertArrayEquals(bytes, loadedBytes);
	}

	@Test
	public void retrieveInputStreamLoadedFromFile() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		byte[] bytes = makeBytes(4929);
		container.addResource(bytes, "randomBytes", "application/octet-stream");
		container.save(tmpFile);

		UCFPackage loaded = new UCFPackage(tmpFile);
		InputStream entryAsInputStream = loaded
				.getResourceAsInputStream("randomBytes");
		byte[] loadedBytes = IOUtils.toByteArray(entryAsInputStream);
		assertArrayEquals(bytes, loadedBytes);
	}

	@Test
	public void fileEntryFromBytes() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);

		byte[] bytes = makeBytes(1024);
		container.addResource(bytes, "binary", UCFPackage.MIME_BINARY);

		container.save(tmpFile);
		ZipFile zipFile = new ZipFile(tmpFile);
		ZipEntry manifestEntry = zipFile.getEntry("META-INF/manifest.xml");
		InputStream manifestStream = zipFile.getInputStream(manifestEntry);

		//System.out.println(IOUtils.toString(manifestStream, "UTF-8"));
		/*
<?xml version="1.0" encoding="UTF-8"?>
<manifest:manifest xmlns:manifest="urn:oasis:names:tc:opendocument:xmlns:manifest:1.0">
 <manifest:file-entry manifest:media-type="application/octet-stream" manifest:full-path="binary" manifest:size="1024"/>
</manifest:manifest>
		 */
		Document doc = parseXml(manifestStream);
		assertEquals(MANIFEST_NS, doc.getRootElement().getNamespace());
		assertEquals("manifest", doc.getRootElement().getNamespacePrefix());
		assertEquals("manifest:manifest", doc.getRootElement().getQualifiedName());
		assertXpathEquals("application/octet-stream", doc.getRootElement(), "/manifest:manifest/manifest:file-entry/@manifest:media-type");
		assertXpathEquals("binary", doc.getRootElement(), "/manifest:manifest/manifest:file-entry/@manifest:full-path");
		assertXpathEquals("1024", doc.getRootElement(), "/manifest:manifest/manifest:file-entry/@manifest:size");

		InputStream io = zipFile.getInputStream(zipFile.getEntry("binary"));
		assertArrayEquals(bytes, IOUtils.toByteArray(io));
	}

	protected static byte[] makeBytes(int size) {
		byte[] bytes = new byte[size];
		for (int i = 0; i < size; i++) {
			bytes[i] = (byte) i;
		}
		return bytes;
	}

	@Test
	public void fileListing() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		Set<String> expectedFiles = new HashSet<String>();
		Set<String> expectedSubFiles = new HashSet<String>();
		Set<String> expectedSubSubFiles = new HashSet<String>();

		container.addResource("Hello there", "helloworld.txt", "text/plain");
		expectedFiles.add("helloworld.txt");

		container.addResource("Soup for everyone", "soup.txt", "text/plain");
		expectedFiles.add("soup.txt");

		container.addResource("<html><body><h1>Yo</h1></body></html>",
				"soup.html", "text/html");
		expectedFiles.add("soup.html");

		container.addResource("Sub-folder entry 1", "sub/1.txt", "text/plain");
		container.addResource("Sub-folder entry 2", "sub/2.txt", "text/plain");
		container.addResource("Sub-folder entry 2", "sub/3/woho.txt",
				"text/plain");

		container.addResource("Other sub-folder entry", "sub2/3.txt",
				"text/plain");

		expectedFiles.add("sub/");
		expectedSubFiles.add("1.txt");
		expectedSubFiles.add("2.txt");
		expectedSubFiles.add("3/");
		expectedSubSubFiles.add("woho.txt");
		expectedFiles.add("sub2/");

		Map<String, ResourceEntry> beforeSaveRootEntries = container
				.listResources();
		assertEquals(expectedFiles, beforeSaveRootEntries.keySet());

		assertEquals(expectedSubFiles, container.listResources("sub").keySet());
		assertEquals(expectedSubFiles, container.listResources("sub/").keySet());
		assertEquals(expectedSubSubFiles, container.listResources("sub/3/")
				.keySet());

		container.save(tmpFile);

		UCFPackage loaded = new UCFPackage(tmpFile);
		Map<String, ResourceEntry> loadedRootEntries = loaded.listResources();
		assertEquals(expectedFiles, loadedRootEntries.keySet());

		assertEquals(expectedSubFiles, loaded.listResources("sub").keySet());
		assertEquals(expectedSubFiles, loaded.listResources("sub/").keySet());
		assertEquals(expectedSubSubFiles, loaded.listResources("sub/3/")
				.keySet());
	}

	@Test
	public void removeResource() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);

		container.addResource("Soup for everyone", "soup.txt", "text/plain");

		container.addResource("Sub-folder entry 1", "sub/1.txt", "text/plain");
		container.addResource("Sub-folder entry 2", "sub/2.txt", "text/plain");
		container.addResource("Sub-folder entry 2", "sub/3/woho.txt",
				"text/plain");

		assertTrue(container.listAllResources().keySet().contains("soup.txt"));
		container.removeResource("soup.txt");
		assertFalse(container.listAllResources().keySet().contains("soup.txt"));

		try {
			container.getResourceAsString("soup.txt");
			fail("Could still retrieve soup.txt");
		} catch (Exception ex) {
			// OK
		}
		
		container.save(tmpFile);
		// reload
		UCFPackage container2 = new UCFPackage(tmpFile);

		assertTrue(container2.listAllResources().keySet().contains("sub/"));
		container2.removeResource("sub"); 
		assertFalse(container2.listAllResources().keySet().contains("sub/"));
		assertFalse(container2.listAllResources().keySet()
				.contains("sub/1.txt"));

		container2.save(tmpFile);

		ZipFile zipFile = new ZipFile(tmpFile);
		assertNull("soup.txt still in zip file", zipFile.getEntry("soup.txt"));
		assertNull("sub/1.txt still in zip file", zipFile.getEntry("sub/1.txt"));
		assertNull("sub/2.txt still in zip file", zipFile.getEntry("sub/2.txt"));
		assertNull("sub/3.txt still in zip file", zipFile.getEntry("sub/3.txt"));
		assertNull("sub/ still in zip file", zipFile.getEntry("sub/"));
		zipFile.close();

		UCFPackage loaded = new UCFPackage(tmpFile);
		assertFalse(loaded.listAllResources().keySet().contains("soup.txt"));
		assertFalse(loaded.listAllResources().keySet().contains("sub/"));
		assertFalse(loaded.listAllResources().keySet().contains("sub/1.txt"));

		try {
			loaded.getResourceAsString("sub/1.txt");
			fail("Could still retrieve soup.txt");
		} catch (Exception ex) {
			// OK
		}
		loaded.save(tmpFile);
	}

	@Test
	public void fileListingRecursive() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		Set<String> expectedFiles = new HashSet<String>();

		container.addResource("Hello there", "helloworld.txt", "text/plain");
		expectedFiles.add("helloworld.txt");

		container.addResource("Soup for everyone", "soup.txt", "text/plain");
		expectedFiles.add("soup.txt");

		container.addResource("<html><body><h1>Yo</h1></body></html>",
				"soup.html", "text/html");
		expectedFiles.add("soup.html");

		container.addResource("Sub-folder entry 1", "sub/1.txt", "text/plain");
		container.addResource("Sub-folder entry 2", "sub/2.txt", "text/plain");
		container.addResource("Sub-folder entry 2", "sub/3/woho.txt",
				"text/plain");
		expectedFiles.add("sub/");
		expectedFiles.add("sub/1.txt");
		expectedFiles.add("sub/2.txt");
		expectedFiles.add("sub/3/");
		expectedFiles.add("sub/3/woho.txt");

		Map<String, ResourceEntry> beforeSaveRootEntries = container
				.listAllResources();

		assertEquals(expectedFiles, beforeSaveRootEntries.keySet());

		container.save(tmpFile);

		UCFPackage loaded = new UCFPackage(tmpFile);
		Map<String, ResourceEntry> loadedRootEntries = loaded
				.listAllResources();
		assertEquals(expectedFiles, loadedRootEntries.keySet());
	}

	@Test
	public void resourceEntries() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		container.addResource("Hello there", "helloworld.txt", "text/plain");
		container.addResource("Sub-folder entry 1", "sub/1.txt", "text/plain");

		ResourceEntry helloResource = container.listResources().get(
				"helloworld.txt");
		assertEquals("helloworld.txt", helloResource.getPath());
		assertEquals(11, helloResource.getSize());
		assertEquals("text/plain", helloResource.getMediaType());

		container.save(tmpFile);

		UCFPackage loaded = new UCFPackage(tmpFile);

		ResourceEntry loadedHelloResource = loaded.listResources().get(
				"helloworld.txt");
		assertEquals("helloworld.txt", loadedHelloResource.getPath());
		assertEquals(11, loadedHelloResource.getSize());
		assertEquals("text/plain", loadedHelloResource.getMediaType());

	}

	@Test
	public void getResourceEntry() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		container.addResource("Hello there", "helloworld.txt", "text/plain");
		container.addResource("Sub-folder entry 1", "sub/1.txt", "text/plain");

		ResourceEntry helloResource = container
				.getResourceEntry("helloworld.txt");
		assertEquals("helloworld.txt", helloResource.getPath());
		assertEquals(11, helloResource.getSize());
		assertEquals("text/plain", helloResource.getMediaType());
		assertFalse(helloResource.isFolder());

		ResourceEntry subResource = container.getResourceEntry("sub/");
		assertEquals("sub/", subResource.getPath());
		assertEquals(-1, subResource.getSize());
		assertEquals("text/plain", subResource.getMediaType());
		assertTrue(subResource.isFolder());

		container.save(tmpFile);

		UCFPackage loaded = new UCFPackage(tmpFile);

		ResourceEntry loadedHelloResource = loaded
				.getResourceEntry("helloworld.txt");
		assertEquals("helloworld.txt", loadedHelloResource.getPath());
		assertEquals(11, loadedHelloResource.getSize());
		assertEquals("text/plain", loadedHelloResource.getMediaType());
		assertFalse(loadedHelloResource.isFolder());

		ResourceEntry loadedSubResource = loaded.getResourceEntry("sub/");
		assertEquals("sub/", loadedSubResource.getPath());
		assertEquals(-1, loadedSubResource.getSize());
		assertEquals("text/plain", loadedSubResource.getMediaType());
		assertTrue(loadedSubResource.isFolder());
	}

	@Test
	public void manifestMimetype() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);

		container.save(tmpFile);
		ZipFile zipFile = new ZipFile(tmpFile);
		ZipEntry manifestEntry = zipFile.getEntry("META-INF/manifest.xml");
		InputStream manifestStream = zipFile.getInputStream(manifestEntry);
		
		//System.out.println(IOUtils.toString(manifestStream, "UTF-8"));
		/*
<?xml version="1.0" encoding="UTF-8"?>
<manifest:manifest xmlns:manifest="urn:oasis:names:tc:opendocument:xmlns:manifest:1.0">
</manifest:manifest>		 
*/
		Document doc = parseXml(manifestStream);
		assertEquals(MANIFEST_NS, doc.getRootElement().getNamespace());
		assertEquals("manifest", doc.getRootElement().getNamespacePrefix());
		assertEquals("manifest:manifest", doc.getRootElement().getQualifiedName());
		assertNull(xpathSelectElement(doc.getRootElement(), "/manifest:manifest/*"));
		
	}



    @Test
    public void rootFileVersion() throws Exception {
        UCFPackage container = new UCFPackage();
        container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
        container.addResource("Hello there", "helloworld.txt", "text/plain");
        container.setRootFile("helloworld.txt", "1.2.3");
        container.save(tmpFile);
        UCFPackage reloaded = new UCFPackage(tmpFile);
        assertEquals("1.2.3", reloaded.getRootFileVersion("helloworld.txt"));
    }
	
    @Test
    public void rootFileVersionNull() throws Exception {
        UCFPackage container = new UCFPackage();
        container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
        container.addResource("Hello there", "helloworld.txt", "text/plain");
        container.setRootFile("helloworld.txt", "1.2.3");
        container.save(tmpFile);
        UCFPackage reloaded = new UCFPackage(tmpFile);
//        reloaded.setRootFile("helloworld.txt");
        reloaded.save(tmpFile);
        @SuppressWarnings("unused")
		UCFPackage again = new UCFPackage(tmpFile);
        assertNull(reloaded.getRootFileVersion("helloworld.txt"));
    }

	@Test
	public void cloneUcfPackage() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		container.addResource("Hello there", "helloworld.txt", "text/plain");
		container.addResource("Soup for everyone", "soup.txt", "text/plain");
//		System.out.println(container.listAllResources());
		assertEquals(2, container.listAllResources().size());

		UCFPackage clone = container.clone();
		
		// Change the original to ensure independence
		container.setPackageMediaType("text/other");
		container.removeResource("soup.txt");
		container.addResource("Something else", "helloworld.txt", "test/other");
		container.addResource("extra", "extra1.txt", "text/plain");
		container.addResource("extra", "extra2.txt", "text/plain");
		
		assertEquals(UCFPackage.MIME_WORKFLOW_BUNDLE, clone.getPackageMediaType());
		assertEquals("Hello there", clone.getResourceAsString("helloworld.txt"));
		ResourceEntry helloWorldEntry = clone.getResourceEntry("helloworld.txt");
		assertEquals("text/plain", helloWorldEntry.getMediaType());
		assertEquals("Soup for everyone", clone.getResourceAsString("soup.txt"));
//		System.out.println(clone.listAllResources());
		assertEquals(2, clone.listAllResources().size());		
	}
	
}
