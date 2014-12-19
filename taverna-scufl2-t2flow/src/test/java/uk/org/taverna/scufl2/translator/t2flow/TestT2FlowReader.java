package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.org.taverna.scufl2.translator.t2flow.T2FlowReader.APPLICATION_VND_TAVERNA_T2FLOW_XML;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.io.structure.StructureReader;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;

public class TestT2FlowReader {
	
	private static final String AS_UUID = "92c5e8d5-8360-4f86-a845-09c9849cbdc5";
	WorkflowBundleIO io = new WorkflowBundleIO();
	private static final String AS_T2FLOW = "/as.t2flow";

	@Test
	public void readSimpleWorkflow() throws Exception {
		URL wfResource = getClass().getResource(AS_T2FLOW);
		assertNotNull("Could not find workflow " + AS_T2FLOW, wfResource);
		WorkflowBundle wfBundle = io.readBundle(wfResource,
				APPLICATION_VND_TAVERNA_T2FLOW_XML);
		Profile profile = wfBundle.getMainProfile();
		assertEquals(1, wfBundle.getProfiles().size());
		assertEquals(profile,
				wfBundle.getProfiles().getByName("taverna-2.1.0"));
		
		String report = IOUtils.toString(getClass().getResourceAsStream("/as.txt"));
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		io.writeBundle(wfBundle, byteStream, StructureReader.TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
                String byteStreamTtoString = byteStream.toString("utf-8");
                report = report.replaceAll("\r", "").replaceAll("\n", "");
                byteStreamTtoString = byteStreamTtoString.replaceAll("\r", "").replaceAll("\n", "");
		assertEquals(report, byteStreamTtoString);
	}
	

	@Test
	public void guessMediaType() throws Exception {

		byte[] firstBytes = new byte[1024];
		getClass().getResourceAsStream(AS_T2FLOW).read(firstBytes);		
		assertEquals(APPLICATION_VND_TAVERNA_T2FLOW_XML, io.guessMediaTypeForSignature(firstBytes));
		// Mess up the xmlns declaration
		firstBytes[49] = 32;
		assertEquals(null, io.guessMediaTypeForSignature(firstBytes));
	}

	@Test
	public void preservedOriginalT2flow() throws Exception {
		URL wfResource = getClass().getResource(AS_T2FLOW);
		assertNotNull("Could not find workflow " + AS_T2FLOW, wfResource);
		WorkflowBundle wfBundle = io.readBundle(wfResource,
				APPLICATION_VND_TAVERNA_T2FLOW_XML);
		Map<String, ResourceEntry> history = wfBundle.getResources()
				.listResources("history");
		assertEquals(1, history.size());
		assertEquals(AS_UUID + ".t2flow", history.keySet().iterator().next());
		ResourceEntry r = history.get(AS_UUID + ".t2flow");
		assertEquals("application/vnd.taverna.t2flow+xml", r.getMediaType());
		String original = IOUtils.toString(getClass().getResourceAsStream(
				AS_T2FLOW));
		String shouldBeOriginal = wfBundle.getResources().getResourceAsString(
				"history/" + AS_UUID + ".t2flow");
		assertEquals(shouldBeOriginal, original);

	}
	

	
	
	
}
