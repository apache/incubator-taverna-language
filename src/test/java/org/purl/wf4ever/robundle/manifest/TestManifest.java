package org.purl.wf4ever.robundle.manifest;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.Bundles;

public class TestManifest {
    @Test
    public void populateFromZip() throws Exception {
        GregorianCalendar before = new GregorianCalendar();
        Bundle bundle = exampleBundle();
        GregorianCalendar created = new GregorianCalendar();

        Path r = bundle.getRoot();
        URI base = r.toUri();
        
        Manifest manifest = new Manifest();
        manifest.populateFromBundle(bundle);
        GregorianCalendar populated = new GregorianCalendar();


        List<Path> paths = new ArrayList<>();
        for (PathMetadata s : manifest.aggregates) {
            Path path = uri2path(base, s.file);
            paths.add(path);
            assertNotNull(path.getParent());
            assertEquals(path.getParent(), s.folder);
            assertEquals(s.proxy.getScheme(), "urn:uuid");
            UUID.fromString(s.proxy.getSchemeSpecificPart());
            assertTrue(s.createdOn.after(before));
            assertTrue(s.createdOn.before(created));
            assertTrue(s.createdOn.before(populated));            
        }
    }

    private Path uri2path(URI base, URI uri) {
        URI fileUri = base.resolve(uri);
        return Paths.get(fileUri);
    }

    private Bundle exampleBundle() throws IOException {
        Bundle bundle = Bundles.createBundle();
        Path r = bundle.getRoot();
        Files.createFile(r.resolve("hello.txt"));
        Path f = r.resolve("f");
        Files.createDirectory(f);
        Files.createFile(f.resolve("file3.txt"));
        Files.createFile(f.resolve("file2.txt"));
        Files.createFile(f.resolve("file1.txt"));

        Path nested = f.resolve("nested");
        Files.createDirectory(nested);
        Files.createFile(nested.resolve("file1.txt"));

        Files.createDirectory(f.resolve("empty"));
        return bundle;
    }
}
