package uk.org.taverna.scufl2.integration;

import java.io.File;
import java.net.URI;

import org.junit.Test;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;

public class TestSimpleWf {
	private static final WorkflowBundleIO bundleIo = new WorkflowBundleIO();
	private static final Scufl2Tools scufl2Tools = new Scufl2Tools();
	private static final String bundleType = "application/vnd.taverna.scufl2.workflow-bundle";
	public static URI BEANSHELL = URI
			.create("http://ns.taverna.org.uk/2010/activity/beanshell");
	
	@Test
	public void testName() throws Exception {
		// Workflow
		WorkflowBundle wb = new WorkflowBundleIO().createBundle();
		
		Workflow wf = wb.getMainWorkflow();
		wf.setName("test_wf");
		InputWorkflowPort raw = new InputWorkflowPort(wf, "RAW");
		OutputWorkflowPort msconvert_log = new OutputWorkflowPort(wf, "MSCONVERT_LOG");
		OutputWorkflowPort cmd = new OutputWorkflowPort(wf, "cmd");
		
		// processor
		Processor msconvert = new Processor(wf, "MSCONVERT");
		InputProcessorPort ms_raw = new InputProcessorPort(msconvert, "raw");
		OutputProcessorPort ms_out = new OutputProcessorPort(msconvert, "out");
		OutputProcessorPort ms_cmd = new OutputProcessorPort(msconvert, "cmd");
		
		// links
		new DataLink(wf, raw, ms_raw);
		new DataLink(wf, ms_out, msconvert_log);
		new DataLink(wf, ms_cmd, cmd);
		
		// Beanshell script
		Activity script = new Activity("msconvert");
		script.setType(BEANSHELL);

		Profile profile = wb.getMainProfile();
		script.setParent(profile);
		profile.getActivities().add(script);
		
		scufl2Tools.createActivityPortsFromProcessor(script, msconvert);
		scufl2Tools.bindActivityToProcessorByMatchingPorts(script, msconvert);
		
		Configuration config = new Configuration();
		config.setConfigures(script);
		config.setType(BEANSHELL.resolve("#Config"));
		config.getPropertyResource().addPropertyAsString(BEANSHELL.resolve("#script"), 
				"blablalbal");
		profile.getConfigurations().add(config);
		
		// Save to file (or System.out ? )
		File file = File.createTempFile("test", ".wfbundle");
		bundleIo.writeBundle(wb, file, bundleType);
		System.out.println(file);
	}

}
