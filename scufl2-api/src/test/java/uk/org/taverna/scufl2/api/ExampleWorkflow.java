package uk.org.taverna.scufl2.api;

import java.net.URI;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.BlockingControlLink;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStack;
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer;
import uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import uk.org.taverna.scufl2.api.iterationstrategy.PortNode;
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

public class ExampleWorkflow {

	protected Workflow workflow;
	protected Processor processor;
	protected WorkflowBundle workflowBundle;
	protected InputProcessorPort processorName;
	protected OutputProcessorPort processorGreeting;
	protected InputActivityPort personName;
	protected OutputActivityPort hello;
	protected Activity activity;
	protected BlockingControlLink condition;
	protected Processor wait4me;
	private DataLink nameLink;

	URI TAVERNA_2_2 = URI.create("http://ns.taverna.org.uk/2010/taverna/2.2/");

	public Activity makeActivity() {
		activity = new Activity();
		activity.setName("HelloScript");
		activity.setConfigurableType(URI
				.create("http://ns.taverna.org.uk/2010/taverna/activities/beanshell#Activity"));

		personName = new InputActivityPort(activity, "personName");
		personName.setDepth(0);
		hello = new OutputActivityPort(activity, "hello");
		hello.setDepth(0);
		hello.setGranularDepth(0);
		return activity;

	}

	public Configuration makeConfiguration() {
		Configuration configuration = new Configuration("Hello");
		configuration.setConfigures(activity);

		configuration
				.getPropertyResource()
				.setTypeURI(
						URI.create("http://ns.taverna.org.uk/2010/taverna/activities/beanshell#Configuration"));
		configuration
				.getPropertyResource()
				.addPropertyAsString(
						URI.create("http://ns.taverna.org.uk/2010/taverna/activities/beanshell#script"),
						"hello = \"Hello, \" + personName;\n"
								+ "JOptionPane.showMessageDialog(null, hello);");
		return configuration;
	}

	public DispatchStack makeDispatchStack() {

		// See scufl2-rdf/src/main/resources/taverna-2.2.ttl

		DispatchStack dispatchStack = new DispatchStack();
		new DispatchStackLayer(dispatchStack,
				TAVERNA_2_2.resolve("Parallelise"));
		new DispatchStackLayer(dispatchStack,
				TAVERNA_2_2.resolve("ErrorBounce"));
		new DispatchStackLayer(dispatchStack, TAVERNA_2_2.resolve("Failover"));
		new DispatchStackLayer(dispatchStack, TAVERNA_2_2.resolve("Retry"));
		new DispatchStackLayer(dispatchStack, TAVERNA_2_2.resolve("Stop"));
		new DispatchStackLayer(dispatchStack, TAVERNA_2_2.resolve("Invoke"));

		// TODO: Should not be included, perhaps.. as it would not be changed
		// if the stack is modified!
		dispatchStack.setType(TAVERNA_2_2.resolve("DefaultDispatchStack"));

		return dispatchStack;
	}

	public IterationStrategyStack makeIterationStrategyStack(
			InputProcessorPort... inputs) {
		IterationStrategyStack stack = new IterationStrategyStack();
		CrossProduct crossProduct = new CrossProduct();
		crossProduct.setParent(stack);
		for (InputProcessorPort inp : inputs) {
			PortNode n = new PortNode(crossProduct, inp);
			n.setDesiredDepth(0);
		}
		return stack;
	}

	public Profile makeMainProfile() {
		Profile profile = new Profile();
		profile.setName("tavernaWorkbench");

		// FIXME: Can't set dc:creator/date/description

		// FIXME: Can't create recommendsEnvironment/requiresEnvironment

		makeActivity().setParent(profile);

		makeConfiguration().setParent(profile);
		makeProcessorBinding().setParent(profile);

		// profile.setProfilePosition(0);

		return profile;
	}

