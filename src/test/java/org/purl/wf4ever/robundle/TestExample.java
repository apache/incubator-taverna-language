package org.purl.wf4ever.robundle;

import static org.junit.Assert.assertEquals;

import java.awt.Desktop;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

public class TestExample {
	@Test
	public void example() throws Exception {
		// Create a new (temporary) RO bundle
		ROBundle bundle = ROBundles.createBundle();

		// Get the inputs
		Path inputs = bundle.getRoot().resolve("inputs");
		Files.createDirectory(inputs);

		// Get an input port:
		Path in1 = inputs.resolve("in1");

		// Setting a string value for the input port:
		ROBundles.setStringValue(in1, "Hello");

		// And retrieving it
		if (ROBundles.isValue(in1)) {
			System.out.println(ROBundles.getStringValue(in1));
		}

		// Or just use the regular Files methods:
		for (String line : Files.readAllLines(in1, Charset.forName("UTF-8"))) {
			System.out.println(line);
		}

		// Binaries and large files are done through the Files API
		try (OutputStream out = Files.newOutputStream(in1,
				StandardOpenOption.APPEND)) {
			out.write(32);
		}
		// Or Java 7 style
		Path localFile = Files.createTempFile("", ".txt");
		Files.copy(in1, localFile, StandardCopyOption.REPLACE_EXISTING);
		System.out.println("Written to: " + localFile);

		Files.copy(localFile, bundle.getRoot().resolve("out1"));

		// Representing references
		URI ref = URI.create("http://example.com/external.txt");
		Path out3 = bundle.getRoot().resolve("out3");
		System.out.println(ROBundles.setReference(out3, ref));
		if (ROBundles.isReference(out3)) {
			URI resolved = ROBundles.getReference(out3);
			System.out.println(resolved);
		}

		// Saving a bundle:
		Path zip = Files.createTempFile("bundle", ".zip");
		ROBundles.closeAndSaveBundle(bundle, zip);
		// NOTE: From now "bundle" and its Path's are CLOSED
		// and can no longer be accessed

		System.out.println("Saved to " + zip);
		if (Desktop.isDesktopSupported()) {
			// Open ZIP file for browsing
			Desktop.getDesktop().open(zip.toFile());
		}

		// Loading a bundle back from disk
		try (ROBundle bundle2 = ROBundles.openBundle(zip)) {
			assertEquals(zip, bundle2.getSource());
			
		}
	}
}
