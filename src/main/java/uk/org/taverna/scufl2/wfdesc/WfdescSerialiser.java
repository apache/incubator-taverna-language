package uk.org.taverna.scufl2.wfdesc;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.xml.namespace.QName;

import org.openrdf.OpenRDFException;
import org.openrdf.elmo.ElmoModule;
import org.openrdf.elmo.sesame.SesameManager;
import org.openrdf.elmo.sesame.SesameManagerFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.contextaware.ContextAwareConnection;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.OrganizedRDFWriter;
import org.openrdf.rio.turtle.TurtleUtil;
import org.openrdf.rio.turtle.TurtleWriter;
import org.openrdf.sail.memory.model.MemURI;
import org.purl.wf4ever.wfdesc.Input;
import org.purl.wf4ever.wfdesc.Output;
import org.purl.wf4ever.wfdesc.Process;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.common.Visitor.VisitorAdapter;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.WriterException;
import uk.org.taverna.scufl2.api.port.InputPort;
import uk.org.taverna.scufl2.api.port.OutputPort;

public class WfdescSerialiser {

	private static final class TurtleWriterWithBase extends TurtleWriter {
		private URITools uriTools = new URITools();
		private final URI baseURI;

		private TurtleWriterWithBase(OutputStream out, URI baseURI) {
			super(out);
			this.baseURI = baseURI;
		}

		@Override
		public void startRDF() throws RDFHandlerException {
			super.startRDF();
			try {
				writeBase();
			} catch (IOException e) {
				throw new RDFHandlerException(e);
			}					
		}

		@Override
		protected void writeURI(org.openrdf.model.URI uri)
				throws IOException {
			
			final String uriString = uriTools.relativePath(baseURI,
					URI.create(uri.toString())).toASCIIString();
			super.writeURI(new MemURI(null, uriString, ""));			
		}

		protected void writeBase() throws IOException {
			writer.write("@base ");
			writer.write("<");
			writer.write(TurtleUtil.encodeURIString(baseURI.toASCIIString()));
			writer.write("> .");
			writer.writeEOL();
		}
	}

	private Scufl2Tools scufl2Tools = new Scufl2Tools();
	private SesameManager sesameManager;
	private URITools uriTools = new URITools();
	private WorkflowBundle wfBundle;

	public Repository getRepository() {
		return getSesameManager().getConnection().getRepository();
	}

	public Scufl2Tools getScufl2Tools() {
		return scufl2Tools;
	}

	public SesameManager getSesameManager() {
		if (sesameManager == null) {
			ElmoModule module = new ElmoModule();
			SesameManagerFactory factory = new SesameManagerFactory(module);
			factory.setInferencingEnabled(true);
			sesameManager = factory.createElmoManager();
		}
		return sesameManager;
	}

	public URITools getUriTools() {
		return uriTools;
	}

	private QName qnameForBean(WorkflowBean bean) {
		URI uri = uriTools.uriForBean(bean);
		org.openrdf.model.URI sesameUri = getRepository().getValueFactory()
				.createURI(uri.toASCIIString());
		return new QName(sesameUri.getNamespace(), sesameUri.getLocalName());
	}

	protected void save(uk.org.taverna.scufl2.api.core.Workflow workflow) {
		workflow.accept(new VisitorAdapter() {
			public boolean visit(WorkflowBean node) {
				QName parentQName = qnameForBean(((Child) node).getParent());
				QName qName = qnameForBean(node);
				if (node instanceof uk.org.taverna.scufl2.api.core.Workflow) {
					org.purl.wf4ever.wfdesc.Workflow wf = sesameManager.create(
							qName, org.purl.wf4ever.wfdesc.Workflow.class);
					// TODO: Connect nested workflows
					return true;
				}
				if (node instanceof Processor) {
//					System.out.println("Eye");
					Process process = sesameManager
							.create(qName, Process.class);
					sesameManager
							.designate(parentQName,
									org.purl.wf4ever.wfdesc.Workflow.class)
							.getHasSubProcess().add(process);
					return true;
				}
				if (node instanceof InputPort) {
					Input input = sesameManager.create(qName, Input.class);
					Process process = sesameManager.designate(parentQName,
							Process.class);
					process.getHasInput().add(input);
					return true;
				}
				if (node instanceof OutputPort) {
					Output output = sesameManager.create(qName, Output.class);
					sesameManager.designate(parentQName, Process.class)
							.getHasOutput().add(output);
					return true;
				}
				if (node instanceof DataLink) {
					DataLink link = (DataLink) node;
					org.purl.wf4ever.wfdesc.DataLink dl = sesameManager.create(
							qnameForBean(link),
							org.purl.wf4ever.wfdesc.DataLink.class);
					Output source = sesameManager.designate(
							qnameForBean(link.getReceivesFrom()), Output.class);
					dl.getHasSource().add(source);
					Input sink = sesameManager.designate(
							qnameForBean(link.getSendsTo()), Input.class);
					dl.getHasSink().add(sink);
					sesameManager
							.designate(parentQName,
									org.purl.wf4ever.wfdesc.Workflow.class)
							.getHasDataLink().add(dl);
					return true;
				}
//				 System.out.println("Ignoring " + node);
				return false;
			}

			@Override
			public boolean visitEnter(WorkflowBean node) {
				if (node instanceof Processor
						|| node instanceof uk.org.taverna.scufl2.api.core.Workflow) {
					visit(node);
					return true;
				}
//				 System.out.println("Skipping " + node);
				return false;
			};
		});
	}

	public void save(WorkflowBundle wfBundle, OutputStream output)
			throws WriterException {
		synchronized (this) {
			if (this.wfBundle != null) {
				throw new IllegalStateException(
						"This serializer is not thread-safe and can only save one WorkflowBundle at a time");
			}
			this.wfBundle = wfBundle;
		}
		try {
			final URI baseURI;
			if (wfBundle.getMainWorkflow() != null) {
				Workflow mainWorkflow = wfBundle.getMainWorkflow();
				baseURI = uriTools.uriForBean(mainWorkflow);
				save(mainWorkflow);
			} else {
				throw new WriterException(
						"wfdesc format requires a main workflow");
			}
			ContextAwareConnection connection = sesameManager.getConnection();
			try {
				connection.setNamespace("wfdesc",
						"http://purl.org/wf4ever/wfdesc#");
				
				connection.export(new OrganizedRDFWriter(new TurtleWriterWithBase(output, baseURI)));
			} catch (OpenRDFException e) {
				throw new WriterException("Can't write to output", e);
			}
		} finally {
			synchronized (this) {
				this.wfBundle = null;
			}
		}
	}

	public void setScufl2Tools(Scufl2Tools scufl2Tools) {
		this.scufl2Tools = scufl2Tools;
	}

	public void setSesameManager(SesameManager sesameManager) {
		this.sesameManager = sesameManager;
	}

	public void setUriTools(URITools uriTools) {
		this.uriTools = uriTools;
	}

}
