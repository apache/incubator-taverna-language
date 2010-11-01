package uk.org.taverna.scufl2.rdfxml.jaxb;

import java.io.File;
import java.net.URI;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

public class DummyParserTest {

	private JAXBContext jaxbContext;
	private Unmarshaller unmarshaller;

	@Test
	public void parse() throws Exception {

		File file = new File(
				"../scufl2-usecases/src/main/resources/workflows/example/workflowBundle.rdf");
		URI baseUri = file.toURI();

		@SuppressWarnings("rawtypes")
		JAXBElement<WorkflowBundleDocument> workflowBundle = (JAXBElement<WorkflowBundleDocument>) unmarshaller
				.unmarshal(file);
		WorkflowBundleDocument bundleDoc = workflowBundle.getValue();
		WorkflowBundle wfBundle = (WorkflowBundle) bundleDoc.getAny().get(0);

		System.out.println(wfBundle.getName());
		System.out.println(wfBundle.getMainWorkflow());
		System.out.println(wfBundle.getSameBaseAs().getResource());
		for (WorkflowBundle.Workflow wfLink : wfBundle.getWorkflow()) {
			String about = wfLink.getWorkflow().getAbout();
			String seeAlso = wfLink.getWorkflow().getSeeAlso().getResource();

			URI wfResource = baseUri.resolve(seeAlso);
			JAXBElement<WorkflowDocument> unmarshalled = (JAXBElement<WorkflowDocument>) unmarshaller
					.unmarshal(wfResource.toURL());
			WorkflowDocument wfDoc = unmarshalled.getValue();
			Workflow wf = (Workflow) wfDoc.getAny().get(0);
			System.out.println(about + " " + wf.getName());
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
			System.out.println(about + " " + profile.getName());
		}
	}

	@Before
	public void makeUnmarshaller() throws JAXBException {
		jaxbContext = JAXBContext.newInstance(
						"uk.org.taverna.scufl2.rdfxml.jaxb:org.purl.dc.elements._1:org.w3._1999._02._22_rdf_syntax_ns_:org.w3._2002._07.owl_:org.w3._2000._01.rdf_schema_:",
				getClass()
						.getClassLoader());
		unmarshaller = jaxbContext.createUnmarshaller();
	}

}
