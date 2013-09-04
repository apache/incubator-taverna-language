package uk.org.taverna.scufl2.api.common;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.ExampleWorkflow;
import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Visitor.VisitorWithPath;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.BlockingControlLink;
import uk.org.taverna.scufl2.api.core.ControlLink;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;

public class TestURIToolsBeans {

	private static final String BUNDLE_URI = "http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/";
	private static final String HELLOWORLD_URI = BUNDLE_URI
			+ "workflow/HelloWorld/";
	private static final String HELLO_URI = HELLOWORLD_URI + "processor/Hello/";
	private URITools uriTools = new URITools();
	private Scufl2Tools scufl2Tools = new Scufl2Tools();
	private WorkflowBundle wfBundle;

	@Before
	public void makeExampleWorkflow() {
		wfBundle = new ExampleWorkflow().makeWorkflowBundle();
	}

	@Test
	public void uriForActivity() throws Exception {
		Activity activity = wfBundle.getMainProfile().getActivities()
				.getByName("HelloScript");
		URI uri = uriTools.uriForBean(activity);
		assertEquals(BUNDLE_URI + "profile/tavernaWorkbench/"
				+ "activity/HelloScript/", uri.toASCIIString());
	}

	@Test
	public void uriForActivityInput() throws Exception {
		Activity activity = wfBundle.getMainProfile().getActivities()
				.getByName("HelloScript");
		URI uri = uriTools.uriForBean(activity.getInputPorts().getByName(
				"personName"));
		assertEquals(BUNDLE_URI + "profile/tavernaWorkbench/"
				+ "activity/HelloScript/in/personName", uri.toASCIIString());
	}

	@Test
	public void uriForActivityOutput() throws Exception {
		Activity activity = wfBundle.getMainProfile().getActivities()
				.getByName("HelloScript");
		URI uri = uriTools.uriForBean(activity.getOutputPorts().getByName(
				"hello"));
		assertEquals(BUNDLE_URI + "profile/tavernaWorkbench/"
				+ "activity/HelloScript/out/hello", uri.toASCIIString());
	}

	@Test
	public void uriForConfig() throws Exception {
		Configuration config = wfBundle.getMainProfile().getConfigurations()
				.getByName("Hello");
		URI uri = uriTools.uriForBean(config);
		assertEquals(BUNDLE_URI + "profile/tavernaWorkbench/"
				+ "configuration/Hello/", uri.toASCIIString());
	}

	@Test
	public void uriForControlLink() throws Exception {
        Processor hello = wfBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		assertNotNull(hello);
		ControlLink condition = wfBundle.getMainWorkflow().getControlLinks()
				.iterator().next();
		assertTrue(condition instanceof BlockingControlLink);
		URI uri = uriTools.uriForBean(condition);

		assertEquals(
				HELLOWORLD_URI
						+ "control?block=processor/Hello/&untilFinished=processor/wait4me/",
				uri.toASCIIString());
	}

	@Test
	public void uriForDatalink() throws Exception {
		Processor hello = wfBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		List<DataLink> nameLinks = scufl2Tools.datalinksTo(hello
				.getInputPorts().getByName("name"));
		URI uri = uriTools.uriForBean(nameLinks.get(0));
		assertEquals(HELLOWORLD_URI
				+ "datalink?from=in/yourName&to=processor/Hello/in/name",
				uri.toASCIIString());
	}

	@Test
	public void uriForDatalinkWithMerge() throws Exception {
		Processor hello = wfBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		List<DataLink> greetingLinks = scufl2Tools.datalinksFrom(hello
				.getOutputPorts().getByName("greeting"));
		URI uri = uriTools.uriForBean(greetingLinks.get(0));
		assertEquals(
				HELLOWORLD_URI
						+ "datalink?from=processor/Hello/out/greeting&to=out/results&mergePosition=0",
				uri.toASCIIString());
	}

