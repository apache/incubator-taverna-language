package uk.org.taverna.scufl2.rdfxml;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.org.taverna.scufl2.api.ExampleWorkflow;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;

public class TestRDFXMLSerializer {
	protected static final String TAVERNAWORKBENCH_RDF = "profile/tavernaWorkbench.rdf";
	protected static final String TAVERNASERVER_RDF = "profile/tavernaServer.rdf";
	protected static final String HELLOWORLD_RDF = "workflow/HelloWorld.rdf";
	RDFXMLSerializer serializer = new RDFXMLSerializer();
	ExampleWorkflow exampleWf = new ExampleWorkflow();
	WorkflowBundle workflowBundle;

	Namespace XSI_NS = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
	Namespace RDF_NS = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	Namespace RDSF_NS = Namespace.getNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
	Namespace SCUFL2_NS = Namespace.getNamespace("s", "http://ns.taverna.org.uk/2010/scufl2#");
	Namespace BEANSHELL_NS = Namespace.getNamespace("beanshell",
			"http://ns.taverna.org.uk/2010/activity/beanshell#");


	protected void assertXpathEquals(String expected, Element element,
			String xpath) throws JDOMException {
		Object o = xpathSelectElement(element, xpath);
		if (o == null) {
			fail("Can't find " + xpath  + " in " + element);
			return;
		}
		String text;
		if (o instanceof Attribute) {
			text = ((Attribute)o).getValue();
		} else {
			text = ((Element)o).getValue();
		}
		assertEquals(expected, text);
	}


