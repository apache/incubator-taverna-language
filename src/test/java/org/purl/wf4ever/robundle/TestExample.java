package org.purl.wf4ever.robundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Desktop;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.NavigableMap;

import org.junit.Test;
import org.purl.wf4ever.robundle.ROBundle;
import org.purl.wf4ever.robundle.ROBundles;

public class TestExample {
	@Test
	public void example() throws Exception {
		// Create a new (temporary) data bundle
		ROBundle dataBundle = ROBundles.createDataBundle();

		// Get the inputs
		Path inputs = dataBundle.getRoot().resolve("inputs");
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

		Files.copy(localFile, dataBundle.getRoot().resolve("out1"));

		// Representing references
		URI ref = URI.create("http://example.com/external.txt");
		Path out3 = dataBundle.getRoot().resolve("out3");
		System.out.println(ROBundles.setReference(out3, ref));
		if (ROBundles.isReference(out3)) {
			URI resolved = ROBundles.getReference(out3);
			System.out.println(resolved);
		}

		// Saving a data bundle:
		Path zip = Files.createTempFile("databundle", ".zip");
		ROBundles.closeAndSaveDataBundle(dataBundle, zip);
		// NOTE: From now dataBundle and its Path's are CLOSED
		// and can no longer be accessed

		System.out.println("Saved to " + zip);
		if (Desktop.isDesktopSupported()) {
			// Open ZIP file for browsing
			Desktop.getDesktop().open(zip.toFile());
		}

		// Loading a data bundle back from disk
		try (ROBundle dataBundle2 = ROBundles.openDataBundle(zip)) {
			assertEquals(zip, dataBundle2.getSource());
			
		}
	}
}
