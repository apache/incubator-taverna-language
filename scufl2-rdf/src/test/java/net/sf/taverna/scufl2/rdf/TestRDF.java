package net.sf.taverna.scufl2.rdf;
import java.net.URI;
import java.util.UUID;

import javax.xml.namespace.QName;

import net.sf.taverna.scufl2.rdf.activity.Activity;
import net.sf.taverna.scufl2.rdf.activity.ActivityType;
import net.sf.taverna.scufl2.rdf.common.Named;
import net.sf.taverna.scufl2.rdf.common.Ontology;
import net.sf.taverna.scufl2.rdf.common.WorkflowBean;
import net.sf.taverna.scufl2.rdf.container.TavernaResearchObject;
import net.sf.taverna.scufl2.rdf.core.DataLink;
import net.sf.taverna.scufl2.rdf.core.IterationStrategy;
import net.sf.taverna.scufl2.rdf.core.Processor;
import net.sf.taverna.scufl2.rdf.core.Workflow;
import net.sf.taverna.scufl2.rdf.port.InputProcessorPort;
import net.sf.taverna.scufl2.rdf.port.InputWorkflowPort;
import net.sf.taverna.scufl2.rdf.port.OutputProcessorPort;
import net.sf.taverna.scufl2.rdf.port.OutputWorkflowPort;
import net.sf.taverna.scufl2.rdf.port.ReceiverPort;
import net.sf.taverna.scufl2.rdf.port.SenderPort;

import org.junit.Before;
import org.junit.Test;
import org.openrdf.elmo.ElmoModule;
import org.openrdf.elmo.sesame.SesameManager;
import org.openrdf.elmo.sesame.SesameManagerFactory;
import org.openrdf.repository.contextaware.ContextAwareConnection;
import org.openrdf.rio.helpers.OrganizedRDFWriter;
import org.openrdf.rio.n3.N3Writer;
import org.openrdf.rio.rdfxml.util.OrganizedRDFXMLWriter;


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
		


		TavernaResearchObject ro = elmoManager.create(randomQName(), TavernaResearchObject.class);

		Workflow wf1= elmoManager.create(randomWfQName(), Workflow.class);
		ro.setMainWorkflow(wf1);
		

		wf1.getInputPorts().add(makeNamed(wf1, "I", InputWorkflowPort.class));
		OutputWorkflowPort wf1_out1 = makeNamed(wf1, "out1", OutputWorkflowPort.class);
		wf1.getOutputPorts().add(wf1_out1);
			
		Processor p1 = makeNamed(wf1, "p1", Processor.class);
		wf1.getProcessors().add(p1);
		InputProcessorPort p1_y1 = makeNamed(wf1, "Y1", InputProcessorPort.class);
		p1.getInputPorts().add(p1_y1);
		OutputProcessorPort p1_y2 = makeNamed(wf1, "Y2", OutputProcessorPort.class);
		p1.getOutputPorts().add(p1_y2);
		
		IterationStrategy itStrat = elmoManager.create(IterationStrategy.class);
		//itStrat.setRoot(new CrossProduct());
		//
		//
		//p1.getIterationStrategyStack().add(itStrat );
		
		Processor p4 = makeNamed(wf1, "p4", Processor.class);
		wf1.getProcessors().add(p4);
		InputProcessorPort p4_x2 = makeNamed(wf1, "X2", InputProcessorPort.class);
		p4.getInputPorts().add(p4_x2);
		p4.getInputPorts().add(makeNamed(wf1, "Y1", InputProcessorPort.class));
		OutputProcessorPort p4_y = makeNamed(wf1, "Y", OutputProcessorPort.class);
		p4.getOutputPorts().add(p4_y);
		
		Processor pNested = makeNamed(wf1, "PNested", Processor.class);
		wf1.getProcessors().add(pNested);
		
		InputProcessorPort pNested_i = makeNamed(wf1, "I", InputProcessorPort.class);
		pNested.getInputPorts().add(pNested_i);
		OutputProcessorPort pNested_o = makeNamed(wf1, "O", OutputProcessorPort.class);
		pNested.getOutputPorts().add(pNested_o);

		wf1.getDatalinks().add(makeDataLink(p1_y2, pNested_i));
		
		wf1.getDatalinks().add(makeDataLink(p1_y2, p4_x2));

		wf1.getDatalinks().add(makeDataLink(pNested_o,
				p1_y1));

		wf1.getDatalinks().add(makeDataLink(p4_y,
				wf1_out1));

		Activity activity = makeNamed(wf1, "act0", Activity.class);
		ro.getActivities().add(activity);
		activity.setType(makeNamed(wf1, "beanshell", ActivityType.class));
		
		elmoManager.persist(ro);
		
		ContextAwareConnection connection = elmoManager.getConnection();
		connection.setNamespace("core", Ontology.CORE);
		connection.setNamespace("instance", Ontology.INSTANCE);
		connection.setNamespace("wf", Ontology.WORKFLOW);
		connection.setNamespace("wf1", Ontology.WORKFLOW);
		

		connection.export(new OrganizedRDFXMLWriter(System.out));
		
		System.out.println("\n\n##\n\n");
		
		connection.export(new OrganizedRDFWriter(new N3Writer(System.out)));
	}

	private DataLink makeDataLink(SenderPort fromPort,
			ReceiverPort toPort) {
		DataLink link = elmoManager.create(randomQName(), DataLink.class);
		link.setSenderPort(fromPort);
		link.setReceiverPort(toPort);
		return link;
	}

	private <T extends Named> T makeNamed(Workflow wf, String name, Class<T> beanType) {
		T named = elmoManager.create(wfPartQName(wf, name, beanType), beanType);
		named.setName(name);
		return named;
	}

	private <T extends WorkflowBean> QName wfPartQName(Workflow wf, String name, Class<T> beanType) {
		String wfNamespace = wf.getQName().getNamespaceURI() + wf.getQName().getLocalPart();
		String path = wfNamespace + "/" + beanType.getSimpleName() + "/";
		return new QName(path, name);
	}
	
	private QName randomQName() {
		return new QName(net.sf.taverna.scufl2.rdf.common.Ontology.INSTANCE, UUID.randomUUID().toString());
	}

	private QName randomWfQName() {
		return new QName(net.sf.taverna.scufl2.rdf.common.Ontology.WORKFLOW, UUID.randomUUID().toString());
	}
	
	
}
