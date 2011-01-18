package uk.org.taverna.scufl2.rdfxml;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.NullOutputStream;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.JDOMFactory;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMSource;
import org.jdom.xpath.XPath;
import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.ExampleWorkflow;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.TestWorkflowBundleIO;
import uk.org.taverna.scufl2.rdfxml.RDFXMLSerializer;

public class TestRDFXMLSerializer {
	protected static final String DUMMY_PROFILE_RDF = "profile/PROFILE.rdf";
	protected static final String DUMMY_WORKFLOW_RDF = "workflow/WORKFLOW.rdf";
	RDFXMLSerializer serializer = new RDFXMLSerializer();
	ExampleWorkflow exampleWf = new ExampleWorkflow();	
	WorkflowBundle workflowBundle;
	
	Namespace XSI_NS = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
	Namespace RDF_NS = Namespace.getNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	Namespace RDSF_NS = Namespace.getNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
	Namespace SCUFL2_NS = Namespace.getNamespace("s", "http://ns.taverna.org.uk/2010/scufl2#");


	@Before
	public void makeExampleWorkflow() {
		workflowBundle = new ExampleWorkflow().makeWorkflowBundle();
		serializer.setWfBundle(workflowBundle);
	}

	
	@Test
	public void workflowBundleXml() throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// To test that seeAlso URIs are stored
		serializer.workflowDoc(new NullOutputStream(), workflowBundle.getMainWorkflow(), URI.create(DUMMY_WORKFLOW_RDF));		
		serializer.profileDoc(new NullOutputStream(), workflowBundle.getMainProfile(), URI.create(DUMMY_PROFILE_RDF));
		
		serializer.workflowBundleDoc(outStream, URI.create("workflowBundle.rdf"));
		//System.out.write(outStream.toByteArray());
		Document doc = parseXml(outStream);
		Element root = doc.getRootElement();
		
