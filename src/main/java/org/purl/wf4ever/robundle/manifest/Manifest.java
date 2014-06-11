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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.Bundles;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@JsonPropertyOrder(value = { "@context", "id", "manifest", "createdOn",
        "createdBy", "createdOn", "authoredOn", "authoredBy", "history",
        "aggregates", "annotations", "@graph" })
public class Manifest {
    

		private static final String MANIFEST_XML = "manifest.xml";
	private static final String MANIFEST_JSON = "manifest.json";

	private static Logger logger = Logger.getLogger(Manifest.class.getCanonicalName());
    
    private static URI ROOT = URI.create("/");

    public abstract class PathMixin {
        @JsonValue
        public abstract String toString();
    }
    public abstract class FileTimeMixin {
        @JsonValue
        public abstract String toString();
    }
    
    
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

    private Map<URI,PathMetadata> aggregates = new LinkedHashMap<>();             
    private List<PathAnnotation> annotations = new ArrayList<>();
    private List<Agent> authoredBy = new ArrayList<>();
    private FileTime authoredOn;
    private Bundle bundle;
    private List<Agent> createdBy = new ArrayList<>();
    private FileTime createdOn = now();
    private List<String> graph;
    private List<Path> history = new ArrayList<>();
    private URI id = URI.create("/");
    private List<Path> manifest = new ArrayList<>();

    public Manifest(Bundle bundle) {
        this.bundle = bundle;
    }

    public List<PathMetadata> getAggregates() {
        return new ArrayList(aggregates.values());
    }

    public List<PathAnnotation> getAnnotations() {
        return annotations;
    }

