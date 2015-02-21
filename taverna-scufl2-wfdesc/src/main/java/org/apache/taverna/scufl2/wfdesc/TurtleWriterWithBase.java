package org.apache.taverna.scufl2.wfdesc;

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

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.apache.taverna.scufl2.api.common.URITools;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.turtle.TurtleUtil;
import org.openrdf.rio.turtle.TurtleWriter;
import org.openrdf.sail.memory.model.MemURI;


public class TurtleWriterWithBase extends TurtleWriter {
	private URITools uriTools = new URITools();
	private final URI baseURI;

	TurtleWriterWithBase(OutputStream out, URI baseURI) {
		super(out);
		this.baseURI = baseURI;
	}

	@Override
	public void startRDF() throws RDFHandlerException {
		super.startRDF();
		try {
			writeBase();
		} catch (IOException e) {
			throw new RDFHandlerException(e);
		}
	}

	@Override
	protected void writeURI(org.openrdf.model.URI uri) throws IOException {

		final String uriString = uriTools.relativePath(baseURI,
				URI.create(uri.toString())).toASCIIString();
		super.writeURI(new MemURI(null, uriString, ""));
	}

	protected void writeBase() throws IOException {
		writer.write("@base ");
		writer.write("<");
		writer.write(TurtleUtil.encodeURIString(baseURI.toASCIIString()));
		writer.write("> .");
		writer.writeEOL();
	}
	
	
}
