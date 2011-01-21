package uk.org.taverna.scufl2.usecases;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.io.WriterException;

public class ConvertT2flowScufl2 {
	public static void main(String[] args) throws JAXBException, IOException,
			ReaderException, WriterException {
		WorkflowBundleIO io = new WorkflowBundleIO();
		for (String filepath : args) {
			File t2File = new File(filepath);
			String filename = t2File.getName();
			filename = filename.replaceFirst("\\..*", ".scufl2");
			File scufl2File = new File(t2File.getParentFile(), filename);
			WorkflowBundle wfBundle = io.readBundle(t2File, "application/vnd.taverna.t2flow+xml");
			io.writeBundle(wfBundle, scufl2File, "application/vnd.taverna.scufl2.workflow-bundle");
		}
	}

}
