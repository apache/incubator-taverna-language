package org.apache.taverna.scufl2.translator.t2flow;
/*
 *
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
 *
*/


import static java.util.Collections.singleton;
import static org.apache.taverna.scufl2.api.container.WorkflowBundle.WORKFLOW_BUNDLE_ROOT;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleReader;


public class T2FlowReader implements WorkflowBundleReader {
	private final class DelayedCloseBufferedInputStream extends
			BufferedInputStream {
		private DelayedCloseBufferedInputStream(InputStream in, int size) {
			super(in, size);
		}

		@Override
		public void close() throws IOException {
			return;
		}

		public void reallyClose() throws IOException {
			super.close();
		}
	}

	protected static final URI PROV = URI.create("http://www.w3.org/ns/prov#");
	protected static final URI HAD_ORIGINAL_SOURCE = PROV.resolve("#hadOriginalSource");
	protected static final URI ENTITY = PROV.resolve("#Entity");
	private static final int MEGABYTE = 1024 * 1024;
	private static final int PRESERVE_ORIGINAL_MAXIMUM_BYTES = 5 * MEGABYTE;
	public static final String APPLICATION_VND_TAVERNA_T2FLOW_XML = "application/vnd.taverna.t2flow+xml";

	private T2FlowParser parser;
	private Scufl2Tools scufl2Tools = new Scufl2Tools();
	private URITools uriTools = new URITools();
	
	@Override
	public Set<String> getMediaTypes() {
		return singleton(APPLICATION_VND_TAVERNA_T2FLOW_XML);
	}

	@Override
	public WorkflowBundle readBundle(File bundleFile, String mediaType)
			throws ReaderException, IOException {
		try {
			WorkflowBundle bundle = getParser().parseT2Flow(bundleFile);
			scufl2Tools.setParents(bundle);
			preserveOriginal(bundle, new FileInputStream(bundleFile));
			return bundle;
		} catch (JAXBException e) {
			if (e.getCause() instanceof IOException) {
				IOException ioException = (IOException) e.getCause();
				throw ioException;
			}
			throw new ReaderException("Can't parse t2flow " + bundleFile, e);
		}
	}

	protected void preserveOriginal(WorkflowBundle bundle,
			InputStream t2flowStream) throws IOException {
		String path = "history/" + bundleUUID(bundle) + ".t2flow";
		bundle.getResources().addResource(t2flowStream, path,
				APPLICATION_VND_TAVERNA_T2FLOW_XML);
		// TODO: Add annotation recording the provenance of this history file
//		PropertyResource original = new PropertyResource();
//		original.setTypeURI(ENTITY);
//		original.setResourceURI(URI.create("../../" + path));
//		// TODO: dcterms:format ?				
//		// TODO: Add this annotation to the workflow bundle rather than main workflow
//		bundle.getMainWorkflow().getCurrentRevision().addProperty(HAD_ORIGINAL_SOURCE, original);		
	}

	protected String bundleUUID(WorkflowBundle bundle) {
		String uuidPath = uriTools.relativePath(WORKFLOW_BUNDLE_ROOT,
				bundle.getGlobalBaseURI()).getPath();
		UUID uuid = UUID
				.fromString(uuidPath.substring(0, uuidPath.length() - 1));
		return uuid.toString();
	}

	@Override
	public WorkflowBundle readBundle(InputStream inputStream, String mediaType)
			throws ReaderException, IOException {
		DelayedCloseBufferedInputStream buffered = new DelayedCloseBufferedInputStream(
				inputStream, PRESERVE_ORIGINAL_MAXIMUM_BYTES);
		try {
			buffered.mark(PRESERVE_ORIGINAL_MAXIMUM_BYTES);
			WorkflowBundle bundle;
			try {
				bundle = getParser().parseT2Flow(buffered);
				scufl2Tools.setParents(bundle);
			} catch (JAXBException e) {
				if (e.getCause() instanceof IOException) {
					IOException ioException = (IOException) e.getCause();
					throw ioException;
				}
				throw new ReaderException("Can't parse t2flow", e);
			}

			// Rewind and try to copy out the original t2flow
			try {
				buffered.reset();
			} catch (IOException ex) {
				// Too big workflow, can't preserve it.
				// TODO: Store to Bundle first?
				// FIXME: Can we close BufferedInputStream without closing
				// inputStream from our parameter?
				return bundle;
			}
			preserveOriginal(bundle, buffered);
			return bundle;
		} finally {
			buffered.reallyClose();
		}
	}

	public void setParser(T2FlowParser parser) {
		this.parser = parser;
	}

	public T2FlowParser getParser() throws JAXBException {
		if (parser == null)
			parser = new T2FlowParser();
		return parser;
	}
	
	@Override
	public String guessMediaTypeForSignature(byte[] firstBytes) {
		if (firstBytes.length < 100)
			return null;
		// FIXME: Does not deal with potential UTF-16 encoding
		
		// Latin 1 can deal with nasty bytes in binaries
		Charset latin1 = Charset.forName("ISO-8859-1");
		String asLatin1 = new String(firstBytes, latin1);
		if (!asLatin1.contains("workflow"))
			return null;
		if (!asLatin1.contains("http://taverna.sf.net/2008/xml/t2flow"))
			return null;
		if (!asLatin1.contains("dataflow"))
			return null;
		// Good enough - XML is hard to check on so few bytes		
		return APPLICATION_VND_TAVERNA_T2FLOW_XML;
	}
}
