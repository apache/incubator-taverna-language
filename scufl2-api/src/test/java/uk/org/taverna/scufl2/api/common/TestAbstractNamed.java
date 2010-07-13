package uk.org.taverna.scufl2.api.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;

public class TestAbstractNamed {

	@Test
	public void setName() throws Exception {
		Workflow wf = new Workflow();
		Processor p = new Processor();
		p.setName("fish");
		p.setName("soup");
		p.setParent(wf);
	}

	@Test
	public void setNameWithParent() throws Exception {
		Workflow wf = new Workflow();
		Processor p = new Processor();
		p.setName("fish");
		p.setParent(wf);
		assertTrue(wf.getProcessors().contains(p));
		assertTrue(wf.getProcessors().containsName("fish"));
		assertFalse(wf.getProcessors().containsName("soup"));
		p.setName("soup");
		assertFalse(wf.getProcessors().containsName("fish"));
		assertTrue(wf.getProcessors().containsName("soup"));
	}

	@Test
	public void replaceOnRename() throws Exception {
		Workflow wf = new Workflow();
		Processor fish = wf.addProcessor("fish");
		Processor soup = wf.addProcessor("soup");
		assertEquals(2, wf.getProcessors().size());
		
		assertEquals(new HashSet(Arrays.asList("fish", "soup")), wf
				.getProcessors().getNames());
		fish.setName("soup");
		assertEquals(new HashSet(Arrays.asList("soup")), wf
				.getProcessors().getNames());
		assertEquals(1, wf.getProcessors().size());

		assertEquals(fish, wf.getProcessors().iterator().next());
		assertEquals(fish, wf.getProcessors().getByName("soup"));
		assertNull(wf.getProcessors().getByName("fish"));
	}

}
