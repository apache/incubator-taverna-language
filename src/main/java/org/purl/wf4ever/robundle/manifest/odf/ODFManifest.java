package org.purl.wf4ever.robundle.manifest.odf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
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

import oasis.names.tc.opendocument.xmlns.manifest._1.FileEntry;
import oasis.names.tc.opendocument.xmlns.manifest._1.Manifest;
import oasis.names.tc.opendocument.xmlns.manifest._1.ObjectFactory;

import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.manifest.PathMetadata;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

public class ODFManifest {

	public static class ManifestNamespacePrefixMapperJAXB_RI extends
			NamespacePrefixMapper {
		@Override
		public String[] getPreDeclaredNamespaceUris() {
			// TODO Auto-generated method stub
			return super.getPreDeclaredNamespaceUris();
		}

		@Override
		public String getPreferredPrefix(String namespaceUri,
				String suggestion, boolean requirePrefix) {
			if (namespaceUri
					.equals("urn:oasis:names:tc:opendocument:xmlns:manifest:1.0")) {
				return "manifest";
			}
			return suggestion;
		}

	}

	private static final String ODF_MANIFEST_VERSION = "1.2";
	public static final String CONTAINER_XML = "META-INF/container.xml";
	public static final String MANIFEST_XML = "META-INF/manifest.xml";

	private static Logger logger = Logger.getLogger(ODFManifest.class
			.getCanonicalName());

	private static JAXBContext jaxbContext;
	public static boolean containsManifest(Bundle bundle) {
		return Files.isRegularFile(manifestXmlPath(bundle));
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
					.newInstance(oasis.names.tc.opendocument.xmlns.manifest._1.ObjectFactory.class
					// ,
					// org.oasis_open.names.tc.opendocument.xmlns.container.ObjectFactory.class,
					// org.w3._2000._09.xmldsig_.ObjectFactory.class,
					// org.w3._2001._04.xmlenc_.ObjectFactory.class
					);
		}
		return jaxbContext;
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

	private org.purl.wf4ever.robundle.manifest.Manifest manifest;

	private Bundle bundle;

	// protected void prepareContainerXML() throws IOException {
	//
	//
	// /* Check if we should prune <rootFiles> */
	// Iterator<Object> iterator = containerXml.getValue().getRootFilesOrAny()
	// .iterator();
	// boolean foundAlready = false;
	// while (iterator.hasNext()) {
	// Object anyOrRoot = iterator.next();
	// if (!(anyOrRoot instanceof JAXBElement)) {
	// continue;
	// }
	// @SuppressWarnings("rawtypes")
	// JAXBElement elem = (JAXBElement) anyOrRoot;
	// if (!elem.getDeclaredType().equals(RootFiles.class)) {
	// continue;
	// }
	// RootFiles rootFiles = (RootFiles) elem.getValue();
	// if (foundAlready
	// || (rootFiles.getOtherAttributes().isEmpty() && rootFiles
	// .getAnyOrRootFile().isEmpty())) {
	// // Delete it!
	// System.err.println("Deleting unneccessary <rootFiles>");
	// iterator.remove();
	// }
	// foundAlready = true;
	// }
	//
	// Marshaller marshaller;
	// OutputStream outStream = null;
	// try {
	// marshaller = createMarshaller();
	// // XMLStreamWriter xmlStreamWriter = XMLOutputFactory
	// // .newInstance().createXMLStreamWriter(outStream);
	// // xmlStreamWriter.setDefaultNamespace(containerElem.getName()
	// // .getNamespaceURI());
	// //
	// // xmlStreamWriter.setPrefix("dsig",
	// // "http://www.w3.org/2000/09/xmldsig#");
	// // xmlStreamWriter.setPrefix("xmlenc",
	// // "http://www.w3.org/2001/04/xmlenc#");
	// outStream = odfPackage.insertOutputStream(CONTAINER_XML);
	//
	// // FIXME: Set namespace prefixes and default namespace
	//
	// marshaller.setProperty("jaxb.formatted.output", true);
	//
	// // TODO: Ensure using default namespace
	// marshaller.marshal(containerXml, outStream);
	//
	// } catch (IOException e) {
	// throw e;
	// } catch (Exception e) {
	// throw new IOException("Could not parse " + CONTAINER_XML, e);
	// } finally {
	// if (outStream != null) {
	// outStream.close();
	// }
	// }
	// }
	//
	// @SuppressWarnings("unchecked")
	// protected void parseContainerXML() throws IOException {
	// createdContainerXml = false;
	// InputStream containerStream = getResourceAsInputStream(CONTAINER_XML);
	// if (containerStream == null) {
	// // Make an empty containerXml
	// Container container = containerFactory.createContainer();
	// containerXml = containerFactory.createContainer(container);
	// createdContainerXml = true;
	// return;
	// }
	// try {
	// Unmarshaller unMarshaller = createUnMarshaller();
	// containerXml = (JAXBElement<Container>) unMarshaller
	// .unmarshal(containerStream);
	// } catch (JAXBException e) {
	// throw new IOException("Could not parse " + CONTAINER_XML, e);
	// }
	//
	// }