    public List<Agent> getAuthoredBy() {
        return authoredBy;
    }

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
//        HashMap<Object, Object> map = new HashMap<>();
//        map.put("@base", getBaseURI());
//        context.add(map);
        context.add(URI
                .create("https://w3id.org/bundle/context"));
        return context;
    }

    @JsonIgnore
    public URI getBaseURI() {
        return getBundle().getRoot().toUri();
    }

    public List<Agent> getCreatedBy() {
        return createdBy;
    }

    public FileTime getCreatedOn() {
        return createdOn;
    }

    public List<String> getGraph() {
        return graph;
    }

    public List<Path> getHistory() {
        return history;
    }

    public URI getId() {
        return id;
    }

    public List<Path> getManifest() {
        return manifest;
    }

    public void populateFromBundle() throws IOException {
        final Set<Path> potentiallyEmptyFolders = new LinkedHashSet<>();

        final Set<URI> existingAggregationsToPrune = new HashSet<>(aggregates.keySet()); 
        
        Files.walkFileTree(bundle.getRoot(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException {
                super.postVisitDirectory(dir, exc);
                if (potentiallyEmptyFolders.remove(dir)) {
                    URI uri = relativeToBundleRoot(dir.toUri());
                    existingAggregationsToPrune.remove(uri);
                    PathMetadata metadata = aggregates.get(uri);
                    if (metadata == null) {
                        metadata = new PathMetadata();
                        aggregates.put(uri, metadata);              
                    }
                    metadata.setFile(withSlash(dir));
                    metadata.setFolder(withSlash(dir.getParent()));
                    metadata.setProxy();
                    metadata.setCreatedOn(Files.getLastModifiedTime(dir));
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
                potentiallyEmptyFolders.remove(withSlash(file.getParent()));
                if (file.startsWith(MIMETYPE)) {
                    return FileVisitResult.CONTINUE;
                }
                if (manifest.contains(file)) {
                	// Don't aggregate the manifests
                	return FileVisitResult.CONTINUE;
                }
                // super.visitFile(file, attrs);
                URI uri = relativeToBundleRoot(file.toUri());
                existingAggregationsToPrune.remove(uri);
                PathMetadata metadata = aggregates.get(uri);
                if (metadata == null) {
                    metadata = new PathMetadata();
                    aggregates.put(uri, metadata);              
                }
                metadata.setFile(file);
                metadata.setFolder(withSlash(file.getParent()));
                metadata.setProxy();
                metadata.setCreatedOn(Files.getLastModifiedTime(file));
                potentiallyEmptyFolders.remove(file.getParent());
                return FileVisitResult.CONTINUE;
            }
        });
        for (URI preExisting : existingAggregationsToPrune) {
            PathMetadata meta = aggregates.get(preExisting);
            if (meta.getFile() != null) {
                // Don't remove 'virtual' resources, only aggregations
                // that went to files
                aggregates.remove(preExisting);
            }
        }
    }

    public void setAggregates(List<PathMetadata> aggregates) {
        this.aggregates.clear();
        
        for (PathMetadata meta : aggregates) {
            URI uri = null;
            if (meta.getFile() != null) {
                uri = relativeToBundleRoot(meta.getFile().toUri());
            } else if (meta.getUri() != null ){ 
                uri = relativeToBundleRoot(meta.getUri());
            } else {
                uri = relativeToBundleRoot(meta.getProxy());
            }
            if (uri == null) {
                logger.warning("Unknown URI for aggregation " + meta);
                continue; 
            }
            this.aggregates.put(uri, meta);
        }
        
    }

    public void setAnnotations(List<PathAnnotation> annotations) {
        this.annotations = annotations;
    }

    public void setAuthoredBy(List<Agent> authoredBy) {
        if (authoredBy == null) { 
            throw new NullPointerException("authoredBy can't be null");
        }
        this.authoredBy = authoredBy;
    }

    public void setAuthoredOn(FileTime authoredOn) {
        this.authoredOn = authoredOn;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public void setCreatedBy(List<Agent> createdBy) {
        if (createdBy == null) { 
            throw new NullPointerException("createdBy can't be null");
        }
        this.createdBy = createdBy;
    }

    public void setCreatedOn(FileTime createdOn) {
        this.createdOn = createdOn;
    }

    public void setGraph(List<String> graph) {
        this.graph = graph;
    }

    public void setHistory(List<Path> history) {
        if (history == null) {
            throw new NullPointerException("history can't be null");
        }
        this.history = history;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public void setManifest(List<Path> manifest) {
        this.manifest = manifest;
    }

    /**
     * Write as an RO Bundle JSON-LD manifest
     * 
     * @return The path of the written manifest (e.g. ".ro/manifest.json")
     * @throws IOException
     */
    public Path writeAsJsonLD() throws IOException {
        Path jsonld = bundle.getFileSystem().getPath(RO, MANIFEST_JSON);
        Files.createDirectories(jsonld.getParent());
        //Files.createFile(jsonld);
        if (!getManifest().contains(jsonld)) {
            getManifest().add(0, jsonld);
        }
        ObjectMapper om = new ObjectMapper();
        om.addMixInAnnotations(Path.class, PathMixin.class);
        om.addMixInAnnotations(FileTime.class, FileTimeMixin.class);
        om.enable(SerializationFeature.INDENT_OUTPUT);
        om.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        om.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);

        om.setSerializationInclusion(Include.NON_NULL);
        try (Writer w = Files.newBufferedWriter(jsonld,
                Charset.forName("UTF-8"), StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            om.writeValue(w, this);
        }
        return jsonld;
    }

    /**
     * Write as a ODF manifest.xml 
     * 
     * @see http://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-part3.html#__RefHeading__752807_826425813
     * @return The path of the written manifest (e.g. "META-INF/manifest.xml")
     * @throws IOException
     */
    public Path writeAsManifestXML() throws IOException {
    	Path manifestxml = bundle.getFileSystem().getPath(META_INF, MANIFEST_XML);
    	
    	
    	//return manifestxml;
    	
    	
    }
    
    public PathMetadata getAggregation(Path file) {
        URI fileUri = file.toUri();
        return getAggregation(fileUri);
    }

    public PathMetadata getAggregation(URI uri) {
        uri = relativeToBundleRoot(uri);
        PathMetadata metadata = aggregates.get(uri);
        if (metadata == null) {
            metadata = new PathMetadata();
            if (! uri.isAbsolute() && uri.getFragment() == null) {                
                Path path = Bundles.uriToBundlePath(bundle, uri);
                metadata.setFile(path);             
            } else { 
                metadata.setUri(uri);
            }
            aggregates.put(uri, metadata);
        }
        return metadata;
    }

    public URI relativeToBundleRoot(URI uri) {
        uri = ROOT.resolve(bundle.getRoot().toUri().relativize(uri));
        return uri;
    }
}
