package com.example;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.io.WriterException;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;

public class WorkflowMaker {
	private static Scufl2Tools scufl2Tools = new Scufl2Tools();
	private static URITools uriTools = new URITools();
	private static WorkflowBundleIO bundleIO = new WorkflowBundleIO();

	public static void main(String[] args) throws Exception {
		new WorkflowMaker().makeWorkflow();
	}

	public void makeWorkflow() throws IOException, WriterException, ReaderException {
		WorkflowBundle bundle = new WorkflowBundle();
		Workflow workflow = new Workflow();
		workflow.setName("Echotest");
		bundle.setMainWorkflow(workflow);
		InputWorkflowPort in1 = new InputWorkflowPort(workflow, "in1");
		OutputWorkflowPort out1 = new OutputWorkflowPort(workflow, "out1");
		
//		 Same as:
/*		Processor p = new Processor();
 *		p.setName("p");
 *		workflow.getProcessors().add(p);
 */
		Processor p = new Processor(workflow, "p");
		InputProcessorPort pIn = new InputProcessorPort(p, "pIn");
		OutputProcessorPort pOut = new OutputProcessorPort(p, "pOut");		

		// Datalink in1->pIn - step by step
		DataLink link = new DataLink();
		link.setReceivesFrom(in1);
		link.setSendsTo(pIn);
		workflow.getDataLinks().add(link);		
		// Or more compact style: pOut -> out1
		new DataLink(workflow, pOut, out1);

		Profile profile = new Profile("default");
		bundle.setMainProfile(profile);
		
		Activity beanshell = new Activity("myBeanshell");
		URI BEANSHELL = URI
				.create("http://ns.taverna.org.uk/2010/activity/beanshell");
		beanshell.setConfigurableType(BEANSHELL);
		profile.getActivities().add(beanshell);

		Configuration beanshellConfig = new Configuration("beanshellConf");
		beanshellConfig.setConfigures(beanshell);
		beanshellConfig.setConfigurableType(BEANSHELL.resolve("#Config"));
		profile.getConfigurations().add(beanshellConfig);

		// The actual configuration properties
		beanshellConfig.getPropertyResource().addPropertyAsString(BEANSHELL.resolve("#script"), 
				"out1 = in1");
		
		ProcessorBinding binding = new ProcessorBinding();
		binding.setBoundProcessor(p);
		binding.setBoundActivity(beanshell);
		// alternatively profile.getProcessorBindings().add(binding)
		binding.setParent(profile);
		
		beanshell.getInputPorts().add(new InputActivityPort(beanshell, "in1"));
		beanshell.getOutputPorts().add(new OutputActivityPort(beanshell, "out1"));
		
		
		
		// Write as Workflow bundle file
		File file = File.createTempFile("test", ".wfbundle");
		
		scufl2Tools.setParents(bundle);
		
		bundleIO.writeBundle(bundle, file, "application/vnd.taverna.scufl2.workflow-bundle");
		System.out.println("Written to " + file + "\n");

		// Read it back in
		WorkflowBundle secondBundle = bundleIO.readBundle(file, "application/vnd.taverna.scufl2.workflow-bundle");
		
		
		// Write in a debug text format
		bundleIO.writeBundle(secondBundle, System.out, "text/vnd.taverna.scufl2.structure");

	}
}
