/**
 * 
 */
package org.apache.taverna.scufl2.translator.scufl;
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


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleReader;


/**
 * @author alanrw
 */
public class ScuflReader implements WorkflowBundleReader {
	public static final String APPLICATION_VND_TAVERNA_SCUFL_XML = "application/vnd.taverna.scufl+xml";
	private static final Set<String> SCUFL_TYPES = new HashSet<String>(
			Arrays.asList(new String[] { APPLICATION_VND_TAVERNA_SCUFL_XML }));

	private Scufl2Tools scufl2Tools = new Scufl2Tools();
	private ScuflParser parser;
	
	@Override
	public Set<String> getMediaTypes() {
		return SCUFL_TYPES;
	}

	@Override
	public WorkflowBundle readBundle(File bundleFile, String mediaType)
			throws ReaderException, IOException {
		try {
			WorkflowBundle bundle = getParser().parseScufl(bundleFile);
			scufl2Tools.setParents(bundle);
			return bundle;
		} catch (JAXBException e) {
			if (e.getCause() instanceof IOException) {
				IOException ioException = (IOException) e.getCause();
				throw ioException;
			}
			throw new ReaderException("Can't parse SCUFL " + bundleFile, e);
		}
	}

	@Override
	public WorkflowBundle readBundle(InputStream inputStream, String mediaType)
			throws ReaderException, IOException {
		try {
			WorkflowBundle bundle = getParser().parseScufl(inputStream);
			scufl2Tools.setParents(bundle);
			return bundle;
		} catch (JAXBException e) {
			if (e.getCause() instanceof IOException) {
				IOException ioException = (IOException) e.getCause();
				throw ioException;
			}
			throw new ReaderException("Can't parse SCUFL", e);
		}
	}

	public void setParser(ScuflParser parser) {
		this.parser = parser;
	}
	
	public ScuflParser getParser() throws JAXBException {
		if (parser == null)
			parser = new ScuflParser();
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
		if (!asLatin1.contains("http://org.embl.ebi.escience/xscufl/0.1alpha"))
			return null;
		// Good enough - XML is hard to check on so few bytes
		return APPLICATION_VND_TAVERNA_SCUFL_XML;
	}
}
