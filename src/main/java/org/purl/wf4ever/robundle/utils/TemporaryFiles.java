package org.purl.wf4ever.robundle.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TemporaryFiles {

    public static Path temporaryBundle()
            throws IOException {
        Path tempDir = Files.createTempDirectory("robundle");
        tempDir.toFile().deleteOnExit();
        // Why inside a tempDir? Because ZipFileSystemProvider
        // creates neighbouring temporary files
        // per file that is written to zip, which could mean a lot of
        // temporary files directly in /tmp - making it difficult to clean up
        Path bundle = tempDir.resolve("robundle.zip");
        bundle.toFile().deleteOnExit();
        return bundle;
    }


}
