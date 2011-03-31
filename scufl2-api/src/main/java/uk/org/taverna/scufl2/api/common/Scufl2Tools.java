package uk.org.taverna.scufl2.api.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Visitor.VisitorWithPath;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.BlockingControlLink;
import uk.org.taverna.scufl2.api.core.ControlLink;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.port.ActivityPort;
import uk.org.taverna.scufl2.api.port.InputPort;
import uk.org.taverna.scufl2.api.port.OutputPort;
import uk.org.taverna.scufl2.api.port.Port;
import uk.org.taverna.scufl2.api.port.ProcessorPort;
import uk.org.taverna.scufl2.api.port.ReceiverPort;
import uk.org.taverna.scufl2.api.port.SenderPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorPortBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * Utility methods for dealing with SCUFL2 models
 * 
 * @author Stian Soiland-Reyes
 */
public class Scufl2Tools {

	/**
	 * Compare {@link ProcessorBinding}s by their {@link ProcessorBinding#getActivityPosition()}.
	 * 
	 * Note: this comparator imposes orderings that are inconsistent with equals.
	 * 
	 * @author Stian Soiland-Reyes
	 */
	public static class BindingComparator implements Comparator<ProcessorBinding> {

		@Override
		public int compare(ProcessorBinding o1, ProcessorBinding o2) {
			return o1.getActivityPosition() - o2.getActivityPosition();
		}

	}

	/**
	 * Returns the {@link Configuration} for a {@link Configurable} in the given {@link Profile}.
	 * 
	 * @param configurable
	 *            the <code>Configurable</code> to find a <code>Configuration</code> for
	 * @param profile
	 *            the <code>Profile</code> to look for the <code>Configuration</code> in
	 * @return the <code>Configuration</code> for a <code>Configurable</code> in the given
	 *         <code>Profile</code>
	 */
	public Configuration configurationFor(Configurable configurable, Profile profile) {
		List<Configuration> configurations = configurationsFor(configurable, profile);
		if (configurations.isEmpty()) {
			throw new IndexOutOfBoundsException("Could not find configuration for " + configurable);
		}
		if (configurations.size() > 1) {
			throw new IllegalStateException("More than one configuration for " + configurable);
		}
		return configurations.get(0);
	}

	public Configuration configurationForActivityBoundToProcessor(Processor concat, Profile profile) {
		ProcessorBinding binding = processorBindingForProcessor(concat, profile);
		Configuration config = configurationFor(binding.getBoundActivity(), profile);
		return config;
	}

	/**
	 * Returns the list of {@link Configuration Configurations} for a {@link Configurable} in the
	 * given {@link Profile}.
	 * 
	 * @param configurable
	 *            the <code>Configurable</code> to find a <code>Configuration</code> for
	 * @param profile
	 *            the <code>Profile</code> to look for the <code>Configuration</code> in
	 * @return the list of <code>Configurations</code> for a <code>Configurable</code> in the given
	 *         <code>Profile</code>
	 */
	@SuppressWarnings("unchecked")
	public List<Configuration> configurationsFor(Configurable configurable, Profile profile) {
		List<Configuration> configurations = new ArrayList<Configuration>();
		for (Configuration config : profile.getConfigurations()) {
			if (config.getConfigures().equals(configurable)) {
				configurations.add(config);
			}
		}
		// Collections.sort(configurations);
		return configurations;
	}

	public List<BlockingControlLink> controlLinksBlocking(Processor blocked) {
		List<BlockingControlLink> controlLinks = new ArrayList<BlockingControlLink>();
		for (ControlLink link : blocked.getParent().getControlLinks()) {
			if (!(link instanceof BlockingControlLink)) {
				continue;
			}
			BlockingControlLink blockingControlLink = (BlockingControlLink) link;
			if (blockingControlLink.getBlock().equals(blocked)) {
				controlLinks.add(blockingControlLink);
			}
		}
		Collections.sort(controlLinks);
		return controlLinks;
	}

	public List<BlockingControlLink> controlLinksWaitingFor(Processor untilFinished) {
		List<BlockingControlLink> controlLinks = new ArrayList<BlockingControlLink>();
		for (ControlLink link : untilFinished.getParent().getControlLinks()) {
			if (!(link instanceof BlockingControlLink)) {
				continue;
			}
			BlockingControlLink blockingControlLink = (BlockingControlLink) link;
			if (blockingControlLink.getUntilFinished().equals(untilFinished)) {
				controlLinks.add(blockingControlLink);
			}
		}
		Collections.sort(controlLinks);
		return controlLinks;

	}

