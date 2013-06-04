package org.purl.wf4ever.robundle.manifest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.Bundles;

public class TestManifest {
    private Bundle bundle;

    @Test
    public void populateFromBundle() throws Exception {
        Path r = bundle.getRoot();
        URI base = r.toUri();

        Manifest manifest = new Manifest(bundle);
        manifest.populateFromBundle();

        List<String> uris = new ArrayList<>();
        for (PathMetadata s : manifest.getAggregates()) {
            uris.add(s.getFile().toASCIIString());
            Path path = uri2path(base, s.getFile());
            assertNotNull(path.getParent());
            assertEquals(Manifest.withSlash(path.getParent()), s.getFolder());
            if (s.getFile().equals(URI.create("f/nested/empty/"))) {
                continue;
                // Folder's don't need proxy and createdOn
            }            
            assertEquals("urn", s.getProxy().getScheme());
            UUID.fromString(s.getProxy().getSchemeSpecificPart().replace("uuid:", ""));
            assertEquals(s.getCreatedOn(), Files.getLastModifiedTime(path));
        }
        assertFalse(uris.contains("mimetype"));
        assertFalse(uris.contains("META-INF"));
        assertTrue(uris.remove("hello.txt"));
        assertTrue(uris.remove("f/file1.txt"));
        assertTrue(uris.remove("f/file2.txt"));
        assertTrue(uris.remove("f/file3.txt"));
        assertTrue(uris.remove("f/nested/file1.txt"));
        assertTrue(uris.remove("f/nested/empty/"));
        assertTrue(uris.isEmpty());
    }

    private Path uri2path(URI base, URI uri) {
        URI fileUri = base.resolve(uri);
        return Paths.get(fileUri);
    }


    @Test
    public void writeAsJsonLD() throws Exception {
        Manifest manifest = new Manifest(bundle);
        manifest.populateFromBundle();
        PathMetadata helloMeta = null;
        for (PathMetadata meta : manifest.getAggregates()) {
            URI root = URI.create("/");
            if (root.resolve(meta.getFile()).equals(root.resolve("hello.txt"))) {
                helloMeta = meta;
            }
        }
        assertNotNull("No metadata for </hello.txt>", helloMeta);
        
        
        
        Path jsonld = manifest.writeAsJsonLD();
        assertEquals(bundle.getFileSystem().getPath(".ro",  "manifest.json"), jsonld);
        assertTrue(Files.exists(jsonld));
        String manifestStr = new String(Files.readAllBytes(jsonld), "UTF8");
        System.out.println(manifestStr);
        
        // Rough and ready that somethings are there
        // TODO: Read back and check as JSON structure
        // TODO: Check as JSON-LD graph 
        assertTrue(manifestStr.contains("@context"));
        assertTrue(manifestStr.contains("http://purl.org/wf4ever/ro-bundle/context.json"));
        assertTrue(manifestStr.contains("f/file2.txt"));
        assertTrue(manifestStr.contains("hello.txt"));
        assertTrue(manifestStr.contains(helloMeta.getProxy().toASCIIString()));
    }
    
    @Before
    public void exampleBundle() throws IOException {
        Path source;
        try (Bundle bundle = Bundles.createBundle()) {
            source = bundle.getSource();
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

            Files.createDirectory(nested.resolve("empty"));
            bundle.setDeleteOnClose(false);
        }
        bundle = Bundles.openBundle(source);
    }
    
    @After
    public void closeBundle() throws IOException {
        bundle.close();
        
    }
}
