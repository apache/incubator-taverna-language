package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;

public class MergeParsingTest {

	private static final String MERGE_FUN = "/merge_fun.t2flow";
	private static final String MERGE_THEN_DATAFLOW = "/merge_then_dataflow_link.t2flow";
	private static final String DATAFLOW_THEN_MERGE = "/dataflow_link_then_merge.t2flow";
	private static final String MISSING_MERGE = "/missing_merge.t2flow";

	@Test
	public void mergeFun() throws Exception {
		URL wfResource = getClass().getResource(MERGE_FUN);
		assertNotNull("Could not find workflow " + MERGE_FUN, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(true);
		WorkflowBundle researchObj = parser.parseT2Flow(wfResource.openStream());		
	}
	
	@Test(expected=ReaderException.class)
	public void mergeThenDataflow() throws Exception {
		URL wfResource = getClass().getResource(MERGE_THEN_DATAFLOW);
		assertNotNull("Could not find workflow " + MERGE_THEN_DATAFLOW, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(true);
		parser.parseT2Flow(wfResource.openStream());
	}
	
	@Test(expected=ReaderException.class)
	public void dataflowThenMerge() throws Exception {
		URL wfResource = getClass().getResource(DATAFLOW_THEN_MERGE);
		assertNotNull("Could not find workflow " + DATAFLOW_THEN_MERGE, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(true);
		parser.parseT2Flow(wfResource.openStream());
	}
	
	@Test(expected=ReaderException.class)
	public void missingMerge() throws Exception {
		URL wfResource = getClass().getResource(MISSING_MERGE);
		assertNotNull("Could not find workflow " + MISSING_MERGE, wfResource);
		T2FlowParser parser = new T2FlowParser();
		parser.setStrict(true);
		parser.parseT2Flow(wfResource.openStream());
	}
	
	
}
