package org.apache.taverna.robundle.manifest.odf;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.isRegularFile;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.newOutputStream;
import static java.nio.file.Files.size;
import static java.util.logging.Level.WARNING;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.manifest.PathMetadata;

import org.apache.taverna.robundle.xml.odf.manifest.FileEntry;
import org.apache.taverna.robundle.xml.odf.manifest.Manifest;

public class ODFManifest extends ODFJaxb {

	private static Logger logger = Logger.getLogger(ODFManifest.class
			.getCanonicalName());
	
	public static final String CONTAINER_XML = "META-INF/container.xml";
	
	public static final String MANIFEST_XML = "META-INF/manifest.xml";

	private static final String ODF_MANIFEST_VERSION = "1.2";

	public static boolean containsManifest(Bundle bundle) {
		return isRegularFile(manifestXmlPath(bundle));
	}

	private static Path manifestXmlPath(Bundle bundle) {
		return bundle.getRoot().resolve(MANIFEST_XML);
	}

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

	private org.apache.taverna.robundle.manifest.Manifest manifest;

	public ODFManifest(org.apache.taverna.robundle.manifest.Manifest manifest) {
		this.manifest = manifest;
		this.bundle = manifest.getBundle();
	}

	public Path createManifestXML() throws IOException {
		Manifest odfManifest = manifestFactory.createManifest();
		odfManifest.setVersion(ODF_MANIFEST_VERSION);
		for (PathMetadata pathMetadata : manifest.getAggregates()) {
			Path path = pathMetadata.getFile();
			if (path == null)
				logger.finest("Skipping non-path entry "
						+ pathMetadata.getUri());
			// if (! Files.isRegularFile(path)) {
			// logger.fine("Not adding " + path +
			// " to  manifest, not a regular file");
			// }
			FileEntry entry = manifestFactory.createFileEntry();
			entry.setFullPath(bundle.getRoot().relativize(path).toString());
			if (pathMetadata.getMediatype() != null)
				entry.setMediaType(pathMetadata.getMediatype());
			else
				entry.setMediaType("application/octet-stream");

			try {
				entry.setSize(BigInteger.valueOf(size(path)));
			} catch (IOException e) {
				logger.log(WARNING, "Can't find size of " + path, e);
			}
			if (pathMetadata.getConformsTo() != null)
				entry.setVersion(pathMetadata.getConformsTo().toString());
			odfManifest.getFileEntry().add(entry);
		}
		Path manifestXml = manifestXmlPath(bundle);
		createDirectories(manifestXml.getParent());
		try (OutputStream outStream = newOutputStream(manifestXml)) {
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
		try (InputStream inStream = newInputStream(manifestXml)) {
			JAXBElement<Manifest> element = (JAXBElement<Manifest>) createUnMarshaller().unmarshal(inStream);
			odfManifest = element.getValue();
		} catch (JAXBException e) {
			// logger.warning("Could not parse " + manifestXml);
			throw new IOException("Could not parse " + manifestXml, e);
		}
		if (!manifest.getManifest().contains(manifestXml))
			manifest.getManifest().add(manifestXml);
		for (FileEntry f : odfManifest.getFileEntry()) {
			Path path = bundle.getRoot().resolve(f.getFullPath());
			if (!exists(path)) {
				logger.warning(MANIFEST_XML + " listed " + path
						+ ", but it does not exist in bundle");
				continue;
			}
			PathMetadata metadata = manifest.getAggregation(path);
			if (f.getMediaType() != null && f.getMediaType().contains("/"))
				metadata.setMediatype(f.getMediaType());
			if (f.getEncryptionData() != null) {
				logger.warning("Unsupported encryption for " + path);
				continue;
			}
			try {
				if (f.getVersion() != null)
					metadata.setConformsTo(new URI(f.getVersion()));
			} catch (URISyntaxException e) {
				logger.warning("Ignoring unsupported version " + f.getVersion());
			}
			if (isRegularFile(path) && f.getSize() != null) {
				long actualSize = size(path);
				long expectedSize = f.getSize().longValue();
				if (expectedSize != actualSize)
					logger.warning("Wrong file size for " + path
							+ ", expected: " + expectedSize + ", actually: "
							+ actualSize);
			}
		}
	}

}
