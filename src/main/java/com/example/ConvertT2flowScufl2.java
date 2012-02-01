package com.example;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.io.WriterException;

public class ConvertT2flowScufl2 {
	public static void main(String[] args) throws Exception, ReaderException,
			WriterException {
		new ConvertT2flowScufl2().convert(args);
	}

	public void convert(String[] filepaths) throws ReaderException, IOException, WriterException {

		WorkflowBundleIO io = new WorkflowBundleIO();
		for (String filepath : filepaths) {
			File t2File = new File(filepath);
			String filename = t2File.getName();
			filename = filename.replaceFirst("\\..*", ".wfbundle");
			File scufl2File = new File(t2File.getParentFile(), filename);
			WorkflowBundle wfBundle = io.readBundle(t2File,
					"application/vnd.taverna.t2flow+xml");
			io.writeBundle(wfBundle, scufl2File,
					"application/vnd.taverna.scufl2.workflow-bundle");
		}

	}

}
