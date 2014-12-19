package uk.org.taverna.scufl2.rdfxml;

import java.net.URI;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.rdfxml.jaxb.ObjectFactory;
import uk.org.taverna.scufl2.rdfxml.jaxb.Profile;
import uk.org.taverna.scufl2.rdfxml.jaxb.ProfileDocument;
import uk.org.taverna.scufl2.rdfxml.jaxb.Workflow;
import uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundle;
import uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowBundleDocument;
import uk.org.taverna.scufl2.rdfxml.jaxb.WorkflowDocument;

public class DummyParserTest {

	private JAXBContext jaxbContext;
	private Unmarshaller unmarshaller;

	@SuppressWarnings("unchecked")
	@Test
	public void parse() throws Exception {
		URL resource = getClass().getResource("example/workflowBundle.rdf");
		URI baseUri = resource.toURI();

		@SuppressWarnings("rawtypes")
		JAXBElement<WorkflowBundleDocument> workflowBundle = (JAXBElement<WorkflowBundleDocument>) unmarshaller
				.unmarshal(resource);
		WorkflowBundleDocument bundleDoc = workflowBundle.getValue();
		WorkflowBundle wfBundle = (WorkflowBundle) bundleDoc.getAny().get(0);

		//System.out.println(wfBundle.getName());
		//System.out.println(wfBundle.getMainWorkflow());
		//System.out.println(wfBundle.getSameBaseAs().getResource());
		for (WorkflowBundle.Workflow wfLink : wfBundle.getWorkflow()) {
			String about = wfLink.getWorkflow().getAbout();
			String seeAlso = wfLink.getWorkflow().getSeeAlso().getResource();

			URI wfResource = baseUri.resolve(seeAlso);
			JAXBElement<WorkflowDocument> unmarshalled = (JAXBElement<WorkflowDocument>) unmarshaller
					.unmarshal(wfResource.toURL());
			WorkflowDocument wfDoc = unmarshalled.getValue();
			Workflow wf = (Workflow) wfDoc.getAny().get(0);
			//System.out.println(about + " " + wf.getName());
		}

		for (WorkflowBundle.Profile profileLink : wfBundle.getProfile()) {
			String about = profileLink.getProfile().getAbout();
			String seeAlso = profileLink.getProfile().getSeeAlso()
					.getResource();

			URI profileResource = baseUri.resolve(seeAlso);
			JAXBElement unmarshalled = (JAXBElement) unmarshaller
					.unmarshal(profileResource.toURL());
			ProfileDocument profileDoc = (ProfileDocument) unmarshalled
					.getValue();
			Profile profile = (Profile) profileDoc.getAny().get(0);
			//System.out.println(about + " " + profile.getName());
		}
	}

	@Before
	public void makeUnmarshaller() throws JAXBException {
		
		Class<?>[] packages = { ObjectFactory.class,
				org.w3._1999._02._22_rdf_syntax_ns_.ObjectFactory.class,
				org.w3._2000._01.rdf_schema_.ObjectFactory.class };
		jaxbContext = JAXBContext.newInstance(packages);	
		unmarshaller = jaxbContext.createUnmarshaller();
	}

}