	protected void checkProfileDocument(Element root, boolean isWorkbench) throws JDOMException {

		assertEquals("ProfileDocument", root.getAttributeValue("type", XSI_NS));
		assertXpathEquals(isWorkbench ? "tavernaWorkbench/" : "tavernaServer/", root, "./@xml:base");

		Element profile = (Element) root.getChildren().get(0);
		Element activity = (Element) root.getChildren().get(1);
		Element binding = (Element) root.getChildren().get(2);
		Element configuration = (Element) root.getChildren().get(3);


		assertSame(xpathSelectElement(root, "./s:Profile"), profile);
		assertSame(xpathSelectElement(root, "./s:Activity"), activity);
		assertSame(xpathSelectElement(root, "./s:ProcessorBinding"), binding);
		assertSame(xpathSelectElement(root, "./s:Configuration"), configuration);

		assertXpathEquals("", profile, "./@rdf:about");
		assertXpathEquals(isWorkbench ? "tavernaWorkbench" : "tavernaServer", profile, "./s:name");
		assertXpathEquals("processorbinding/Hello/", profile, "./s:processorBinding/@rdf:resource");
		assertXpathEquals("configuration/Hello/", profile, "./s:activateConfiguration/@rdf:resource");

		// activity
		assertXpathEquals("activity/HelloScript/", activity, "./@rdf:about");
		assertXpathEquals("HelloScript", activity, "./s:name");
		assertXpathEquals("http://ns.taverna.org.uk/2010/activity/beanshell",
				activity, "./rdf:type/@rdf:resource");
		// activity input
		assertXpathEquals("activity/HelloScript/in/personName", activity, "./s:inputActivityPort/s:InputActivityPort/@rdf:about");
		assertXpathEquals("personName", activity, "./s:inputActivityPort/s:InputActivityPort/s:name");
		assertXpathEquals("0", activity, "./s:inputActivityPort/s:InputActivityPort/s:portDepth");
		assertXpathEquals("http://www.w3.org/2001/XMLSchema#integer", activity, "./s:inputActivityPort/s:InputActivityPort/s:portDepth/@rdf:datatype");
		// activity output
		assertXpathEquals("activity/HelloScript/out/hello", activity, "./s:outputActivityPort/s:OutputActivityPort/@rdf:about");
		assertXpathEquals("hello", activity, "./s:outputActivityPort/s:OutputActivityPort/s:name");
		assertXpathEquals("0", activity, "./s:outputActivityPort/s:OutputActivityPort/s:portDepth");
		assertXpathEquals("http://www.w3.org/2001/XMLSchema#integer", activity, "./s:outputActivityPort/s:OutputActivityPort/s:portDepth/@rdf:datatype");
		assertXpathEquals("0", activity, "./s:outputActivityPort/s:OutputActivityPort/s:granularPortDepth");
		assertXpathEquals("http://www.w3.org/2001/XMLSchema#integer", activity, "./s:outputActivityPort/s:OutputActivityPort/s:granularPortDepth/@rdf:datatype");

		// processor binding
		assertXpathEquals("processorbinding/Hello/", binding, "./@rdf:about");
		assertXpathEquals("Hello", binding, "./s:name");
		assertXpathEquals("activity/HelloScript/", binding, "./s:bindActivity/@rdf:resource");
		assertXpathEquals("../../workflow/HelloWorld/processor/Hello/", binding, "./s:bindProcessor/@rdf:resource");
		// input port binding
		assertXpathEquals("processorbinding/Hello/in/name", binding, "./s:inputPortBinding/s:InputPortBinding/@rdf:about");
		assertXpathEquals("activity/HelloScript/in/personName", binding, "./s:inputPortBinding/s:InputPortBinding/s:bindInputActivityPort/@rdf:resource");
		assertXpathEquals("../../workflow/HelloWorld/processor/Hello/in/name", binding, "./s:inputPortBinding/s:InputPortBinding/s:bindInputProcessorPort/@rdf:resource");
		// output port binding
		assertXpathEquals("processorbinding/Hello/out/greeting", binding, "./s:outputPortBinding/s:OutputPortBinding/@rdf:about");
		assertXpathEquals("activity/HelloScript/out/hello", binding, "./s:outputPortBinding/s:OutputPortBinding/s:bindOutputActivityPort/@rdf:resource");
		assertXpathEquals("../../workflow/HelloWorld/processor/Hello/out/greeting", binding, "./s:outputPortBinding/s:OutputPortBinding/s:bindOutputProcessorPort/@rdf:resource");


		assertXpathEquals("configuration/Hello/", configuration, "./@rdf:about");
		assertXpathEquals("configuration/Hello.json", configuration, "./rdfs:seeAlso/@rdf:resource");

		assertXpathEquals(
				"http://ns.taverna.org.uk/2010/activity/beanshell#Config",
				configuration, "./rdf:type/@rdf:resource");
		assertXpathEquals("Hello", configuration, "./s:name");
		assertXpathEquals("activity/HelloScript/", configuration, "./s:configure/@rdf:resource");
//		assertXpathEquals("hello = \"Hello, \" + personName;\n" +
//				(isWorkbench ? "JOptionPane.showMessageDialog(null, hello);" : "System.out.println(\"Server says: \" + hello);"), configuration, "./beanshell:script");

	}

	protected void checkRoot(Element root) {
		assertEquals(RDF_NS, root.getNamespace());
		assertEquals("rdf", root.getNamespacePrefix());
		assertEquals("RDF", root.getName());
		assertEquals(SCUFL2_NS, root.getNamespace(""));
		String schemaLocation = root.getAttributeValue("schemaLocation", XSI_NS);
		schemaLocation = schemaLocation.replaceAll("\\s+", " ");
		String[] schemaLocations = schemaLocation.split(" ");
		String[] expectedSchemaLocations = {
				"http://ns.taverna.org.uk/2010/scufl2#","http://ns.taverna.org.uk/2010/scufl2/scufl2.xsd",
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#","http://ns.taverna.org.uk/2010/scufl2/rdf.xsd"
		};
		assertArrayEquals(expectedSchemaLocations, schemaLocations);
	}