	@Test
	public void uriForDispatchStack() throws Exception {
		URI uri = uriTools.uriForBean(wfBundle.getMainWorkflow()
				.getProcessors().getByName("Hello").getDispatchStack());
		assertEquals(HELLO_URI + "dispatchstack/", uri.toASCIIString());
	}

	@Test
	public void uriForDispatchStackLayer() throws Exception {
		Processor hello = wfBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		URI uri = uriTools.uriForBean(hello.getDispatchStack().get(0));
		assertEquals(HELLO_URI + "dispatchstack/0/", uri.toASCIIString());
	}

	@Test
	public void uriForIterationStrategyCross() throws Exception {
		Processor hello = wfBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		CrossProduct crossProduct = (CrossProduct) hello
				.getIterationStrategyStack().get(0);
		URI uri = uriTools.uriForBean(crossProduct.get(0));
		assertEquals(HELLO_URI + "iterationstrategy/0/0/",
				uri.toASCIIString());
	}

	@Test
	public void uriForIterationStrategyRoot() throws Exception {
		Processor hello = wfBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		URI uri = uriTools.uriForBean(hello.getIterationStrategyStack().get(0));

		assertEquals(HELLO_URI + "iterationstrategy/0/",
				uri.toASCIIString());
	}

	@Test
	public void uriForIterationStrategyStack() throws Exception {
		URI uri = uriTools
				.uriForBean(wfBundle.getMainWorkflow().getProcessors()
						.getByName("Hello").getIterationStrategyStack());
		assertEquals(HELLO_URI +

		"iterationstrategy/", uri.toASCIIString());
	}

	@Test
	public void uriForProcessor() throws Exception {
		URI uri = uriTools.uriForBean(wfBundle.getMainWorkflow()
				.getProcessors().getByName("Hello"));
		assertEquals(HELLO_URI, uri.toASCIIString());
	}

	@Test
	public void uriForProcessorBinding() throws Exception {
		ProcessorBinding processorBinding = wfBundle.getMainProfile()
				.getProcessorBindings().getByName("Hello");
		URI uri = uriTools.uriForBean(processorBinding);
		assertEquals(BUNDLE_URI + "profile/tavernaWorkbench/"
				+ "processorbinding/Hello/", uri.toASCIIString());
	}

	@Test
	public void uriForProcessorBindingIn() throws Exception {
		ProcessorBinding processorBinding = wfBundle.getMainProfile()
				.getProcessorBindings().getByName("Hello");
		ProcessorInputPortBinding inputPortBinding = processorBinding
				.getInputPortBindings().iterator().next();
		URI uri = uriTools.uriForBean(inputPortBinding);
		assertEquals(BUNDLE_URI + "profile/tavernaWorkbench/"
				+ "processorbinding/Hello/in/name" + "", uri.toASCIIString());
	}

	@Test
	public void uriForProcessorBindingOut() throws Exception {
		ProcessorBinding processorBinding = wfBundle.getMainProfile()
				.getProcessorBindings().getByName("Hello");
		ProcessorOutputPortBinding outputPortBinding = processorBinding
				.getOutputPortBindings().iterator().next();
		URI uri = uriTools.uriForBean(outputPortBinding);
		assertEquals(BUNDLE_URI + "profile/tavernaWorkbench/"
				+ "processorbinding/Hello/out/greeting" + "",
				uri.toASCIIString());
	}

	@Test
	public void uriForProcessorInPort() throws Exception {
		Processor hello = wfBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		URI uri = uriTools.uriForBean(hello.getInputPorts().getByName("name"));
		assertEquals(HELLO_URI + "in/name", uri.toASCIIString());
	}

	@Test
	public void uriForProcessorOutPort() throws Exception {

		Processor hello = wfBundle.getMainWorkflow().getProcessors()
				.getByName("Hello");
		URI uri = uriTools.uriForBean(hello.getOutputPorts().getByName(
				"greeting"));
		assertEquals(HELLO_URI + "out/greeting", uri.toASCIIString());
	}

