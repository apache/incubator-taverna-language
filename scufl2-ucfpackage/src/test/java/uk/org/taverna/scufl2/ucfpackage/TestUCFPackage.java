package uk.org.taverna.scufl2.ucfpackage;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;

public class TestUCFPackage {

	private static final int MIME_OFFSET = 30;
	private static final boolean DELETE_FILES = true;
	private File tmpFile;

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

	@Test(expected = IllegalStateException.class)
	public void noDefaultMimeType() throws Exception {
		UCFPackage container = new UCFPackage();
		container.save(tmpFile);
	}

	@Test
	public void mimeTypePosition() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_EPUB);
		assertEquals(UCFPackage.MIME_EPUB, container.getPackageMediaType());
		container.save(tmpFile);
		assertTrue(tmpFile.exists());
		ZipFile zipFile = new ZipFile(tmpFile);
		// Must be first entry
		ZipEntry mimeEntry = zipFile.entries().nextElement();
		assertEquals("First zip entry is not 'mimetype'", "mimetype",
				mimeEntry.getName());
		assertEquals(
				"mimetype should be uncompressed, but compressed size mismatch",
				mimeEntry.getCompressedSize(), mimeEntry.getSize());
		assertEquals("mimetype should have STORED method", ZipEntry.STORED,
				mimeEntry.getMethod());
		assertEquals("Wrong mimetype", UCFPackage.MIME_EPUB,
				IOUtils.toString(zipFile.getInputStream(mimeEntry), "ASCII"));

		// Check position 30++ according to
		// http://livedocs.adobe.com/navigator/9/Navigator_SDK9_HTMLHelp/wwhelp/wwhimpl/common/html/wwhelp.htm?context=Navigator_SDK9_HTMLHelp&file=Appx_Packaging.6.1.html#1522568
		byte[] expected = ("mimetype" + UCFPackage.MIME_EPUB + "PK")
				.getBytes("ASCII");
		FileInputStream in = new FileInputStream(tmpFile);
		assertEquals(MIME_OFFSET, in.skip(MIME_OFFSET));
		byte[] actual = new byte[expected.length];
		assertEquals(expected.length, in.read(actual));
		assertArrayEquals(expected, actual);
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
		InputStream manifestStream = zipFile.getInputStream(manifestEntry);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
						+ "<manifest:manifest xmlns:manifest=\"urn:oasis:names:tc:opendocument:xmlns:manifest:1.0\">\n"
						+ " <manifest:file-entry manifest:media-type=\"text/plain\" manifest:full-path=\"helloworld.txt\" manifest:size=\"18\"/>\n"
						+ "</manifest:manifest>",
				IOUtils.toString(manifestStream, "UTF-8"));
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
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
						+ "<manifest:manifest xmlns:manifest=\"urn:oasis:names:tc:opendocument:xmlns:manifest:1.0\">\n"
						+ " <manifest:file-entry manifest:media-type=\"application/octet-stream\" manifest:full-path=\"binary\" manifest:size=\"1024\"/>\n"
						+ "</manifest:manifest>",
				IOUtils.toString(manifestStream, "UTF-8"));
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
		container2.removeResource("sub"); // should not work
		assertTrue(container2.listAllResources().keySet().contains("sub/"));
		assertTrue(container2.listAllResources().keySet().contains("sub/1.txt"));

		container2.removeResource("sub/");
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
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
						+ "<manifest:manifest xmlns:manifest=\"urn:oasis:names:tc:opendocument:xmlns:manifest:1.0\">\n"
						+ "</manifest:manifest>",
				IOUtils.toString(manifestStream, "UTF-8"));
	}

	@Test
	public void getRootfiles() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		container.addResource("Hello there", "helloworld.txt", "text/plain");
		assertTrue("Root files not empty", container.getRootFiles().isEmpty());
		assertNotSame("Should return copy of rootfiles",
				container.getRootFiles(), container.getRootFiles());
	}

	@Test
	public void setRootfile() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		container.addResource("Hello there", "helloworld.txt", "text/plain");
		container.addResource("Soup for everyone", "soup.txt", "text/plain");
		container.setRootFile("helloworld.txt");

		List<ResourceEntry> rootFiles = container.getRootFiles();

		assertEquals(1, rootFiles.size());
		ResourceEntry rootFile = rootFiles.get(0);
		assertEquals("helloworld.txt", rootFile.getPath());
		assertEquals("text/plain", rootFile.getMediaType());
	}

	@Test
	public void setRootfileSaved() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		container.addResource("Hello there", "helloworld.txt", "text/plain");
		container.addResource("Soup for everyone", "soup.txt", "text/plain");
		container.setRootFile("helloworld.txt");
		container.save(tmpFile);

		ZipFile zipFile = new ZipFile(tmpFile);
		ZipEntry manifestEntry = zipFile.getEntry("META-INF/container.xml");
		InputStream manifestStream = zipFile.getInputStream(manifestEntry);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
						+ "<container xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ns3=\"http://www.w3.org/2001/04/xmlenc#\">\n"
						+ "    <rootFiles>\n"
						+ "        <rootFile media-type=\"text/plain\" full-path=\"helloworld.txt\"/>\n"
						+ "    </rootFiles>\n" + "</container>\n",
				IOUtils.toString(manifestStream, "UTF-8"));

	}

	@Test
	public void addResourceContainerXml() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		container.addResource("Hello there", "helloworld.txt", "text/plain");
		container.addResource("Soup for everyone", "soup.txt", "text/plain");
		container.setRootFile("helloworld.txt");
		assertEquals("helloworld.txt", container.getRootFiles().get(0)
				.getPath());

		String containerXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
				+ "<container xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\" xmlns:ex='http://example.com/' xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ns3=\"http://www.w3.org/2001/04/xmlenc#\">\n"
				+ "   <ex:example>first example</ex:example>\n"
				+ "    <rootFiles>\n"
				+ "        <rootFile ex:extraAnnotation='hello' media-type=\"text/plain\" full-path=\"soup.txt\"/>\n"
				+ "    </rootFiles>\n"
				+ "   <ex:example>second example</ex:example>\n"
				+ "</container>\n";
		// Should overwrite setRootFile()
		container.addResource(containerXml, "META-INF/container.xml",
				"text/xml");


		assertEquals("soup.txt", container.getRootFiles().get(0).getPath());


		container.save(tmpFile);

		ZipFile zipFile = new ZipFile(tmpFile);
		ZipEntry manifestEntry = zipFile.getEntry("META-INF/container.xml");
		InputStream manifestStream = zipFile.getInputStream(manifestEntry);
		assertEquals(containerXml, IOUtils.toString(manifestStream, "UTF-8"));

	}

	@Test
	public void setRootfileExtendsContainerXml() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		container.addResource("Hello there", "helloworld.txt", "text/plain");
		container.addResource("<html><body><h1>Yo</h1></body></html>",
				"helloworld.html", "text/html");

		String containerXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
				+ "<container xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\" xmlns:ex='http://example.com/' xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ns3=\"http://www.w3.org/2001/04/xmlenc#\">\n"
				+ "    <rootFiles>\n"
				+ "        <rootFile ex:extraAnnotation='hello' media-type=\"text/html\" full-path=\"helloworld.html\"/>\n"
				+ "    </rootFiles>\n"
				+ "   <ex:example>more example</ex:example>\n"
				+ "</container>\n";
		// Should overwrite setRootFile()
		container.addResource(containerXml, "META-INF/container.xml",
				"text/xml");
		container.setRootFile("helloworld.txt");
		assertEquals("helloworld.html", container.getRootFiles().get(0)
				.getPath());
		assertEquals("helloworld.txt", container.getRootFiles().get(1)
				.getPath());
		container.save(tmpFile);

		String expectedContainerXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
				+ "<container xmlns=\"urn:oasis:names:tc:opendocument:xmlns:container\" xmlns:ex='http://example.com/' xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ns3=\"http://www.w3.org/2001/04/xmlenc#\">\n"
				+ "    <rootFiles>\n"
				+ "        <rootFile ex:extraAnnotation='hello' media-type=\"text/html\" full-path=\"helloworld.html\"/>\n"
				+ "        <rootFile media-type=\"text/plain\" full-path=\"helloworld.txt\"/>\n"
				+ "    </rootFiles>\n"
				+ "   <ex:example>more example</ex:example>\n"
				+ "</container>\n";

		ZipFile zipFile = new ZipFile(tmpFile);
		ZipEntry manifestEntry = zipFile.getEntry("META-INF/container.xml");
		InputStream manifestStream = zipFile.getInputStream(manifestEntry);
		assertEquals(expectedContainerXml,
				IOUtils.toString(manifestStream, "UTF-8"));

	}

	@Test(expected = IllegalArgumentException.class)
	public void setRootfileMissing() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		container.addResource("Hello there", "helloworld.txt", "text/plain");
		try {
			container.setRootFile("unknown.txt");
		} finally {
			assertTrue("Should not have added unknown.txt", container
					.getRootFiles().isEmpty());
		}

	}

	@Test
	public void unmodifiableRootFiles() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		container.addResource("Hello there", "helloworld.txt", "text/plain");
		container.setRootFile("helloworld.txt");
		List<ResourceEntry> rootFiles = container.getRootFiles();
		assertEquals(1, rootFiles.size());
		rootFiles.remove(0);
		assertEquals(0, rootFiles.size());
		assertEquals("Should not be able to modify rootFiles list", 1,
				container.getRootFiles().size());
	}

	@Test
	public void multipleRootfiles() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		container.addResource("Hello there", "helloworld.txt", "text/plain");
		container.addResource("<html><body><h1>Yo</h1></body></html>",
				"helloworld.html", "text/html");
		container.addResource("Soup for everyone", "soup.txt", "text/plain");
		container.setRootFile("helloworld.txt");
		// Adding a second file of a different mime type
		container.setRootFile("helloworld.html");
		List<ResourceEntry> rootFiles = container.getRootFiles();
		assertEquals(2, rootFiles.size());

		ResourceEntry rootFile0 = rootFiles.get(0);
		assertEquals("helloworld.txt", rootFile0.getPath());
		assertEquals("text/plain", rootFile0.getMediaType());

		ResourceEntry rootFile1 = rootFiles.get(1);
		assertEquals("helloworld.html", rootFile1.getPath());
		assertEquals("text/html", rootFile1.getMediaType());
	}

	@Test
	public void multipleRootfilesReloaded() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		container.addResource("Hello there", "helloworld.txt", "text/plain");
		container.addResource("<html><body><h1>Yo</h1></body></html>",
				"helloworld.html", "text/html");
		container.addResource("Soup for everyone", "soup.txt", "text/plain");
		container.setRootFile("helloworld.txt");
		// Adding a second file of a different mime type
		container.setRootFile("helloworld.html");

		container.save(tmpFile);
		UCFPackage reloaded = new UCFPackage(tmpFile);

		List<ResourceEntry> rootFiles = reloaded.getRootFiles();
		assertEquals(2, rootFiles.size());

		ResourceEntry rootFile0 = rootFiles.get(0);
		assertEquals("helloworld.txt", rootFile0.getPath());
		assertEquals("text/plain", rootFile0.getMediaType());

		ResourceEntry rootFile1 = rootFiles.get(1);
		assertEquals("helloworld.html", rootFile1.getPath());
		assertEquals("text/html", rootFile1.getMediaType());
	}

	@Test
	public void unsetMultipleRootFiles() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		container.addResource("Hello there", "helloworld.txt", "text/plain");
		container.addResource("<html><body><h1>Yo</h1></body></html>",
				"helloworld.html", "text/html");
		container.addResource("Soup for everyone", "soup.txt", "text/plain");
		container.setRootFile("helloworld.txt");
		container.setRootFile("helloworld.html");
		List<ResourceEntry> rootFiles = container.getRootFiles();
		assertEquals(2, rootFiles.size());

		container.unsetRootFile("helloworld.txt");
		rootFiles = container.getRootFiles();
		assertEquals(1, rootFiles.size());

		ResourceEntry rootFile0 = rootFiles.get(0);
		assertEquals("helloworld.html", rootFile0.getPath());
		assertEquals("text/html", rootFile0.getMediaType());
	}

	@Test
	public void unsetRootFile() throws Exception {
		UCFPackage container = new UCFPackage();
		container.setPackageMediaType(UCFPackage.MIME_WORKFLOW_BUNDLE);
		container.addResource("Hello there", "helloworld.txt", "text/plain");
		container.addResource("Soup for everyone", "soup.txt", "text/plain");
		container.setRootFile("helloworld.txt");
		assertEquals(1, container.getRootFiles().size());
		container.unsetRootFile("helloworld.txt");
		assertEquals(0, container.getRootFiles().size());
	}

}
