package uk.org.taverna.scufl2.api.io;

import static org.junit.Assert.assertEquals;
import static uk.org.taverna.scufl2.api.io.structure.StructureReader.TEXT_VND_TAVERNA_SCUFL2_STRUCTURE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Test;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public class TestStructureReader {

	private static final String UTF_8 = "utf-8";
	protected WorkflowBundleIO bundleIO = new WorkflowBundleIO();
	
	public String getStructureFormatWorkflowBundle() throws IOException {
		InputStream helloWorldStream = getClass().getResourceAsStream(
				"HelloWorld.txt");
		return IOUtils.toString(helloWorldStream);
	}
	
	@Test
	public void configurationReadTwice() throws Exception {
		InputStream inputStream = new ByteArrayInputStream(
				getStructureFormatWorkflowBundle().getBytes("utf-8"));
		WorkflowBundle readBundle = bundleIO.readBundle(inputStream,
				TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
		assertEquals(1, readBundle.getMainProfile().getConfigurations().size());
		new Scufl2Tools().setParents(readBundle);
		assertEquals(1, readBundle.getMainProfile().getConfigurations().size());
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bundleIO.writeBundle(readBundle, output, TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
		assertEquals(1, readBundle.getMainProfile().getConfigurations().size());
		String bundleTxt = new String(output.toByteArray(), UTF_8);
		assertEquals(getStructureFormatWorkflowBundle(), bundleTxt);
		
	}


}
