package org.purl.wf4ever.wfdesc.scufl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;
import org.purl.wf4ever.wfdesc.scufl2.ConvertToWfdesc;

import static org.junit.Assert.*;

public class TestConvertToWfdesc {
	private static final boolean DEBUG = false;
	private static final String HELLOANYONE_T2FLOW = "helloanyone.t2flow";
	private static final String HELLOWORLD_T2FLOW = "helloworld.t2flow";
	private File helloworldT2flow;
	private File helloanyoneT2flow;
	private File helloworldWfdesc;
	private File helloanyoneWfdesc;
	private InputStream origIn;
	private PrintStream origOut;
	private PrintStream origErr;
	
	@Before
	public void copyT2flow() throws IOException {
		File tempdir = File.createTempFile("scufl2-wfdesc", "test");
		tempdir.delete();
		assertTrue(tempdir.mkdir());
		if (DEBUG) {
			System.out.println("Copying to " + tempdir);
		}
		
		helloworldT2flow = new File(tempdir, HELLOWORLD_T2FLOW);
		FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/" + HELLOWORLD_T2FLOW), helloworldT2flow);
		
		helloanyoneT2flow = new File(tempdir, HELLOANYONE_T2FLOW);
		FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/" + HELLOANYONE_T2FLOW), helloanyoneT2flow);
		
		helloworldWfdesc = new File(tempdir, "helloworld.wfdesc.ttl");
		
		helloanyoneWfdesc = new File(tempdir, "helloanyone.wfdesc.ttl");
	}
	
	@After
	public void deleteTemp() throws IOException {
		if (DEBUG) {
			return;
		}
		for (File f : new File[] { helloworldT2flow, helloanyoneT2flow,
				helloworldWfdesc, helloanyoneWfdesc,
				helloworldT2flow.getParentFile() }) {
			f.delete();
		}		
	}
	
	@Before
	public void origStd() {
		origIn = System.in;
		origOut = System.out;
		origErr = System.err;
	}
	
	@After
	public void restoreStd() {
		System.setIn(origIn);
		System.setOut(origOut);
		System.setErr(origErr);	
	}
	
	@Test
	public void help() throws Exception {
		help("-h");
		help("--help");
	}
	
	private void help(String argName) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream outBuf = new PrintStream(out);
		
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		PrintStream errBuf = new PrintStream(err);
		
		try {
			System.setOut(outBuf);
			System.setErr(errBuf);	
			ConvertToWfdesc.main(new String[]{argName});
		} finally {
			restoreStd();
		}
		out.flush();
		out.close();
		err.flush();
		err.close();
		
		assertEquals(0, err.size());		
		String help = out.toString("utf-8");
//		System.out.println(help);
		assertTrue(help.contains("scufl2-to-wfdesc"));
		assertTrue(help.contains("\nIf no arguments are given"));
	}
	
	@Test
	public void stdin() throws Exception {
		byte[] input = IOUtils.toByteArray(getClass().getResourceAsStream("/" + HELLOWORLD_T2FLOW));
		assertTrue(input.length > 0);
		InputStream in = new ByteArrayInputStream(input);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream outBuf = new PrintStream(out);
		try {
			System.setIn(in);
			System.setOut(outBuf);		
			ConvertToWfdesc.main(new String[]{});
		} finally {
			restoreStd();
		}
		out.flush();
		out.close();
		String turtle = out.toString("utf-8");
		//System.out.println(turtle);
		assertTrue(turtle.contains("Hello_World"));
		assertTrue(turtle.contains("processor/hello/out/value"));
	}
	
	@Test
	public void convert() throws Exception {		
		assertFalse(helloworldWfdesc.exists());
		assertFalse(helloanyoneWfdesc.exists());
		ConvertToWfdesc.main(new String[]{helloworldT2flow.getAbsolutePath(), helloanyoneT2flow.getAbsolutePath() });
		assertTrue(helloworldWfdesc.exists());
		assertTrue(helloanyoneWfdesc.exists());
		
		Repository myRepository = new SailRepository(new MemoryStore());
		myRepository.initialize();
		RepositoryConnection con = myRepository.getConnection();
		con.add(helloworldWfdesc, helloworldWfdesc.toURI().toASCIIString(), RDFFormat.TURTLE);
//		assertTrue(con.prepareTupleQuery(QueryLanguage.SPARQL,
		assertTrue(con.prepareBooleanQuery(QueryLanguage.SPARQL, 
				"PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#>  " +
				"ASK { " +
				"?wf a wfdesc:Workflow, wfdesc:Process ;" +
				"  wfdesc:hasOutput ?greeting; " +
				"  wfdesc:hasDataLink ?link; " +
				"  wfdesc:hasSubProcess ?hello . " +
				"?hello a wfdesc:Process ;" +
				"  wfdesc:hasOutput ?value . " +
				"?greeting a wfdesc:Output . " +
				"?value a wfdesc:Output . " +
				"?link a wfdesc:DataLink ; " +
				"  wfdesc:hasSource ?value ; " +
				"  wfdesc:hasSink ?greeting . " +				
			    "}").evaluate());
		
	}
	
	
	
}