		checkRoot(root);
		checkWorkflowBundleDocument(root);
		
	}

	@Test
	public void workflowXml() throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// To test that seeAlso URIs are stored
		serializer.workflowDoc(outStream, workflowBundle.getMainWorkflow(), URI.create(DUMMY_WORKFLOW_RDF));
		System.out.write(outStream.toByteArray());
		Document doc = parseXml(outStream);
		Element root = doc.getRootElement();
		
		checkRoot(root);
		//checkWorkflowDocument(root);
		
	}

	

	protected void checkWorkflowDocument(Element root) throws JDOMException {
		assertEquals("WorkflowDocument", root.getAttributeValue("type", XSI_NS));

		Element wf = root.getChild("Workflow", SCUFL2_NS);
		assertSame(wf, root.getChildren().get(0));
		
		
		assertXpathEquals("HelloWorld/", wf, "./@rdf:about");
		
		
		assertXpathEquals("HelloWorld", wf, "./s:name");		
		assertXpathEquals("http://ns.taverna.org.uk/2010/workflow/00626652-55ae-4a9e-80d4-c8e9ac84e2ca/", 
				wf, "./s:workflowIdentifier/@rdf:resource");
		
		assertXpathEquals("in/yourName", 
				wf, "./s:inputWorkflowPort/s:InputWorkflowPort/@rdf:about");
		assertXpathEquals("yourName", 
				wf, "./s:inputWorkflowPort/s:InputWorkflowPort/name");
		assertXpathEquals("0", 
				wf, "./s:inputWorkflowPort/s:InputWorkflowPort/s:portDepth");
		assertXpathEquals("http://www.w3.org/2001/XMLSchema#integer", 
				wf, "./s:inputWorkflowPort/s:InputWorkflowPort/s:portDepth/@rdf:datatype");
		
		
		assertXpathEquals("out/results", 
				wf, "./s:outputWorkflowPort/s:OutputWorkflowPort/@rdf:about");
		assertXpathEquals("yourName", 
				wf, "./s:outputWorkflowPort/s:OutputWorkflowPort/name");
		
		
		assertXpathEquals("processor/Hello", 
				wf, "./s:processor/s:Processor/@rdf:about");
		assertXpathEquals("Hello", 
				wf, "./s:processor/s:Processor/name");
		
		assertXpathEquals("processor/Hello/in/name", 
				wf, "./s:processor/s:Processor/inputProcessorPort/InputProcessorPort/@rdf:about");
		assertXpathEquals("name", 
				wf, "./s:processor/s:Processor/inputProcessorPort/InputProcessorPort/name");
		assertXpathEquals("0", 
				wf, "./s:processor/s:Processor/inputProcessorPort/InputProcessorPort/s:portDepth");
		assertXpathEquals("http://www.w3.org/2001/XMLSchema#integer", 
				wf, "./s:processor/s:Processor/inputProcessorPort/InputProcessorPort/s:portDepth/@rdf:datatype");
		
		

		assertXpathEquals("processor/Hello/out/greeting", 
				wf, "./s:processor/s:Processor/outputProcessorPort/OutputProcessorPort/@rdf:about");

		assertXpathEquals("name", 
				wf, "./s:processor/s:Processor/outputProcessorPort/OutputProcessorPort/name");
		assertXpathEquals("0", 
				wf, "./s:processor/s:Processor/outputProcessorPort/OutputProcessorPort/s:portDepth");
		assertXpathEquals("http://www.w3.org/2001/XMLSchema#integer", 
				wf, "./s:processor/s:Processor/outputProcessorPort/OutputProcessorPort/s:portDepth/@rdf:datatype");
		assertXpathEquals("0", 
				wf, "./s:processor/s:Processor/outputProcessorPort/OutputProcessorPort/s:granularPortDepth");
		assertXpathEquals("http://www.w3.org/2001/XMLSchema#integer", 
				wf, "./s:processor/s:Processor/outputProcessorPort/OutputProcessorPort/s:granularPortDepth/@rdf:datatype");
		
		// FIXME: probably not what we want - at least we should say it's an *instance* of the default dispatch stack
		assertXpathEquals("http://ns.taverna.org.uk/2010/scufl2/taverna#defaultDispatchStack", 
				wf, "./s:dispatchStack/rdf:type");
		assertXpathEquals("Resource", 
				wf, "./s:dispatchStack/@rdf:parseType");
		
		
		
	}


	protected void checkWorkflowBundleDocument(Element root) throws JDOMException {
		assertEquals("WorkflowBundleDocument", root.getAttributeValue("type", XSI_NS));

		Element wbundle = root.getChild("WorkflowBundle", SCUFL2_NS);
		assertSame(wbundle, root.getChildren().get(0));
		
		assertXpathEquals("./", wbundle, "./@rdf:about");		
		
		assertXpathEquals("HelloWorld", wbundle, "./s:name");		
		assertXpathEquals("http://ns.taverna.org.uk/2010/workflowBundle/28f7c554-4f35-401f-b34b-516e9a0ef731/", 
				wbundle, "./s:sameBaseAs/@rdf:resource");
		
		assertXpathEquals("workflow/HelloWorld/", 
				wbundle, "./s:mainWorkflow/@rdf:resource");
		assertXpathEquals("workflow/HelloWorld/", 
				wbundle, "./s:workflow/s:Workflow/@rdf:about");
		assertXpathEquals(DUMMY_WORKFLOW_RDF, 
				wbundle, "./s:workflow/s:Workflow/rdfs:seeAlso/@rdf:resource");

		assertXpathEquals("profile/tavernaWorkbench/", 
				wbundle, "./s:mainProfile/@rdf:resource");
		
		
		assertXpathEquals("profile/tavernaWorkbench/", 
				wbundle, "./s:profile[1]/s:Profile/@rdf:about");
		assertXpathEquals(DUMMY_PROFILE_RDF, 
				wbundle, "./s:profile[1]/s:Profile/rdfs:seeAlso/@rdf:resource");
		
		
		assertXpathEquals("profile/tavernaServer/", 
				wbundle, "./s:profile[2]/s:Profile/@rdf:about");
		assertNull(xpathSelectElement(wbundle, "./s:profile[2]/s:Profile/rdfs:seeAlso"));

	}


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


	protected Object xpathSelectElement(Element element, String xpath) throws JDOMException {
		XPath x = XPath.newInstance(xpath);
		x.addNamespace(SCUFL2_NS);
		x.addNamespace(RDF_NS);
		x.addNamespace(RDSF_NS);
		return x.selectSingleNode(element);
	}

	protected void checkRoot(Element root) {
		assertEquals(RDF_NS, root.getNamespace());		
		assertEquals("rdf", root.getNamespacePrefix());
		assertEquals("RDF", root.getName());		
		assertEquals(SCUFL2_NS, root.getNamespace(""));		
		String schemaLocation = root.getAttributeValue("schemaLocation", XSI_NS);
		String[] schemaLocations = schemaLocation.split(" ");
		String[] expectedSchemaLocations = {
				"http://ns.taverna.org.uk/2010/scufl2#","http://ns.taverna.org.uk/2010/scufl2/scufl2.xsd",
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#","http://ns.taverna.org.uk/2010/scufl2/rdf.xsd"
		};
		assertArrayEquals(expectedSchemaLocations, schemaLocations);
	}


	protected Document parseXml(ByteArrayOutputStream outStream)
			throws JDOMException, IOException {
		SAXBuilder saxBuilder = new SAXBuilder();
		return saxBuilder.build(new ByteArrayInputStream(outStream.toByteArray()));
	}
}