	@Test
	public void uriForProfile() throws Exception {
		URI uri = uriTools.uriForBean(wfBundle.getMainProfile());
		assertEquals(BUNDLE_URI + "profile/tavernaWorkbench/" + "",
				uri.toASCIIString());
	}

	@Test
	public void uriForWfBundle() throws Exception {
		URI uri = uriTools.uriForBean(wfBundle);
		assertEquals(BUNDLE_URI, uri.toASCIIString());
	}

	@Test
	public void uriForWorkflow() throws Exception {
		URI uri = uriTools.uriForBean(wfBundle.getMainWorkflow());
		assertEquals(HELLOWORLD_URI, uri.toASCIIString());
	}

	@Test
	public void uriForWorkflowInPort() throws Exception {
		URI uri = uriTools.uriForBean(wfBundle.getMainWorkflow()
				.getInputPorts().getByName("yourName"));
		assertEquals(HELLOWORLD_URI + "in/yourName", uri.toASCIIString());
	}

	@Test
	public void uriForWorkflowOutPort() throws Exception {
		URI uri = uriTools.uriForBean(wfBundle.getMainWorkflow()
				.getOutputPorts().getByName("results"));
		assertEquals(HELLOWORLD_URI + "out/results", uri.toASCIIString());
	}

	@Test
	public void uriVisitor() {
		final StringBuffer paths = new StringBuffer();
		wfBundle.accept(new VisitorWithPath() {
			@Override
			public boolean visit() {
				WorkflowBean node = getCurrentNode();
				URI uri;
				if (getCurrentPath().isEmpty()) {
					uri = uriTools.uriForBean(node);
				} else {
					uri = uriTools.relativeUriForBean(node, getCurrentPath()
							.peek());
				}
				String indent = "";
				for (int i = 0; i < getCurrentPath().size(); i++) {
					indent += "  ";
				}
				paths.append(indent);
				paths.append(uri);
				paths.append("\n");
				// we won't recurse into Configuration as PropertyResource's
				// don't have URIs
				return !(node instanceof Configuration);
			}
		});
		// System.out.println(paths);
		assertEquals(
				"http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/\n"
						+ "  workflow/HelloWorld/\n"
						+ "    in/yourName\n"
						+ "    out/results\n"
						+ "    processor/Hello/\n"
						+ "      in/name\n"
						+ "      out/greeting\n"
						+ "      iterationstrategy/\n"
						+ "        0/\n"
						+ "          0/\n"
						+ "      dispatchstack/\n"
						+ "        0/\n"
						+ "        1/\n"
						+ "        2/\n"
						+ "        3/\n"
						+ "        4/\n"
						+ "        5/\n"
						+ "    processor/wait4me/\n"
						+ "      iterationstrategy/\n"
						+ "        0/\n"
						+ "      dispatchstack/\n"
						+ "        0/\n"
						+ "        1/\n"
						+ "        2/\n"
						+ "        3/\n"
						+ "        4/\n"
						+ "        5/\n"
					    + "    datalink?from=processor/Hello/out/greeting&to=out/results&mergePosition=0\n"
						+ "    datalink?from=in/yourName&to=processor/Hello/in/name\n"
						+ "    datalink?from=in/yourName&to=out/results&mergePosition=1\n"
						+ "    control?block=processor/Hello/&untilFinished=processor/wait4me/\n"
					    + "    ../../../../workflow/00626652-55ae-4a9e-80d4-c8e9ac84e2ca/\n"
						+ "  profile/tavernaServer/\n"
						+ "    activity/HelloScript/\n"
						+ "      in/personName\n"
						+ "      out/hello\n"
						+ "    processorbinding/Hello/\n"
						+ "      in/name\n"
						+ "      out/greeting\n"
						+ "    configuration/Hello/\n"
						+ "  profile/tavernaWorkbench/\n"
						+ "    activity/HelloScript/\n"
						+ "      in/personName\n"
						+ "      out/hello\n"
						+ "    processorbinding/Hello/\n"
						+ "      in/name\n"
						+ "      out/greeting\n"
						+ "    configuration/Hello/\n",
				paths.toString());
	}

}
