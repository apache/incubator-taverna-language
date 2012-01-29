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
import uk.org.taverna.scufl2.api.iterationstrategy.DotProduct;
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

public class WorkflowMaker {
	private static Scufl2Tools scufl2Tools = new Scufl2Tools();
	private static URITools uriTools = new URITools();
	private static WorkflowBundleIO bundleIO = new WorkflowBundleIO();

	public static void main(String[] args) throws Exception {
		new WorkflowMaker().makeWorkflow();
	}

	public void makeWorkflow() throws IOException, WriterException,
			ReaderException {

		/** Top-level object is a Workflow Bundle */
		WorkflowBundle bundle = new WorkflowBundle();

		Workflow workflow = new Workflow();
		/** Workflow names must be unique within the WorkflowBundle */
		workflow.setName("Echotest");

		bundle.setMainWorkflow(workflow);
		/**
		 * Additional (typically nested) workflows can be added:
		 * 
		 * <pre>
		 * bundle.getWorkflows().add(workflow2)
		 * </pre>
		 * 
		 * but the above is implied by setMainWorkflow()
		 */

		/** Creating and adding a workflow port */
		InputWorkflowPort in1 = new InputWorkflowPort();
		in1.setName("in1");
		/** where does this input port belong? */
		in1.setParent(workflow);
		/**
		 * implies:
		 * 
		 * <pre>
		 * workflow.getInputPorts().add(in1);
		 * </pre>
		 */

		/**
		 * If input should be a list instead of single value:
		 * 
		 * <pre>
		 * in1.setDepth(1);
		 * </pre>
		 */

		/** Output, this time using the shorthand constructors */
		OutputWorkflowPort out1 = new OutputWorkflowPort(workflow, "out1");

		/**
		 * A processor is a unit which performs some work in a workflow. The
		 * name must be unique within the parent workflow.
		 * 
		 */
		Processor p = new Processor(workflow, "p");

		/**
		 * Same as:
		 * 
		 * <pre>
		 * Processor p = new Processor();
		 * p.setName(&quot;p&quot;);
		 * p.setParent(workflow);
		 * workflow.getProcessors().add(p);
		 * </pre>
		 */

		/**
		 * Processors typically have inputs and outputs which are connected
		 * within the workflow
		 */
		InputProcessorPort pIn = new InputProcessorPort(p, "pIn");
		OutputProcessorPort pOut = new OutputProcessorPort(p, "pOut");
		/**
		 * .. any additional ports must have a unique name within the input or
		 * output ports of that processor.
		 */

		/**
		 * Defining a data link from the workflow input port 'in1' to the
		 * processor input port 'pIn' - this means that data will flow from
		 * 'in1' to 'pIn'.
		 */
		DataLink link = new DataLink();
		link.setReceivesFrom(in1);
		link.setSendsTo(pIn);
		/**
		 * The ports must be either processor or workflow ports, and both of the
		 * same workflow as the datalink is added to:
		 */
		workflow.getDataLinks().add(link);

		/**
		 * Or more compact style: pOut -> out1 .. connecting processor output
		 * port 'pOut' to the workflow output port 'out1'
		 */
		new DataLink(workflow, pOut, out1);
		/**
		 * the constructor will perform for us:
		 * 
		 * <pre>
		 * setParent(workflow)
		 * 		workflow.getDataLinks().add(workflow)
		 * </pre>
		 */

		/**
		 * Note: As datalinks are unique based on the connection, they don't
		 * have names
		 */

		/**
		 * Unless this workflow bundle is abstract, we need to fill in what the
		 * processors are to execute, by connecting them to the activity
		 * implementations. This is done in a Profile, which is added to the
		 * WorkflowBundle.
		 */
		Profile profile = new Profile("default");

		/**
		 * One profile can be suggest as the 'main' profile - but alternative
		 * execution profiles can also be added, for instance to provide
		 * Grid-based activities rather than Web Service activities - each of
		 * them must have a unique name within the profiles of the workflow
		 * bundle.
		 */
		bundle.setMainProfile(profile);

		/**
		 * Additional profiles can be added with:
		 * 
		 * <pre>
		 * bundle.getProfiles().add(profile2);
		 * </pre>
		 */

		/**
		 * An Activity is a particular service implementation. The name is not
		 * important, but need to be unique within the activities in a profile.
		 * (The default constructor can be used to generate a random UUID-based
		 * name)
		 */
		Activity myBeanshell = new Activity("myBeanshell");

		/**
		 * Activities are of different types, identified by an URI. A workflow
		 * engine will typically expose which activity types it supports.
		 * <p>
		 * The default types of Taverna have the prefix
		 * http://ns.taverna.org.uk/2010/activity/, but other plugins will have
		 * different URI bases.
		 */
		URI BEANSHELL = URI
				.create("http://ns.taverna.org.uk/2010/activity/beanshell");
		myBeanshell.setConfigurableType(BEANSHELL);

		/**
		 * Activities are activated within a particular profile (Therefore
		 * execution of a profile requires the engine to support all types of
		 * all activities of the profile)
		 */
		profile.getActivities().add(myBeanshell);

		/**
		 * Most activities also require a configuration in order to run. The
		 * name of the configuration is not important, but must be unique within
		 * the configurations of a profile. The default constructor
		 * Configuration() generates a UUID-based name as a fallback.
		 */
		Configuration beanshellConfig = new Configuration("beanshellConf");
		/**
		 * The activity we configure. (DispatchStackLayer can also be
		 * configured)
		 */
		beanshellConfig.setConfigures(myBeanshell);
		/**
		 * A configuration is of a specified type (specified as an URI), which
		 * is typically related to (but different from) the activity type - but
		 * might in some cases be shared amongst several activity types.
		 */
		beanshellConfig.setConfigurableType(BEANSHELL.resolve("#Config"));

		/**
		 * Configurations are normally shared in the same profile as the
		 * activity they configure (the parent) - but in some cases might also
		 * be added by other profiles in order to reuse a configuration across
		 * profiles. (Note: A profile is *stored* within its parent profile).
		 */
		beanshellConfig.setParent(profile);
		profile.getConfigurations().add(beanshellConfig);

		/**
		 * Depending on the configuration type specified above, certain
		 * *properties* should be specified, and other properties might be
		 * optional. In this case, only
		 * <http://ns.taverna.org.uk/2010/activity/beanshell#script> is
		 * specified, as a string value. (more complex properties can be
		 * specified using other methods of the PropertyResource)
		 */
		beanshellConfig.getPropertyResource().addPropertyAsString(
				BEANSHELL.resolve("#script"), "out1 = in1");
		/**
		 * Note that property names are specified as URIs, which are often
		 * related to the URI of the configuration type - but might be reused
		 * across several configuration types.
		 */

		/**
		 * A Processor Binding connects a Processor ('p') with an Activity
		 * 'myBeanshell'. This means that execution of p will use the activity.
		 */
		ProcessorBinding binding = new ProcessorBinding();
		binding.setBoundProcessor(p);
		binding.setBoundActivity(myBeanshell);

		/**
		 * It is possible, but not common, for multiple processor bindings to
		 * reuse the same activity. On execution, the workflow engine might or
		 * might not instantiate this as the same activity implementation.
		 */

		/** And add binding to the profile */
		binding.setParent(profile);
		/**
		 * alternatively:
		 * 
		 * <pre>
		 * profile.getProcessorBindings().add(binding)
		 * </pre>
		 */

		/**
		 * It is possible to bind more than one activity for the same processor,
		 * in which case they will be used as alternate services on failure. (As
		 * the default Dispatch Stack contains the Failover layer). In this
		 * case, the processor bindings should specify the 'activity position',
		 * which determines the ordering of activities within a processor:
		 * 
		 * <pre>
		 * binding.setActivityPosition(15);
		 * </pre>
		 */

		/**
		 * Activities have input and output ports as well, normally these match
		 * one-to-one with the bound processor's port names and depth.
		 */
		InputActivityPort aIn1 = new InputActivityPort(myBeanshell, "in1");
		myBeanshell.getInputPorts().add(aIn1);
		OutputActivityPort aOut1 = new OutputActivityPort(myBeanshell, "out1");
		myBeanshell.getOutputPorts().add(aOut1);

		/**
		 * But in case the activities don't match up (such as when multiple
		 * activities are bound to the same processor, or as in this example
		 * where the port matches the script), a port mapping must be specified
		 * in the processor binding:
		 */
		binding.getInputPortBindings().add(
				new ProcessorInputPortBinding(binding, pIn, aIn1));
		new ProcessorOutputPortBinding(binding, aOut1, pOut);

		/**
		 * It is not required to bind any processor input port, but many
		 * activities expect some or all their inputs bound. It is not required
		 * to bind all activity output ports, but all processor output ports
		 * must be bound for each processor binding.
		 */

		/**
		 * Not covered by this example:
		 * 
		 * Dispatch stack:
		 * 
		 * <pre>
		 * DispatchStackLayer dispatchStackLayer = new DispatchStackLayer();
		 * dispatchStackLayer
		 * 		.setConfigurableType(URI
		 * 				.create(&quot;http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/Retry&quot;));
		 * p.getDispatchStack().add(dispatchStackLayer);
		 * Configuration retryConfig = new Configuration();
		 * retryConfig.setConfigures(dispatchStackLayer);
		 * // ..
		 * </pre>
		 * 
		 * Iteration strategies:
		 * 
		 * <pre>
		 * p.getIterationStrategyStack().add(new DotProduct(pIn, pIn2));
		 * </pre>
		 */

		/**
		 * Before storing the workflow bundle, we'll make sure that everything
		 * we made has a parent included (so that for instance a configuration
		 * is stored together with its parent profile). The
		 * scufl2Tools.setParents method will traverse the WorkflowBundle from
		 * the top and fill in any blank parents.
		 */
		scufl2Tools.setParents(bundle);

		/** We will write out as a SCUFL2 Workflow Bundle file (*.wfbundle),
		 * for simplicity we'll just use a temporary file in this example.  */
		File file = File.createTempFile("test", ".wfbundle");

		/**
		 * Bundle IO 
		 */
		bundleIO.writeBundle(bundle, file,
				"application/vnd.taverna.scufl2.workflow-bundle");
		System.out.println("Written to " + file + "\n");

		// Read it back in
		WorkflowBundle secondBundle = bundleIO.readBundle(file,
				"application/vnd.taverna.scufl2.workflow-bundle");

		// Write in a debug text format
		bundleIO.writeBundle(secondBundle, System.out,
				"text/vnd.taverna.scufl2.structure");

	}
}
