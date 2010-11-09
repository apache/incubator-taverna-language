package uk.org.taverna.scufl2.bundle;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestScufl2Bundle {


	private static final int MIME_OFFSET = 30;
	private File tmpFile;

	@Test(expected = IllegalArgumentException.class)
	public void mimeTypeInvalidCharset() throws Exception {
		Scufl2Bundle scufl2Bundle = new Scufl2Bundle();
		scufl2Bundle.setMimeType("food/brød");
	}

	@Test(expected = IllegalArgumentException.class)
	public void mimeTypeEmpty() throws Exception {
		Scufl2Bundle scufl2Bundle = new Scufl2Bundle();
		scufl2Bundle.setMimeType("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void mimeTypeNoSlash() throws Exception {
		Scufl2Bundle scufl2Bundle = new Scufl2Bundle();
		scufl2Bundle.setMimeType("applicationtext");
	}

	@Test
	public void defaultMimeType() throws Exception {
		Scufl2Bundle scufl2Bundle = new Scufl2Bundle();
		assertEquals(Scufl2Bundle.MIME_SCUFL2_BUNDLE,
				scufl2Bundle.getMimeType());
		System.out.println(tmpFile);
		scufl2Bundle.save(tmpFile);
		assertTrue(tmpFile.exists());
		ZipFile zipFile = new ZipFile(tmpFile);
		// Must be first entry
		ZipEntry mimeEntry = zipFile.entries().nextElement();
		assertEquals("First zip entry is not 'mimetype'", "mimetype",
				mimeEntry.getName());
		assertEquals("mimetype should be uncompressed, but compressed size mismatch", mimeEntry.getCompressedSize(), mimeEntry.getSize());
		assertEquals("mimetype should have STORED method", ZipEntry.STORED, mimeEntry.getMethod());
		assertEquals("Wrong mimetype", Scufl2Bundle.MIME_SCUFL2_BUNDLE,
				IOUtils.toString(zipFile.getInputStream(mimeEntry)));

		// Check position 30++ according to
		// http://livedocs.adobe.com/navigator/9/Navigator_SDK9_HTMLHelp/wwhelp/wwhimpl/common/html/wwhelp.htm?context=Navigator_SDK9_HTMLHelp&file=Appx_Packaging.6.1.html#1522568
		byte[] expected = ("mimetype" + Scufl2Bundle.MIME_SCUFL2_BUNDLE + "PK")
				.getBytes("ASCII");
		FileInputStream in = new FileInputStream(tmpFile);
		assertEquals(MIME_OFFSET, in.skip(MIME_OFFSET));
		byte[] actual = new byte[expected.length];
		assertEquals(expected.length, in.read(actual));
		assertArrayEquals(expected, actual);
	}

	@Before
	public void createTempFile() throws IOException {
		tmpFile = File.createTempFile("test", ".bundle");
		assertTrue(tmpFile.delete());
		tmpFile.deleteOnExit();
	}

	@Test
	public void workflowBundleMimeType() throws Exception {
		Scufl2Bundle scufl2Bundle = new Scufl2Bundle();
		scufl2Bundle.setMimeType(Scufl2Bundle.MIME_WORKFLOW_BUNDLE);
		assertEquals(Scufl2Bundle.MIME_WORKFLOW_BUNDLE,
				scufl2Bundle.getMimeType());
		File tmpFile = File.createTempFile("test", ".bundle");
		assertTrue(tmpFile.delete());
		System.out.println(tmpFile);
		scufl2Bundle.save(tmpFile);
		ZipFile zipFile = new ZipFile(tmpFile);
		ZipEntry mimeEntry = zipFile.getEntry("mimetype");
		assertEquals("mimetype", mimeEntry.getName());
		assertEquals("Wrong mimetype", Scufl2Bundle.MIME_WORKFLOW_BUNDLE,
				IOUtils.toString(zipFile.getInputStream(mimeEntry)));

	}

	@Test
	public void manifestMimetype() throws Exception {
		Scufl2Bundle scufl2Bundle = new Scufl2Bundle();
		scufl2Bundle.setMimeType(Scufl2Bundle.MIME_WORKFLOW_BUNDLE);

		File tmpFile = File.createTempFile("test", ".bundle");
		assertTrue(tmpFile.delete());
		System.out.println(tmpFile);
		scufl2Bundle.save(tmpFile);
		ZipFile zipFile = new ZipFile(tmpFile);
		ZipEntry manifestEntry = zipFile.getEntry("META-INF/manifest.xml");
		InputStream manifestStream = zipFile.getInputStream(manifestEntry);

	}

}
