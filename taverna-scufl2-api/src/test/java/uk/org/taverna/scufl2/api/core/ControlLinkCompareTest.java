package uk.org.taverna.scufl2.api.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class ControlLinkCompareTest {
	@SuppressWarnings("unchecked")
	@Test
	public void expectedOrder() throws Exception {

		Workflow wf = new Workflow();		
		wf.setName("wf");
		
		Processor a = new Processor(wf, "a");
		Processor b = new Processor(wf, "b");
		Processor c = new Processor(wf, "c");
		Processor d = new Processor(wf, "d");

		BlockingControlLink b_blocks_c = new BlockingControlLink(c, b);
		BlockingControlLink a_blocks_c = new BlockingControlLink(c, a);
		BlockingControlLink a_blocks_b = new BlockingControlLink(b, a);
		BlockingControlLink b_blocks_d = new BlockingControlLink(d, b);
		BlockingControlLink a_blocks_d = new BlockingControlLink(d, a);
		
		ArrayList<ControlLink> links = new ArrayList<ControlLink>(wf.getControlLinks());
		assertEquals(Arrays.asList(a_blocks_b, a_blocks_c, a_blocks_d, b_blocks_c, b_blocks_d), links);
		Collections.shuffle(links);
		Collections.sort(links);
		assertEquals(Arrays.asList(a_blocks_b, a_blocks_c, a_blocks_d, b_blocks_c, b_blocks_d), links);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void nullSupport() throws Exception {
		Workflow wf = new Workflow();		
		wf.setName("wf");
		
		
		
		Processor a = new Processor(wf, "a");
		Processor b = new Processor(wf, "b");
		Processor c = new Processor(wf, "c");
		Processor d = new Processor(wf, "d");

		BlockingControlLink b_blocks_c = new BlockingControlLink(c, b);
		BlockingControlLink null_blocks_c = new BlockingControlLink();
		null_blocks_c.setBlock(c);
		null_blocks_c.setParent(wf);
		BlockingControlLink a_blocks_b = new BlockingControlLink(b, a);
		BlockingControlLink b_blocks_null = new BlockingControlLink();
		b_blocks_null.setUntilFinished(b);
		b_blocks_null.setParent(wf);
		
		BlockingControlLink null_blocks_null = new BlockingControlLink();		
		null_blocks_null.setParent(wf);
		
		
		ArrayList<ControlLink> links = new ArrayList<ControlLink>(wf.getControlLinks());
		assertEquals(Arrays.asList(null_blocks_null, null_blocks_c, a_blocks_b, b_blocks_null, b_blocks_c), links);				

		Collections.shuffle(links);		
		Collections.sort(links);
		
		BlockingControlLink a_blocks_d_no_parent = new BlockingControlLink();
		a_blocks_d_no_parent.setBlock(d);
		a_blocks_d_no_parent.setUntilFinished(a);
		// no setParent
		links.add(a_blocks_d_no_parent);
		Collections.shuffle(links);		
		Collections.sort(links);
		
		assertEquals(Arrays.asList(null_blocks_null, null_blocks_c, a_blocks_b, a_blocks_d_no_parent, b_blocks_null, b_blocks_c), links);		
	}

}
