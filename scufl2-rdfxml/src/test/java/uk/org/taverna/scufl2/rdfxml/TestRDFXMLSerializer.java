package uk.org.taverna.scufl2.rdfxml;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.JDOMFactory;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMSource;
import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.ExampleWorkflow;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.TestWorkflowBundleIO;
import uk.org.taverna.scufl2.rdfxml.RDFXMLSerializer;

public class TestRDFXMLSerializer {
	RDFXMLSerializer serializer = new RDFXMLSerializer();
	ExampleWorkflow exampleWf = new ExampleWorkflow();	
	WorkflowBundle workflowBundle;
	
	Namespace XSI_NS = Namespace.getNamespace("http://www.w3.org/2001/XMLSchema-instance");
	Namespace RDF_NS = Namespace.getNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	Namespace SCUFL2_NS = Namespace.getNamespace("http://ns.taverna.org.uk/2010/scufl2#");


	@Before
	public void makeExampleWorkflow() {
		workflowBundle = new ExampleWorkflow().makeWorkflowBundle();
		serializer.setWfBundle(workflowBundle);
	}

	
	@Test
	public void workflowBundleXml() throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		serializer.workflowBundleDoc(outStream, URI.create("workflowBundle.rdf"));
		Document doc = parseXml(outStream);

		System.out.write(outStream.toByteArray());
		
		Element root = doc.getRootElement();
		
		checkRoot(root);
		checkWorkflowBundleDocument(root);
		
	}


	protected void checkWorkflowBundleDocument(Element root) {
		assertEquals("WorkflowBundleDocument", root.getAttributeValue("type", XSI_NS));

		Element wbundle = root.getChild("WorkflowBundle", SCUFL2_NS);
		assertSame(wbundle, root.getChildren().get(0));
		
		
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


	private Document parseXml(ByteArrayOutputStream outStream)
			throws JDOMException, IOException {
		SAXBuilder saxBuilder = new SAXBuilder();
		return saxBuilder.build(new ByteArrayInputStream(outStream.toByteArray()));
	}
}
