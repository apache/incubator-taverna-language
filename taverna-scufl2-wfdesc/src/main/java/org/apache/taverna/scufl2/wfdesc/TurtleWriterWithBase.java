package org.apache.taverna.scufl2.wfdesc;

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