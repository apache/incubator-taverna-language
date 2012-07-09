package uk.org.taverna.scufl2.api.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;

public class DataLinkCompareTest {
	@SuppressWarnings("unchecked")
	@Test
	public void expectedOrder() throws Exception {

		Workflow wf = new Workflow();
		wf.setName("wf");
		InputWorkflowPort a = new InputWorkflowPort(wf, "a");
		InputWorkflowPort b = new InputWorkflowPort(wf, "b");
		InputWorkflowPort c = new InputWorkflowPort(wf, "c");

		OutputWorkflowPort x = new OutputWorkflowPort(wf, "x");
		OutputWorkflowPort y = new OutputWorkflowPort(wf, "y");
		OutputWorkflowPort z = new OutputWorkflowPort(wf, "z");

		DataLink c_x = new DataLink(wf, c, x);
		DataLink b_x = new DataLink(wf, b, x);
		DataLink b_z = new DataLink(wf, b, z);
		DataLink a_z = new DataLink(wf, a, z);
		DataLink a_x = new DataLink(wf, a, x);

		ArrayList<DataLink> links = new ArrayList<DataLink>(wf.getDataLinks());
		assertEquals(Arrays.asList(a_x, a_z, b_x, b_z, c_x), links);
		Collections.shuffle(links);
		Collections.sort(links);
		assertEquals(Arrays.asList(a_x, a_z, b_x, b_z, c_x), links);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void nullSupport() throws Exception {

		Workflow wf = new Workflow();
		wf.setName("wf");
		InputWorkflowPort a = new InputWorkflowPort(wf, "a");
		InputWorkflowPort b = new InputWorkflowPort(wf, "b");
		InputWorkflowPort c = new InputWorkflowPort(wf, "c");

		OutputWorkflowPort x = new OutputWorkflowPort(wf, "x");
		OutputWorkflowPort y = new OutputWorkflowPort(wf, "y");
		OutputWorkflowPort z = new OutputWorkflowPort(wf, "z");

		DataLink null_null = new DataLink();
		null_null.setParent(wf);
		// neither receivesFrom nor sendsTo

		DataLink null_z = new DataLink();
		// no receivesFrom
		null_z.setSendsTo(z);
		null_z.setParent(wf);
		DataLink a_z = new DataLink(wf, a, z);
		DataLink a_null = new DataLink();
		// no sendsTo
		a_null.setReceivesFrom(a);
		a_null.setParent(wf);

		ArrayList<DataLink> links = new ArrayList<DataLink>(wf.getDataLinks());
		assertEquals(Arrays.asList(null_null, null_z, a_null, a_z), links);

		DataLink allNull = new DataLink();
		links.add(allNull);

		Collections.shuffle(links);
		Collections.sort(links);
		assertEquals(Arrays.asList(allNull, null_null, null_z, a_null, a_z),
				links);
	}

}
