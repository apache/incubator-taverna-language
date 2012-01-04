package uk.org.taverna.scufl2.wfdesc;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.io.WriterException;

public class ConvertToWfdesc {
	public static void main(String[] args) throws JAXBException, IOException,
			ReaderException, WriterException {
		WorkflowBundleIO io = new WorkflowBundleIO();
		for (String filepath : args) {
			File original = new File(filepath);
			String filename = original.getName();
			filename = filename.replaceFirst("\\..*", "") + ".wfdesc.ttl";
			File wfdesc = new File(original.getParentFile(), filename);
			WorkflowBundle wfBundle = io.readBundle(original, null);
			io.writeBundle(wfBundle, wfdesc,
					"text/vnd.wf4ever.wfdesc+turtle");	
		}
	}
}
