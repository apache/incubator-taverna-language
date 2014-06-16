package org.purl.wf4ever.robundle.manifest.combine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.identifiers.combine_specifications.omex_manifest.Content;
import org.identifiers.combine_specifications.omex_manifest.ObjectFactory;
import org.identifiers.combine_specifications.omex_manifest.OmexManifest;
import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.manifest.PathMetadata;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * Parse and generate COMBINE Archive OMEX manifest
 * 
 * 
 * @author Stian Soiland-Reyes
 *
 */
public class CombineManifest {

	private static final URI OMEX_METADATA = URI
			.create("http://identifiers.org/combine.specifications/omex-metadata");

	private static final String MANIFEST_XML = "manifest.xml";

	public static class ManifestNamespacePrefixMapperJAXB_RI extends
			NamespacePrefixMapper {
		@Override
		public String getPreferredPrefix(String namespaceUri,
				String suggestion, boolean requirePrefix) {
			if (namespaceUri.equals(OMEX_METADATA.toString())) {
				return "";
			}
			return suggestion;
		}

		@Override
		public String[] getPreDeclaredNamespaceUris() {
			return new String[] { OMEX_METADATA.toString() };
		}

	}

	private static Logger logger = Logger.getLogger(CombineManifest.class
			.getCanonicalName());

	private org.purl.wf4ever.robundle.manifest.Manifest manifest;
	private Bundle bundle;
	private static boolean warnedPrefixMapper;

	private static ObjectFactory objectFactory = new ObjectFactory();

	private static JAXBContext jaxbContext;

	public CombineManifest(org.purl.wf4ever.robundle.manifest.Manifest manifest) {
		this.manifest = manifest;
		this.bundle = manifest.getBundle();
	}

	private OmexManifest makeOmexManifest() {
		Path manifestXml = bundle.getRoot().resolve("manifest.xml");		
		OmexManifest omexManifest = objectFactory.createOmexManifest();

		PathMetadata aggr = manifest.getAggregation(manifestXml);
		if (aggr.getConformsTo() == null) {
			// Add the manifest itself
			aggr.setConformsTo(URI
					.create("http://identifiers.org/combine.specifications/omex-manifest"));
		}

		for (PathMetadata metadata : manifest.getAggregates()) {
			Content content = objectFactory.createContent();
			Path file = metadata.getFile();

			if (file == null) {
				content.setLocation(metadata.getUri().toString());
			} else {
				Path relPath = metadata.getFile().relativize(bundle.getRoot());
				content.setLocation("./" + relPath);
			}
			if (metadata.getMediatype() != null
					&& !metadata.getMediatype().isEmpty()) {
				content.setFormat(metadata.getMediatype());
			} else if (metadata.getConformsTo() != null) {
				content.setFormat(metadata.getConformsTo().toString());
			} else {
				// Binary fallback as 'format' is required attribute
				content.setFormat("application/octet-stream");
			}

			// TODO: Handle 'master' attribute

			omexManifest.getContent().add(content);

		}
		// TODO: Should we add .ro/manifest.json and .ro/* ?
		return omexManifest;
	}

	public Path createManifestXML() throws IOException {
		OmexManifest omexManifest = makeOmexManifest();
		
		Path manifestXml = manifestXmlPath(bundle);
		Files.createDirectories(manifestXml.getParent());
		try (OutputStream outStream = Files.newOutputStream(manifestXml)) {
			try {
				createMarshaller().marshal(omexManifest, outStream);
			} catch (JAXBException e) {
				throw new RuntimeException("Could not serialize OMEX Manifest",
						e);
			}
		}
		return manifestXml;
	}

	public void readManifestXML() throws IOException {
		Path manifestXml = manifestXmlPath(bundle);
		OmexManifest omexManifest;
		try (InputStream inStream = Files.newInputStream(manifestXml)) {
			omexManifest = (OmexManifest) createUnMarshaller().unmarshal(inStream);
		} catch (JAXBException e) {
			// logger.warning("Could not parse " + manifestXml);
			throw new IOException("Could not parse " + manifestXml, e);
		}
		if (!manifest.getManifest().contains(manifestXml)) {
			manifest.getManifest().add(manifestXml);
		}
		for (Content c : omexManifest.getContent()) {
			PathMetadata metadata;
			if (c.getLocation().contains(":")) {
				try { 
					URI uri = new URI(c.getLocation());
					if (uri.isAbsolute()) { 
						metadata = manifest.getAggregation(uri);
					} else { 
						logger.warning("Not an absolute URI, but contains :" + c.getLocation());
						continue;
					} 
				} catch (URISyntaxException e) {
					logger.warning("Invalid URI " + c.getLocation());
					continue; 
				}
			} else { 			
				Path path = bundle.getRoot().resolve(c.getLocation());
				if (Files.exists(path)) {
					metadata = manifest.getAggregation(path);
				} else {
					logger.warning(MANIFEST_XML + " listed relative path " + path
							+ ", but it does not exist in bundle");
					continue;
				}
			}
			
			// Format - is it an URI or media type?
			if (c.getFormat().contains(":")) { 
				metadata.setConformsTo(URI.create(c.getFormat()));
			} else if (! c.getFormat().isEmpty()) {			
				metadata.setMediatype(c.getFormat());
			} else if (metadata.getFile() != null) { 
				metadata.setMediatype(manifest.guessMediaType(metadata.getFile()));
			} // else: Not needed for URIs
		}
	}

	private static Path manifestXmlPath(Bundle bundle) {
		return bundle.getRoot().resolve(MANIFEST_XML);
	}

	protected static void setPrefixMapper(Marshaller marshaller) {
		boolean setPrefixMapper = false;

		try {
			// This only works with JAXB RI, in which case we can set the
			// namespace prefix mapper
			Class.forName("com.sun.xml.bind.marshaller.NamespacePrefixMapper");
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper",
					new ManifestNamespacePrefixMapperJAXB_RI());
			// Note: A similar mapper for the built-in java
			// (com.sun.xml.bind.internal.namespacePrefixMapper)
			// is no longer included here, as it will not (easily) compile with
			// Maven.
			setPrefixMapper = true;
		} catch (Exception e) {
			logger.log(Level.FINE, "Can't find NamespacePrefixMapper", e);
		}

		if (!setPrefixMapper && !warnedPrefixMapper) {
			logger.info("Could not set prefix mapper (missing or incompatible JAXB) "
					+ "- will use prefixes ns0, ns1, ..");
			warnedPrefixMapper = true;
		}
	}

	protected static synchronized Marshaller createMarshaller()
			throws JAXBException {
		Marshaller marshaller = getJaxbContext().createMarshaller();
		setPrefixMapper(marshaller);
		return marshaller;
	}

	protected static synchronized Unmarshaller createUnMarshaller()
			throws JAXBException {
		Unmarshaller unmarshaller = getJaxbContext().createUnmarshaller();
		return unmarshaller;
	}

	protected static synchronized JAXBContext getJaxbContext()
			throws JAXBException {
		if (jaxbContext == null) {
			jaxbContext = JAXBContext
					.newInstance(org.identifiers.combine_specifications.omex_manifest.ObjectFactory.class);
		}
		return jaxbContext;
	}

	public static boolean containsManifest(Bundle bundle) {
		return Files.isRegularFile(manifestXmlPath(bundle));
	}

}
