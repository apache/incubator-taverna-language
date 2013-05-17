package org.purl.wf4ever.robundle.manifest;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.purl.wf4ever.robundle.Bundle;

public class Manifest {
    private static final String MIMETYPE = "/mimetype";
    private static final String META_INF = "/META-INF";
    private static final String RO = "/.ro";

    public static FileTime now() {
        return FileTime.fromMillis(new GregorianCalendar().getTimeInMillis());
    }

    URI id = URI.create("/");
    List<Path> manifest = new ArrayList<>();
    FileTime createdOn = now();

    List<Agent> createdBy = new ArrayList<>();
    FileTime authoredOn;
    List<Agent> authoredBy;
    List<Path> history;
    List<PathMetadata> aggregates = new ArrayList<>();
    List<PathAnnotation> annotations = new ArrayList<>();
    List<String> graph;

    public void populateFromBundle(Bundle bundle) throws IOException {
        final Set<Path> potentiallyEmptyFolders = new LinkedHashSet<>();

        Files.walkFileTree(bundle.getRoot(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                    BasicFileAttributes attrs) throws IOException {
                if (dir.startsWith(RO) || dir.startsWith(META_INF)) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                potentiallyEmptyFolders.add(withSlash(dir));
                potentiallyEmptyFolders.remove(withSlash(dir.getParent()));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file,
                    BasicFileAttributes attrs) throws IOException {
                if (file.startsWith(MIMETYPE)) {
                    return FileVisitResult.CONTINUE;
                }
                // super.visitFile(file, attrs);
                PathMetadata metadata = new PathMetadata();
                // Strip out the widget:// magic
                metadata.file = file.getRoot().toUri().relativize(file.toUri());
                metadata.folder = withSlash(file.getParent());
                metadata.proxy = URI.create("urn:uuid:" + UUID.randomUUID());
                metadata.createdOn = Files.getLastModifiedTime(file);
                aggregates.add(metadata);
                potentiallyEmptyFolders.remove(file.getParent());
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                super.postVisitDirectory(dir, exc);
                if (potentiallyEmptyFolders.remove(dir)) {
                    PathMetadata metadata = new PathMetadata();
                    // Strip out the widget:// magic
                    metadata.file = dir.getRoot().toUri().relativize(withSlash(dir).toUri());
                    metadata.folder = withSlash(dir.getParent());
                    //metadata.proxy = URI.create("urn:uuid:" + UUID.randomUUID());
                    //metadata.createdOn = Files.getLastModifiedTime(dir);
                    aggregates.add(metadata);
                    potentiallyEmptyFolders.remove(withSlash(dir.getParent()));
                    return FileVisitResult.CONTINUE;
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    protected static Path withSlash(Path dir) {
        if (dir == null) {
            return null;
        }
        if (Files.isDirectory(dir)) {
            Path fname = dir.getFileName();
            if (fname == null) {
                return dir;
            }
            String fnameStr = fname.toString();
            if (fnameStr.endsWith("/")) {
                return dir;
            }
            return dir.resolveSibling(fnameStr + "/");
        }
        return dir;
    }

}
