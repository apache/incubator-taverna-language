package org.apache.taverna.scufl2.cwl;

import static org.junit.Assert.*;

import java.net.URL;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.cwl.CwlReader;
import org.junit.Test;

public class TestCwlReader {
	@Test
	public void parseEx1() throws Exception {
		URL revsort = getClass().getResource("/ex1/revsort.cwl");
		CwlReader reader = new CwlReader();
		WorkflowBundle b = reader.readBundle(revsort);
		new WorkflowBundleIO().writeBundle(b, System.out, "text/vnd.taverna.scufl2.structure");
	}
}
