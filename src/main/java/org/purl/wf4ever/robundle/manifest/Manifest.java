package org.purl.wf4ever.robundle.manifest;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.purl.wf4ever.robundle.Bundle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@JsonPropertyOrder(value = { "@context", "id", "manifest", "createdOn",
        "createdBy", "createdOn", "authoredOn", "authoredBy", "history",
        "aggregates", "annotations", "@graph" })
public class Manifest {
    
    private static URI ROOT = URI.create("/");

    private static final String META_INF = "/META-INF";
    private static final String MIMETYPE = "/mimetype";
    private static final String RO = "/.ro";

    public static FileTime now() {
        return FileTime.fromMillis(new GregorianCalendar().getTimeInMillis());
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

    private List<PathMetadata> aggregates = new ArrayList<>();
    private List<PathAnnotation> annotations = new ArrayList<>();
    private List<Agent> authoredBy;
    private FileTime authoredOn;
    private Bundle bundle;
    private List<Agent> createdBy = new ArrayList<>();
    private FileTime createdOn = now();
    private List<String> graph;
    private List<Path> history;
    private URI id = URI.create("/");
    private List<Path> manifest = new ArrayList<>();

    public Manifest(Bundle bundle) {
        this.bundle = bundle;
    }

    public List<PathMetadata> getAggregates() {
        return aggregates;
    }

    public List<PathAnnotation> getAnnotations() {
        return annotations;
    }

    public List<Agent> getAuthoredBy() {
        return authoredBy;
    }

    @JsonIgnore
    public FileTime getAuthoredOn() {
        return authoredOn;
    }

    @JsonIgnore
    public Bundle getBundle() {
        return bundle;
    }

    @JsonProperty(value = "@context")
    public List<Object> getContext() {
        ArrayList<Object> context = new ArrayList<>();
        HashMap<Object, Object> map = new HashMap<>();
        map.put("@base", getBundle().getRoot().toUri());
        context.add(map);
        context.add(URI
                .create("http://purl.org/wf4ever/ro-bundle/context.json"));
        return context;
    }

    public List<Agent> getCreatedBy() {
        return createdBy;
    }

    @JsonIgnore
    public FileTime getCreatedOn() {
        return createdOn;
    }

    public List<String> getGraph() {
        return graph;
    }

    @JsonIgnore
    public List<Path> getHistory() {
        return history;
    }

    public URI getId() {
        return id;
    }

    @JsonIgnore
    public List<Path> getManifest() {
        return manifest;
    }

    public void populateFromBundle() throws IOException {
        final Set<Path> potentiallyEmptyFolders = new LinkedHashSet<>();

        Files.walkFileTree(bundle.getRoot(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                super.postVisitDirectory(dir, exc);
                if (potentiallyEmptyFolders.remove(dir)) {
                    PathMetadata metadata = new PathMetadata();
                    // Strip out the widget:// magic
                    metadata.setFile(ROOT.resolve(dir.getRoot().toUri()
                            .relativize(withSlash(dir).toUri())));
                    metadata.setFolder(withSlash(dir.getParent()));
                    // metadata.proxy = URI.create("urn:uuid:" +
                    // UUID.randomUUID());
                    // metadata.createdOn = Files.getLastModifiedTime(dir);
                    aggregates.add(metadata);
                    potentiallyEmptyFolders.remove(withSlash(dir.getParent()));
                    return FileVisitResult.CONTINUE;
                }
                return FileVisitResult.CONTINUE;
            }

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
                metadata.setFile(ROOT.resolve(file.getRoot().toUri()
                        .relativize(file.toUri())));
                metadata.setFolder(withSlash(file.getParent()));
                metadata.setProxy(URI.create("urn:uuid:" + UUID.randomUUID()));
                metadata.setCreatedOn(Files.getLastModifiedTime(file));
                aggregates.add(metadata);
                potentiallyEmptyFolders.remove(file.getParent());
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void setAggregates(List<PathMetadata> aggregates) {
        this.aggregates = aggregates;
    }

    public void setAnnotations(List<PathAnnotation> annotations) {
        this.annotations = annotations;
    }

    public void setAuthoredBy(List<Agent> authoredBy) {
        this.authoredBy = authoredBy;
    }

    public void setAuthoredOn(FileTime authoredOn) {
        this.authoredOn = authoredOn;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public void setCreatedBy(List<Agent> createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedOn(FileTime createdOn) {
        this.createdOn = createdOn;
    }

    public void setGraph(List<String> graph) {
        this.graph = graph;
    }

    public void setHistory(List<Path> history) {
        this.history = history;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public void setManifest(List<Path> manifest) {
        this.manifest = manifest;
    }

    public Path writeAsJsonLD() throws IOException {
        Path jsonld = bundle.getFileSystem().getPath(".ro", "manifest.json");
        Files.createDirectories(jsonld.getParent());
        Files.createFile(jsonld);
        if (!manifest.contains(jsonld)) {
            manifest.add(0, jsonld);
        }
        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        om.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);

        om.setSerializationInclusion(Include.NON_NULL);
        try (Writer w = Files.newBufferedWriter(jsonld,
                Charset.forName("UTF-8"), StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            om.writeValue(w, this);
        }
        return jsonld;
    }

    public PathMetadata getAggregation(Path file) {
        URI fileUri = file.toUri();
        fileUri = ROOT.resolve(file.getRoot().toUri().relativize(fileUri));
        return getAggregation(fileUri);
    }

    public PathMetadata getAggregation(URI uri) {
        for (PathMetadata meta : getAggregates()) {
            if (uri.equals(meta.getFile()) || uri.equals(meta.getUri()) || uri.equals(meta.getProxy())) {
                return meta;
            }
        }
        return null;
    }
}
