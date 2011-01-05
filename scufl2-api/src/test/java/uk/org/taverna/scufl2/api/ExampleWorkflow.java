package uk.org.taverna.scufl2.api;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputWorkflowPort;
import uk.org.taverna.scufl2.api.profiles.Profile;

public class ExampleWorkflow {

	private Workflow workflow;
	private Processor processor;
	private WorkflowBundle workflowBundle;
	private InputProcessorPort processorName;

	public Profile makeMainProfile() {
		return new Profile();
	}

	public Workflow makeMainWorkflow() {
		workflow = new Workflow();
		workflow.setName("HelloWorld");

		// NOTE: setWorkflowIdentifier should only be called when loading a
		// workflow
		// which already has an ID
		// workflow.setWorkflowIdentifier(URI.create("http://ns.taverna.org.uk/2010/workflow/00626652-55ae-4a9e-80d4-c8e9ac84e2ca/"));

		InputWorkflowPort yourName = new InputWorkflowPort(workflow, "yourName");
		OutputWorkflowPort results = new OutputWorkflowPort(workflow, "results");
		// Not needed:
		// workflow.getInputPorts().add(yourName);
		// workflow.getOutputPorts().add(results);

		workflow.getProcessors().add(makeProcessor());

		return workflow;
	}

	public Processor makeProcessor() {
		processor = new Processor(workflow, "Hello");
		processorName = new InputProcessorPort(processor, "name");
		processorGreeting = new OutputProcessorPort(processor, "greeting");

		return processor;
	}

	public Profile makeSecondaryProfile() {
		return makeMainProfile();
	}

	public WorkflowBundle makeWorkflowBundle() {
		//  Based on uk.org.taverna.scufl2.scufl2-usecases/src/main/resources/workflows/example/workflowBundle.rdf

		workflowBundle = new WorkflowBundle();
		workflowBundle.setName("HelloWorld");
		// NOTE: setSameBaseAs should only be called when loading a workflow bundle
		// which already has an ID
		//workflowBundle.setSameBaseAs(URI.create("http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/"))
		workflowBundle.setMainWorkflow(makeMainWorkflow());
		workflowBundle.setMainProfile(makeMainProfile());
		workflowBundle.getProfiles().add(makeSecondaryProfile());
		return workflowBundle;
	}

}
