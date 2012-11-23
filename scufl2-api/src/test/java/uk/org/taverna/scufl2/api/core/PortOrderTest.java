package uk.org.taverna.scufl2.api.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import uk.org.taverna.scufl2.api.common.Named;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.api.port.Port;

import static org.junit.Assert.*;

public class PortOrderTest {
	List<Port> ports = new ArrayList<Port>();
	
	Workflow wf = new Workflow();

	@Test
	public void orderedByName() throws Exception {
		ports.add(new InputWorkflowPort(wf, "p3"));
		ports.add(new InputWorkflowPort(wf, "p1"));
		ports.add(new InputWorkflowPort(wf, "p2"));
		Collections.sort(ports);
		assertNamesEqual(ports, "p1", "p2", "p3");
	}
	
	@Test
	public void ignoringNull() throws Exception {
		ports.add(new InputWorkflowPort(null, "p3"));
		ports.add(new InputWorkflowPort(null, "p1"));
		ports.add(new InputWorkflowPort(wf, "p2"));
		Collections.sort(ports);
		assertNamesEqual(ports, "p1", "p2", "p3");
	}
	
	@Test
	public void orderedByClassName() throws Exception {
		ports.add(new InputWorkflowPort(wf, "p3"));
		ports.add(new OutputWorkflowPort(wf, "p1"));
		ports.add(new InputWorkflowPort(wf, "p2"));
		Collections.sort(ports);
		assertNamesEqual(ports, "p2", "p3", "p1");
	}


	public static void assertNamesEqual(List<? extends Named> named, String... expectedNames) {
		List<String> names = namesOf(named);	
		assertEquals(Arrays.asList(expectedNames), names);		
	}

	public static List<String> namesOf(List<? extends Named> named) {
		List<String> names = new ArrayList<String>();
		for (Named n : named) {
			names.add(n.getName());
		}
		return names;
	}

}