	private ObjectFactory manifestFactory = new oasis.names.tc.opendocument.xmlns.manifest._1.ObjectFactory();

	private static boolean warnedPrefixMapper;

	public ODFManifest(org.purl.wf4ever.robundle.manifest.Manifest manifest) {
		this.manifest = manifest;
		this.bundle = manifest.getBundle();
	}

	public Path createManifestXML() throws IOException {
		Manifest odfManifest = manifestFactory.createManifest();
		odfManifest.setVersion(ODF_MANIFEST_VERSION);
		for (PathMetadata pathMetadata : manifest.getAggregates()) {
			Path path = pathMetadata.getFile();
			if (path == null) {
				logger.finest("Skipping non-path entry "
						+ pathMetadata.getUri());
			}
			// if (! Files.isRegularFile(path)) {
			// logger.fine("Not adding " + path +
			// " to  manifest, not a regular file");
			// }
			FileEntry entry = manifestFactory.createFileEntry();
			entry.setFullPath(bundle.getRoot().relativize(path).toString());
			if (pathMetadata.getMediatype() != null) {
				entry.setMediaType(pathMetadata.getMediatype());
			} else {
				entry.setMediaType("application/octet-stream");
			}

			try {
				entry.setSize(BigInteger.valueOf(Files.size(path)));
			} catch (IOException e) {
				logger.log(Level.WARNING, "Can't find size of " + path, e);
			}
			if (pathMetadata.getConformsTo() != null) {
				entry.setVersion(pathMetadata.getConformsTo().toString());
			}
			odfManifest.getFileEntry().add(entry);
		}
		Path manifestXml = manifestXmlPath(bundle);
		Files.createDirectories(manifestXml.getParent());
		try (OutputStream outStream = Files.newOutputStream(manifestXml)) {
			try {
				createMarshaller().marshal(odfManifest, outStream);
			} catch (JAXBException e) {
				throw new RuntimeException("Could not serialize ODF Manifest",
						e);
			}
		}
		return manifestXml;

	}

	public void readManifestXML() throws IOException {
		Path manifestXml = manifestXmlPath(bundle);
		Manifest odfManifest;
		try (InputStream inStream = Files.newInputStream(manifestXml)) {
			odfManifest = (Manifest) createUnMarshaller().unmarshal(inStream);
		} catch (JAXBException e) {
			// logger.warning("Could not parse " + manifestXml);
			throw new IOException("Could not parse " + manifestXml, e);
		}
		if (!manifest.getManifest().contains(manifestXml)) {
			manifest.getManifest().add(manifestXml);
		}
		for (FileEntry f : odfManifest.getFileEntry()) {
			Path path = bundle.getRoot().resolve(f.getFullPath());
			if (!Files.exists(path)) {
				logger.warning(MANIFEST_XML + " listed " + path
						+ ", but it does not exist in bundle");
				continue;
			}
			PathMetadata metadata = manifest.getAggregation(path);
			if (f.getMediaType() != null && f.getMediaType().contains("/")) {
				metadata.setMediatype(f.getMediaType());
			}
			if (f.getEncryptionData() != null) {
				logger.warning("Unsupported encryption for " + path);
				continue;
			}
			if (f.getVersion() != null) {
				try {
					metadata.setConformsTo(new URI(f.getVersion()));
				} catch (URISyntaxException e) {
					logger.warning("Ignoring unsupported version "
							+ f.getVersion());
				}
			}
			if (Files.isRegularFile(path) && f.getSize() != null) {
				long actualSize = Files.size(path);
				long expectedSize = f.getSize().longValue();
				if (expectedSize != actualSize) {
					logger.warning("Wrong file size for " + path
							+ ", expected: " + expectedSize + ", actually: "
							+ actualSize);

				}
			}
		}
	}

}
