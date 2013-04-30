package uk.org.taverna.databundle;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility functions for dealing with data bundles.
 * <p>
 * The style of using this class is similar to that of {@link Files}. In fact, a
 * data bundle is implemented as a set of {@link Path}s.
 * 
 * @author Stian Soiland-Reyes
 * 
 */
public class DataBundles {

	private static final String INPUTS = "inputs";
	private static final String OUTPUTS = "outputs";
	
	public static Path createDataBundle() throws IOException {
		return Files.createTempDirectory("databundle");		
	}
	
	public static Path getInputs(Path dataBundle) throws IOException {
		Path inputs = dataBundle.resolve(INPUTS);
		Files.createDirectories(inputs);
		return inputs;
	}
	
	public static Path getOutputs(Path dataBundle) throws IOException {
		Path inputs = dataBundle.resolve(OUTPUTS);
		Files.createDirectories(inputs);
		return inputs;
	}

	public static boolean hasInputs(Path dataBundle) {
		Path inputs = dataBundle.resolve(INPUTS);
		return Files.isDirectory(inputs);
	}

	public static boolean hasOutputs(Path dataBundle) {
		Path outputs = dataBundle.resolve(OUTPUTS);
		return Files.isDirectory(outputs);
	}
}