	protected void checkWorkflowBundleDocument(Element root) throws JDOMException {
		assertEquals("WorkflowBundleDocument", root.getAttributeValue("type", XSI_NS));

		assertXpathEquals("./", root, "./@xml:base");

		Element wbundle = root.getChild("WorkflowBundle", SCUFL2_NS);
		assertSame(wbundle, root.getChildren().get(0));



		assertXpathEquals("", wbundle, "./@rdf:about");

		assertXpathEquals("HelloWorld", wbundle, "./s:name");
		assertXpathEquals("http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/",
				wbundle, "./s:globalBaseURI/@rdf:resource");

		assertXpathEquals("workflow/HelloWorld/",
				wbundle, "./s:mainWorkflow/@rdf:resource");
		assertXpathEquals("workflow/HelloWorld/",
				wbundle, "./s:workflow/s:Workflow/@rdf:about");
		assertXpathEquals(HELLOWORLD_RDF,
				wbundle, "./s:workflow/s:Workflow/rdfs:seeAlso/@rdf:resource");

		assertXpathEquals("profile/tavernaWorkbench/",
				wbundle, "./s:mainProfile/@rdf:resource");



		assertXpathEquals("profile/tavernaServer/",
				wbundle, "./s:profile[1]/s:Profile/@rdf:about");
		assertXpathEquals("profile/tavernaServer.rdf", wbundle, "./s:profile[1]/s:Profile/rdfs:seeAlso/@rdf:resource");

		assertXpathEquals("profile/tavernaWorkbench/",
				wbundle, "./s:profile[2]/s:Profile/@rdf:about");
		assertXpathEquals(TAVERNAWORKBENCH_RDF,
				wbundle, "./s:profile[2]/s:Profile/rdfs:seeAlso/@rdf:resource");



	}


