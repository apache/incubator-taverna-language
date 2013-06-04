package org.purl.wf4ever.robundle.fs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Test;
import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.Bundles;


public class TestBundleFileTypeDetector {
	@Test
	public void detectRoBundle() throws Exception {
		BundleFileTypeDetector detector = new BundleFileTypeDetector();
		try (Bundle bundle = Bundles.createBundle()) {
            assertEquals("application/vnd.wf4ever.robundle+zip",
                    detector.probeContentType(bundle.getSource()));
        }
	}

	@Test
	public void detectEmptyZip() throws Exception {
		BundleFileTypeDetector detector = new BundleFileTypeDetector();

		Path zip = Files.createTempFile("test", ".bin");
		zip.toFile().deleteOnExit();
		try (ZipOutputStream zout = new ZipOutputStream(
				Files.newOutputStream(zip))) {
			ZipEntry entry = new ZipEntry("e");
			zout.putNextEntry(entry);
			zout.closeEntry();

		}
		assertEquals("application/zip", detector.probeContentType(zip));
	}

	@Test
	public void detectNonZip() throws Exception {
		BundleFileTypeDetector detector = new BundleFileTypeDetector();

		Path file = Files.createTempFile("test", ".bin");
		file.toFile().deleteOnExit();
		Files.write(file, Arrays.asList("This is just some text",
				"added here to make the file", "larger than 38 bytes"), Charset
				.forName("UTF8"));
		assertTrue(Files.size(file)> 38);
		assertNull(detector.probeContentType(file));
	}

	@Test
	public void detectEmpty() throws Exception {
		BundleFileTypeDetector detector = new BundleFileTypeDetector();

		Path file = Files.createTempFile("test", ".bin");
		file.toFile().deleteOnExit();
		assertEquals(0, Files.size(file));
		assertNull(detector.probeContentType(file));
	}
	
	@Test
	public void detectorSPI() throws Exception {
		try (Bundle bundle = Bundles.createBundle()) {		
    		assertEquals("application/vnd.wf4ever.robundle+zip",
    				Files.probeContentType(bundle.getSource()));
		}
		
	}
	
}