	public Workflow makeMainWorkflow() {
		workflow = new Workflow();
		workflow.setName("HelloWorld");

		// NOTE: setWorkflowIdentifier should only be called when loading a
		// workflow
		// which already has an ID
		workflow.setWorkflowIdentifier(URI
				.create("http://ns.taverna.org.uk/2010/workflow/00626652-55ae-4a9e-80d4-c8e9ac84e2ca/"));

		InputWorkflowPort yourName = new InputWorkflowPort(workflow, "yourName");
		yourName.setDepth(0);
		OutputWorkflowPort results = new OutputWorkflowPort(workflow, "results");
		// Not needed:
		// workflow.getInputPorts().add(yourName);
		// workflow.getOutputPorts().add(results);

		workflow.getProcessors().add(makeProcessor());
		workflow.getProcessors().add(makeProcessor2());

		// Make links
		DataLink directLink = new DataLink(workflow, yourName, results);
		directLink.setMergePosition(1);

		DataLink greetingLink = new DataLink(workflow, processorGreeting,
				results);
		greetingLink.setMergePosition(0);

		nameLink = new DataLink(workflow, yourName, processorName);

		condition = new BlockingControlLink(processor, wait4me);

		return workflow;
	}

	public Processor makeProcessor() {
		processor = new Processor(workflow, "Hello");
		processorName = new InputProcessorPort(processor, "name");
		processorName.setDepth(0);
		processorGreeting = new OutputProcessorPort(processor, "greeting");
		processorGreeting.setDepth(0);
		processorGreeting.setGranularDepth(0);

		// FIXME: Should not need to make default dispatch stack
		makeDispatchStack().setParent(processor);

		// FIXME: Should not need to make default iteration stack
		makeIterationStrategyStack(processorName).setParent(processor);

		return processor;
	}

	public Processor makeProcessor2() {
		wait4me = new Processor(workflow, "wait4me");

		// FIXME: Should not need to make default dispatch stack
		makeDispatchStack().setParent(wait4me);

		// FIXME: Should not need to make default iteration stack
		makeIterationStrategyStack().setParent(wait4me);

		return processor;
	}

	public ProcessorBinding makeProcessorBinding() {
		ProcessorBinding processorBinding = new ProcessorBinding();
		processorBinding.setName("Hello");
		processorBinding.setBoundProcessor(processor);
		processorBinding.setBoundActivity(activity);

		new ProcessorInputPortBinding(processorBinding, processorName,
				personName);
		new ProcessorOutputPortBinding(processorBinding, hello,
				processorGreeting);

		return processorBinding;
	}

	public Profile makeSecondaryProfile() {
		Profile profile = makeMainProfile();
		profile.setName("tavernaServer");
		Configuration config = profile.getConfigurations().getByName("Hello");
		config.getPropertyResource().getProperties().clear();
		// FIXME: Need removeProperty!
		config.getPropertyResource()
				.addPropertyAsString(
						URI.create("http://ns.taverna.org.uk/2010/taverna/activities/beanshell#script"),
						"hello = \"Hello, \" + personName;\n"
								+ "System.out.println(\"Server says: \" + hello);");
		return profile;
	}

	public WorkflowBundle makeWorkflowBundle() {
		// Based on
		// uk.org.taverna.scufl2.scufl2-usecases/src/main/resources/workflows/example/workflowBundle.rdf

		workflowBundle = new WorkflowBundle();
		workflowBundle.setName("HelloWorld");
		// NOTE: setSameBaseAs should only be called when loading a workflow
		// bundle
		// which already has an ID
		workflowBundle
				.setSameBaseAs(URI
						.create("http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/"));
		Workflow workflow = makeMainWorkflow();
		workflow.setParent(workflowBundle);
		workflowBundle.setMainWorkflow(workflow);
		Profile profile = makeMainProfile();
		profile.setParent(workflowBundle);
		workflowBundle.setMainProfile(profile);
		Profile secondaryProfile = makeSecondaryProfile();
		secondaryProfile.setParent(workflowBundle);
		return workflowBundle;
	}

}