	protected void checkWorkflowDocument(Element root) throws JDOMException {
		assertEquals("WorkflowDocument", root.getAttributeValue("type", XSI_NS));



		assertXpathEquals("HelloWorld/", root, "./@xml:base");


		Element wf = root.getChild("Workflow", SCUFL2_NS);
		assertSame(wf, root.getChildren().get(0));

		assertXpathEquals("", wf, "./@rdf:about");


		assertXpathEquals("HelloWorld", wf, "./s:name");
		assertXpathEquals("http://ns.taverna.org.uk/2010/workflow/00626652-55ae-4a9e-80d4-c8e9ac84e2ca/",
				wf, "./s:workflowIdentifier/@rdf:resource");

		assertXpathEquals("in/yourName",
				wf, "./s:inputWorkflowPort/s:InputWorkflowPort/@rdf:about");
		assertXpathEquals("yourName",
				wf, "./s:inputWorkflowPort/s:InputWorkflowPort/s:name");
		assertXpathEquals("0",
				wf, "./s:inputWorkflowPort/s:InputWorkflowPort/s:portDepth");
		assertXpathEquals("http://www.w3.org/2001/XMLSchema#integer",
				wf, "./s:inputWorkflowPort/s:InputWorkflowPort/s:portDepth/@rdf:datatype");


		assertXpathEquals("out/results",
				wf, "./s:outputWorkflowPort/s:OutputWorkflowPort/@rdf:about");
		assertXpathEquals("results",
				wf, "./s:outputWorkflowPort/s:OutputWorkflowPort/s:name");


		assertXpathEquals("processor/Hello/",
				wf, "./s:processor[1]/s:Processor/@rdf:about");
		assertXpathEquals("Hello",
				wf, "./s:processor[1]/s:Processor/s:name");

		assertXpathEquals("processor/Hello/in/name",
				wf, "./s:processor[1]/s:Processor/s:inputProcessorPort/s:InputProcessorPort/@rdf:about");
		assertXpathEquals("name",
				wf, "./s:processor[1]/s:Processor/s:inputProcessorPort/s:InputProcessorPort/s:name");
		assertXpathEquals("0",
				wf, "./s:processor[1]/s:Processor/s:inputProcessorPort/s:InputProcessorPort/s:portDepth");
		assertXpathEquals("http://www.w3.org/2001/XMLSchema#integer",
				wf, "./s:processor[1]/s:Processor/s:inputProcessorPort/s:InputProcessorPort/s:portDepth/@rdf:datatype");



		assertXpathEquals("processor/Hello/out/greeting",
				wf, "./s:processor[1]/s:Processor/s:outputProcessorPort/s:OutputProcessorPort/@rdf:about");

		assertXpathEquals("greeting",
				wf, "./s:processor[1]/s:Processor/s:outputProcessorPort/s:OutputProcessorPort/s:name");
		assertXpathEquals("0",
				wf, "./s:processor[1]/s:Processor/s:outputProcessorPort/s:OutputProcessorPort/s:portDepth");
		assertXpathEquals("http://www.w3.org/2001/XMLSchema#integer",
				wf, "./s:processor[1]/s:Processor/s:outputProcessorPort/s:OutputProcessorPort/s:portDepth/@rdf:datatype");
		assertXpathEquals("0",
				wf, "./s:processor[1]/s:Processor/s:outputProcessorPort/s:OutputProcessorPort/s:granularPortDepth");
		assertXpathEquals("http://www.w3.org/2001/XMLSchema#integer",
				wf, "./s:processor[1]/s:Processor/s:outputProcessorPort/s:OutputProcessorPort/s:granularPortDepth/@rdf:datatype");

		// FIXME: probably not what we want - at least we should say it's an *instance* of the default dispatch stack
		assertXpathEquals("http://ns.taverna.org.uk/2010/taverna/2.2/DefaultDispatchStack",
				wf, "./s:processor[1]/s:Processor/s:dispatchStack/s:DispatchStack/rdf:type/@rdf:resource");
		assertXpathEquals("processor/Hello/dispatchstack/",
				wf, "./s:processor[1]/s:Processor/s:dispatchStack/s:DispatchStack/@rdf:about");

		assertXpathEquals("processor/Hello/iterationstrategy/", wf, "./s:processor[1]/s:Processor/s:iterationStrategyStack/s:IterationStrategyStack/@rdf:about");
		assertXpathEquals("Collection", wf, "./s:processor[1]/s:Processor/s:iterationStrategyStack/s:IterationStrategyStack/s:iterationStrategies/@rdf:parseType");
		assertXpathEquals("processor/Hello/iterationstrategy/0/", wf, "./s:processor[1]/s:Processor/s:iterationStrategyStack/s:IterationStrategyStack/s:iterationStrategies/s:CrossProduct/@rdf:about");
		assertXpathEquals("Collection", wf, "./s:processor[1]/s:Processor/s:iterationStrategyStack/s:IterationStrategyStack/s:iterationStrategies/s:CrossProduct/s:productOf/@rdf:parseType");
		assertXpathEquals("processor/Hello/iterationstrategy/0/0/", wf, "./s:processor[1]/s:Processor/s:iterationStrategyStack/s:IterationStrategyStack/s:iterationStrategies/s:CrossProduct/s:productOf/s:PortNode/@rdf:about");
		assertXpathEquals("processor/Hello/in/name", wf, "./s:processor[1]/s:Processor/s:iterationStrategyStack/s:IterationStrategyStack/s:iterationStrategies/s:CrossProduct/s:productOf/s:PortNode/s:iterateOverInputPort/@rdf:resource");
		assertXpathEquals("0", wf, "./s:processor[1]/s:Processor/s:iterationStrategyStack/s:IterationStrategyStack/s:iterationStrategies/s:CrossProduct/s:productOf/s:PortNode/s:desiredDepth");
		assertXpathEquals("http://www.w3.org/2001/XMLSchema#integer", wf, "./s:processor[1]/s:Processor/s:iterationStrategyStack/s:IterationStrategyStack/s:iterationStrategies/s:CrossProduct/s:productOf/s:PortNode/s:desiredDepth/@rdf:datatype");


		assertXpathEquals("processor/wait4me/",
				wf, "./s:processor[2]/s:Processor/@rdf:about");
		assertXpathEquals("wait4me",
				wf, "./s:processor[2]/s:Processor/s:name");


		assertXpathEquals("datalink?from=processor/Hello/out/greeting&to=out/results&mergePosition=0",
				wf, "./s:datalink[1]/s:DataLink/@rdf:about");

		assertXpathEquals("datalink?from=in/yourName&to=processor/Hello/in/name",
				wf, "./s:datalink[2]/s:DataLink/@rdf:about");

		assertXpathEquals("datalink?from=in/yourName&to=out/results&mergePosition=1",
				wf, "./s:datalink[3]/s:DataLink/@rdf:about");
		assertXpathEquals("in/yourName",
				wf, "./s:datalink[3]/s:DataLink/s:receiveFrom/@rdf:resource");
		assertXpathEquals("out/results",
				wf, "./s:datalink[3]/s:DataLink/s:sendTo/@rdf:resource");
		assertXpathEquals("1",
				wf, "./s:datalink[3]/s:DataLink/s:mergePosition");
		assertXpathEquals("http://www.w3.org/2001/XMLSchema#integer",
				wf, "./s:datalink[3]/s:DataLink/s:mergePosition/@rdf:datatype");

		assertXpathEquals("control?block=processor/Hello/&untilFinished=processor/wait4me/",
				wf, "./s:control/s:Blocking/@rdf:about");


		assertXpathEquals("processor/Hello/",
				wf, "./s:control/s:Blocking/s:block/@rdf:resource");
		assertXpathEquals("processor/wait4me/",
				wf, "./s:control/s:Blocking/s:untilFinished/@rdf:resource");

		assertXpathEquals("datalink?from=processor/Hello/out/greeting&to=out/results&mergePosition=0",
				wf, "./s:datalink[1]/s:DataLink/@rdf:about");
		assertXpathEquals("processor/Hello/out/greeting",
				wf, "./s:datalink[1]/s:DataLink/s:receiveFrom/@rdf:resource");
		assertXpathEquals("out/results",
				wf, "./s:datalink[1]/s:DataLink/s:sendTo/@rdf:resource");
		assertXpathEquals("0",
				wf, "./s:datalink[1]/s:DataLink/s:mergePosition");
		assertXpathEquals("http://www.w3.org/2001/XMLSchema#integer",
				wf, "./s:datalink[1]/s:DataLink/s:mergePosition/@rdf:datatype");



		assertXpathEquals("datalink?from=in/yourName&to=processor/Hello/in/name",
				wf, "./s:datalink[2]/s:DataLink/@rdf:about");
		assertXpathEquals("in/yourName",
				wf, "./s:datalink[2]/s:DataLink/s:receiveFrom/@rdf:resource");
		assertXpathEquals("processor/Hello/in/name",
				wf, "./s:datalink[2]/s:DataLink/s:sendTo/@rdf:resource");
		assertNull(xpathSelectElement(wf, "./s:datalink[2]/s:DataLink/s:mergePosition"));



		assertXpathEquals("datalink?from=in/yourName&to=out/results&mergePosition=1",
				wf, "./s:datalink[3]/s:DataLink/@rdf:about");
		assertXpathEquals("in/yourName",
				wf, "./s:datalink[3]/s:DataLink/s:receiveFrom/@rdf:resource");
		assertXpathEquals("out/results",
				wf, "./s:datalink[3]/s:DataLink/s:sendTo/@rdf:resource");
		assertXpathEquals("1",
				wf, "./s:datalink[3]/s:DataLink/s:mergePosition");
		assertXpathEquals("http://www.w3.org/2001/XMLSchema#integer",
				wf, "./s:datalink[3]/s:DataLink/s:mergePosition/@rdf:datatype");

		assertXpathEquals("control?block=processor/Hello/&untilFinished=processor/wait4me/",
				wf, "./s:control/s:Blocking/@rdf:about");


		assertXpathEquals("processor/Hello/",
				wf, "./s:control/s:Blocking/s:block/@rdf:resource");
		assertXpathEquals("processor/wait4me/",
				wf, "./s:control/s:Blocking/s:untilFinished/@rdf:resource");

	}