	@SuppressWarnings("unchecked")
	public List<DataLink> datalinksFrom(SenderPort senderPort) {
		@SuppressWarnings("rawtypes")
		Workflow wf = findParent(Workflow.class, (Child) senderPort);
		List<DataLink> links = new ArrayList();
		for (DataLink link : wf.getDataLinks()) {
			if (link.getReceivesFrom().equals(senderPort)) {
				links.add(link);
			}
		}
		Collections.sort(links);
		return links;
	}

	@SuppressWarnings("unchecked")
	public List<DataLink> datalinksTo(ReceiverPort receiverPort) {
		@SuppressWarnings("rawtypes")
		Workflow wf = findParent(Workflow.class, (Child) receiverPort);
		List<DataLink> links = new ArrayList<DataLink>();
		for (DataLink link : wf.getDataLinks()) {
			if (link.getSendsTo().equals(receiverPort)) {
				links.add(link);
			}
		}
		Collections.sort(links);
		return links;
	}

	public <T extends WorkflowBean> T findParent(Class<T> parentClass, Child<?> child) {
		WorkflowBean parent = child.getParent();
		if (parent == null) {
			return null;
		}
		if (parentClass.isAssignableFrom(parent.getClass())) {
			@SuppressWarnings("unchecked")
			T foundParent = (T) parent;
			return foundParent;
		}
		if (parent instanceof Child) {
			return findParent(parentClass, (Child<?>) parent);
		}
		return null;

	}

	public ProcessorBinding processorBindingForProcessor(Processor processor, Profile profile) {
		List<ProcessorBinding> bindings = processorBindingsForProcessor(processor, profile);
		if (bindings.isEmpty()) {
			throw new IndexOutOfBoundsException("Could not find bindings for " + processor);
		}
		if (bindings.size() > 1) {
			throw new IllegalStateException("More than one proc binding for " + processor);
		}
		return bindings.get(0);
	}

	public List<ProcessorBinding> processorBindingsForProcessor(Processor processor, Profile profile) {
		List<ProcessorBinding> bindings = new ArrayList<ProcessorBinding>();
		for (ProcessorBinding pb : profile.getProcessorBindings()) {
			if (pb.getBoundProcessor().equals(processor)) {
				bindings.add(pb);
			}
		}
		Collections.sort(bindings, new BindingComparator());
		return bindings;
	}

	public List<ProcessorBinding> processorBindingsToActivity(Activity activity) {
		Profile profile = activity.getParent();
		List<ProcessorBinding> bindings = new ArrayList<ProcessorBinding>();
		for (ProcessorBinding pb : profile.getProcessorBindings()) {
			if (pb.getBoundActivity().equals(activity)) {
				bindings.add(pb);
			}
		}
		Collections.sort(bindings, new BindingComparator());
		return bindings;
	}

	public ProcessorInputPortBinding processorPortBindingForPort(InputPort inputPort,
			Profile profile) {
		return (ProcessorInputPortBinding) processorPortBindingForPortInternal(inputPort, profile);
	}

	public ProcessorOutputPortBinding processorPortBindingForPort(OutputPort outputPort,
			Profile profile) {
		return (ProcessorOutputPortBinding) processorPortBindingForPortInternal(outputPort, profile);
	}

	@SuppressWarnings("rawtypes")
	protected ProcessorPortBinding processorPortBindingForPortInternal(Port port, Profile profile) {

		List<ProcessorBinding> processorBindings;
		if (port instanceof ProcessorPort) {
			ProcessorPort processorPort = (ProcessorPort) port;
			processorBindings = processorBindingsForProcessor(processorPort.getParent(), profile);
		} else if (port instanceof ActivityPort) {
			ActivityPort activityPort = (ActivityPort) port;
			processorBindings = processorBindingsToActivity(activityPort.getParent());
		} else {
			throw new IllegalArgumentException("Port must be a ProcessorPort or ActivityPort");
		}
		for (ProcessorBinding procBinding : processorBindings) {
			Set<? extends ProcessorPortBinding> portBindings;
			if (port instanceof InputPort) {
				portBindings = procBinding.getInputPortBindings();
			} else {
				portBindings = procBinding.getOutputPortBindings();
			}
			for (ProcessorPortBinding portBinding : portBindings) {
				if (port instanceof ProcessorPort
						&& portBinding.getBoundProcessorPort().equals(port)) {
					return portBinding;
				}
				if (port instanceof ActivityPort && portBinding.getBoundActivityPort().equals(port)) {
					return portBinding;
				}
			}
		}
		return null;
	}

	public void setParents(WorkflowBundle bundle) {
		bundle.accept(new VisitorWithPath() {
			@Override
			public boolean visit() {
				WorkflowBean node = getCurrentNode();
				if (node instanceof Child) {
					Child child = (Child) node;
					WorkflowBean parent = getCurrentPath().peek();
					if (child.getParent() != parent) {
						child.setParent(parent);
					}
				}
				return true;
			}

		});
	}

}
