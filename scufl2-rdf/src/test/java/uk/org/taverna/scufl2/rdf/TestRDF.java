package uk.org.taverna.scufl2.rdf;
import java.util.UUID;

import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.concepts.owl.Ontology;
import org.openrdf.elmo.ElmoModule;
import org.openrdf.elmo.sesame.SesameManager;
import org.openrdf.elmo.sesame.SesameManagerFactory;
import org.openrdf.elmo.sesame.roles.SesameEntity;
import org.openrdf.repository.contextaware.ContextAwareConnection;
import org.openrdf.rio.helpers.OrganizedRDFWriter;
import org.openrdf.rio.n3.N3Writer;
import org.openrdf.rio.rdfxml.util.OrganizedRDFXMLWriter;

import uk.org.taverna.scufl2.ontology.Activity;
import uk.org.taverna.scufl2.ontology.ActivityType;
import uk.org.taverna.scufl2.ontology.DataLink;
import uk.org.taverna.scufl2.ontology.InputProcessorPort;
import uk.org.taverna.scufl2.ontology.InputWorkflowPort;
import uk.org.taverna.scufl2.ontology.Named;
import uk.org.taverna.scufl2.ontology.OutputProcessorPort;
import uk.org.taverna.scufl2.ontology.OutputWorkflowPort;
import uk.org.taverna.scufl2.ontology.Processor;
import uk.org.taverna.scufl2.ontology.ReceiverPort;
import uk.org.taverna.scufl2.ontology.SenderPort;
import uk.org.taverna.scufl2.ontology.Workflow;
import uk.org.taverna.scufl2.ontology.WorkflowBundle;
import uk.org.taverna.scufl2.ontology.WorkflowElement;


public class TestRDF {

	private SesameManager elmoManager;

	@Before
	public void initElmoManager() {
		ElmoModule module = new ElmoModule();
		SesameManagerFactory factory = new SesameManagerFactory(module);
		factory.setInferencingEnabled(true);
		elmoManager = factory.createElmoManager();
	}

	@Test
	public void makeExampleWorkflow() throws Exception {

		WorkflowBundle ro = elmoManager.create(randomBundleQName(),
				WorkflowBundle.class);

		Workflow wf1= elmoManager.create(randomWfQName(), Workflow.class);
		ro.setMainWorkflow(wf1);


		wf1.getInputWorkflowPort().add(
				makeNamed(wf1, "I", InputWorkflowPort.class));
		OutputWorkflowPort wf1_out1 = makeNamed(wf1, "out1", OutputWorkflowPort.class);
		wf1.getOutputWorkflowPort().add(wf1_out1);

		Processor p1 = makeNamed(wf1, "p1", Processor.class);
		wf1.getProcessors().add(p1);
		InputProcessorPort p1_y1 = makeNamed(wf1, "Y1", InputProcessorPort.class);
		p1.getInputProcessorPort().add(p1_y1);
		OutputProcessorPort p1_y2 = makeNamed(wf1, "Y2", OutputProcessorPort.class);
		p1.getOutputProcessorPort().add(p1_y2);


		Processor p4 = makeNamed(wf1, "p4", Processor.class);
		wf1.getProcessors().add(p4);
		InputProcessorPort p4_x2 = makeNamed(wf1, "X2", InputProcessorPort.class);
		p4.getInputProcessorPort().add(p4_x2);
		p4.getInputProcessorPort().add(
				makeNamed(wf1, "Y1", InputProcessorPort.class));
		OutputProcessorPort p4_y = makeNamed(wf1, "Y", OutputProcessorPort.class);
		p4.getOutputProcessorPort().add(p4_y);

		Processor pNested = makeNamed(wf1, "PNested", Processor.class);
		wf1.getProcessors().add(pNested);

		InputProcessorPort pNested_i = makeNamed(wf1, "I", InputProcessorPort.class);
		pNested.getInputProcessorPort().add(pNested_i);
		OutputProcessorPort pNested_o = makeNamed(wf1, "O", OutputProcessorPort.class);
		pNested.getOutputProcessorPort().add(pNested_o);

		wf1.getDatalinks().add(makeDataLink(p1_y2, pNested_i));

		wf1.getDatalinks().add(makeDataLink(p1_y2, p4_x2));

		wf1.getDatalinks().add(makeDataLink(pNested_o,
				p1_y1));

		wf1.getDatalinks().add(makeDataLink(p4_y,
				wf1_out1));

		Activity activity = elmoManager.create(Activity.class);
		ro.getActivities().add(activity);
		ActivityType beanshellType = elmoManager.create(
				new QName("http://ns.taverna.org.uk/2010/taverna/activity/",
						"beanshell"),
				ActivityType.class);
		activity.setActivityType(beanshellType);

		elmoManager.persist(ro);

		ContextAwareConnection connection = elmoManager.getConnection();
		connection.setNamespace("scufl2",
				"http://ns.taverna.org.uk/2010/scufl2#");

		connection.export(new OrganizedRDFXMLWriter(System.out));

		System.out.println("\n\n##\n\n");

		connection.export(new OrganizedRDFWriter(new N3Writer(System.out)));
	}

	private DataLink makeDataLink(SenderPort fromPort,
			ReceiverPort toPort) {
		DataLink link = elmoManager.create(DataLink.class);
		link.setReceivesFrom(fromPort);
		link.setSendsTo(toPort);
		return link;
	}

	private <T extends Named> T makeNamed(Workflow wf, String name,
			Class<T> beanType) {
		T named = elmoManager.create(wfPartQName(wf, name, beanType), beanType);
		named.setName(name);
		return named;
	}

	private <T extends WorkflowElement> QName wfPartQName(Workflow wf,
			String name, Class<T> beanType) {
		SesameEntity wfEntity = (SesameEntity) wf;

		String wfNamespace = wfEntity.getQName().getNamespaceURI()
				+ wfEntity.getQName().getLocalPart();
		String path = wfNamespace + "/" + beanType.getSimpleName() + "/";
		return new QName(path, name);
	}

	private QName randomBundleQName() {
		return new QName("http://ns.taverna.org.uk/2010/workflowBundle/", UUID
				.randomUUID().toString());
	}

	private QName randomWfQName() {
		return new QName("http://ns.taverna.org.uk/2010/workflow/", UUID
				.randomUUID().toString());
	}


}