	// TODO: Update tests
	@Ignore
	@Test
	public void exampleProfileTavernaServer() throws Exception {
		URL tavernaWorkbenc = getClass().getResource("example/profile/tavernaServer.rdf");
		SAXBuilder saxBuilder = new SAXBuilder();
		Document doc = saxBuilder.build(tavernaWorkbenc);
		Element root = doc.getRootElement();

		checkRoot(root);
		checkProfileDocument(root, false);

	}


	// TODO: Update tests
	@Ignore
	@Test
	public void exampleProfileTavernaWorkbench() throws Exception {
		URL tavernaWorkbenc = getClass().getResource("example/profile/tavernaWorkbench.rdf");
		SAXBuilder saxBuilder = new SAXBuilder();
		Document doc = saxBuilder.build(tavernaWorkbenc);
		Element root = doc.getRootElement();

		checkRoot(root);
		checkProfileDocument(root, true);

	}

	@Test
	public void exampleWorkflow() throws Exception {
		URL workflowURL = getClass().getResource("example/workflow/HelloWorld.rdf");
		SAXBuilder saxBuilder = new SAXBuilder();
		Document doc = saxBuilder.build(workflowURL);

		Element root = doc.getRootElement();

		checkRoot(root);
		checkWorkflowDocument(root);

	}


