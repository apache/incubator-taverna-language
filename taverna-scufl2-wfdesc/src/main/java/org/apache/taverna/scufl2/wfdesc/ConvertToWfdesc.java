package org.apache.taverna.scufl2.wfdesc;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.bind.JAXBException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WriterException;


public class ConvertToWfdesc {
	
	static {
		BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.WARN);
	}
	
	public static void main(String[] args) throws JAXBException, IOException,
			ReaderException, WriterException {
		WorkflowBundleIO io = new WorkflowBundleIO();
		if (Arrays.asList(args).contains("-h") || Arrays.asList(args).contains("--help")) {
			System.out.println("scufl2-to-wfdesc [workflow] ...");
			System.out.println("");
			System.out.println("Converts each of the workflow files to wfdesc.");
			System.out.println("See http://purl.org/wf4ever/model");
			System.out.println("The wfdesc will be stored in a matching filename with ");
			System.out.println("the .wfdesc.ttl extension.");
			System.out.println("If no arguments are given, read from STDIN and write to STDOUT.");
			return;
		}
		if (args.length==0) {
			WorkflowBundle wfBundle = io.readBundle(System.in, null);
			io.writeBundle(wfBundle, System.out,
					"text/vnd.wf4ever.wfdesc+turtle");
		}
		for (String filepath : args) {
			File original = new File(filepath);
			String filename = original.getName();
			filename = filename.replaceFirst("\\..*", "") + ".wfdesc.ttl";
			File wfdesc = new File(original.getParentFile(), filename);
			WorkflowBundle wfBundle = io.readBundle(original, null);
			io.writeBundle(wfBundle, wfdesc,
					"text/vnd.wf4ever.wfdesc+turtle");
			System.out.println("Converted " + original.getPath() + " to " + wfdesc.getPath());
		}
	}
}