	@Test
	public void exampleWorkflowBundle() throws Exception {
		URL workflowBundleURL = getClass().getResource("example/workflowBundle.rdf");


		SAXBuilder saxBuilder = new SAXBuilder();
		Document doc = saxBuilder.build(workflowBundleURL);

		Element root = doc.getRootElement();

		checkRoot(root);
		checkWorkflowBundleDocument(root);

	}


	@Before
	public void makeExampleWorkflow() {
		workflowBundle = new ExampleWorkflow().makeWorkflowBundle();
		serializer.setWfBundle(workflowBundle);
	}


	protected Document parseXml(ByteArrayOutputStream outStream)
			throws JDOMException, IOException {
		SAXBuilder saxBuilder = new SAXBuilder();
		return saxBuilder.build(new ByteArrayInputStream(outStream.toByteArray()));
	}


	@Test
	public void profile() throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// To test that seeAlso URIs are stored
		serializer.profileDoc(outStream, workflowBundle.getMainProfile(), URI.create(TAVERNAWORKBENCH_RDF));
		//System.out.write(outStream.toByteArray());
		Document doc = parseXml(outStream);
		Element root = doc.getRootElement();

		checkRoot(root);
		checkProfileDocument(root, true);
	}


	@Test
	public void workflow() throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// To test that seeAlso URIs are stored
		serializer.workflowDoc(outStream, workflowBundle.getMainWorkflow(), URI.create(HELLOWORLD_RDF));
//		System.out.write(outStream.toByteArray());
		Document doc = parseXml(outStream);
		Element root = doc.getRootElement();

		checkRoot(root);
		checkWorkflowDocument(root);
	}

	@Test
	public void workflowBundle() throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// To test that seeAlso URIs are stored
		serializer.workflowDoc(new NullOutputStream(), workflowBundle.getMainWorkflow(), URI.create(HELLOWORLD_RDF));
		serializer.profileDoc(new NullOutputStream(), workflowBundle.getProfiles().getByName("tavernaWorkbench"), URI.create(TAVERNAWORKBENCH_RDF));
		serializer.profileDoc(new NullOutputStream(), workflowBundle.getProfiles().getByName("tavernaServer"), URI.create(TAVERNASERVER_RDF));

		serializer.workflowBundleDoc(outStream, URI.create("workflowBundle.rdf"));
		//System.out.write(outStream.toByteArray());
		Document doc = parseXml(outStream);
		Element root = doc.getRootElement();

		checkRoot(root);
		checkWorkflowBundleDocument(root);

	}


	protected Object xpathSelectElement(Element element, String xpath) throws JDOMException {
		XPath x = XPath.newInstance(xpath);
		x.addNamespace(SCUFL2_NS);
		x.addNamespace(RDF_NS);
		x.addNamespace(RDSF_NS);
		x.addNamespace(BEANSHELL_NS);
		//x.addNamespace(XML_NS);

		return x.selectSingleNode(element);
	}
}
