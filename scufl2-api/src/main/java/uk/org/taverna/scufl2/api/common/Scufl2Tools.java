package uk.org.taverna.scufl2.api.common;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer;
import uk.org.taverna.scufl2.api.port.ActivityPort;
import uk.org.taverna.scufl2.api.port.InputPort;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.port.Port;
import uk.org.taverna.scufl2.api.port.ProcessorPort;
import uk.org.taverna.scufl2.api.port.ReceiverPort;
import uk.org.taverna.scufl2.api.port.SenderPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorPortBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.property.PropertyException;
import uk.org.taverna.scufl2.api.property.PropertyResource;


/**
 * Utility methods for dealing with SCUFL2 models
 * 
 * @author Stian Soiland-Reyes
 */
public class Scufl2Tools {


	public static URI PORT_DEFINITION = URI
			.create("http://ns.taverna.org.uk/2010/scufl2#portDefinition");
	
	private static URITools uriTools = new URITools();
	
	public static URI NESTED_WORKFLOW = URI
			.create("http://ns.taverna.org.uk/2010/activity/nested-workflow");
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

	public Configuration configurationForActivityBoundToProcessor(Processor processor, Profile profile) {
		ProcessorBinding binding = processorBindingForProcessor(processor, profile);
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
	
	public PropertyResource portDefinitionFor(ActivityPort activityPort, Profile profile) throws PropertyException {
		Configuration actConfig = configurationFor(activityPort.getParent(), profile);
		
		URI portURI = uriTools.uriForBean(activityPort);
		URI configURI = uriTools.uriForBean(actConfig);

		
		URI portDefinition;
		URI definesPort;
		if (activityPort instanceof InputPort) {
			portDefinition = PORT_DEFINITION.resolve("#inputPortDefinition");
			definesPort = PORT_DEFINITION.resolve("#definesInputPort");
		} else {
			portDefinition = PORT_DEFINITION.resolve("#outputPortDefinition");
			definesPort = PORT_DEFINITION.resolve("#definesOutputPort");
		}
		for (PropertyResource portDef : 
			actConfig.getPropertyResource().getPropertiesAsResources(portDefinition)) {			
			URI portDefURI = portDef.getPropertyAsResourceURI(definesPort);
			// We'll compare the URIs as absolute URIs - but portDefURI is most likely relative
			// to the config
			if (configURI.resolve(portDefURI).equals(portURI)) {
				return portDef;
			}
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
		if (profile == null) {
			profile = processor.getParent().getParent().getMainProfile();
		}
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

	public DispatchStackLayer dispatchStackByType(Processor processor, URI type) {
		DispatchStackLayer candidate = null;
		for (DispatchStackLayer layer : processor.getDispatchStack()) {
			if (layer.getConfigurableType().equals(type)) {
				if (candidate != null) {
					throw new IllegalStateException("Found multiple dispatch stack layers of type "
							+ type + " in " + processor);
				}
				candidate = layer;
			}
		}
		return candidate;
		
	}

	/**
	 * Find processors that a given processor can connect to downstream.
	 * <p>
	 * This is calculated as all processors in the dataflow, except the
	 * processor itself, and any processor <em>upstream</em>, following both
	 * data links and conditional links.
	 * 
	 * @see #possibleUpStreamProcessors(Dataflow, Processor)
	 * @see #splitProcessors(Collection, Processor)
	 * 
	 * @param dataflow
	 *            Dataflow from where to find processors
	 * @param processor
	 *            Processor which is to be connected
	 * @return A set of possible downstream processors
	 */
	public Set<Processor> possibleDownStreamProcessors(
			Workflow dataflow, Processor processor) {
		ProcessorSplit splitProcessors = splitProcessors(dataflow
				.getProcessors(), processor);
		Set<Processor> possibles = new HashSet<Processor>(splitProcessors
				.getUnconnected());
		possibles.addAll(splitProcessors.getDownStream());
		return possibles;
	}

	/**
	 * Find processors that a given processor can connect to upstream.
	 * <p>
	 * This is calculated as all processors in the dataflow, except the
	 * processor itself, and any processor <em>downstream</em>, following both
	 * data links and conditional links.
	 * 
	 * @see #possibleDownStreamProcessors(Dataflow, Processor)
	 * @see #splitProcessors(Collection, Processor)
	 * 
	 * @param dataflow
	 *            Dataflow from where to find processors
	 * @param processor
	 *            Processor which is to be connected
	 * @return A set of possible downstream processors
	 */
	public Set<Processor> possibleUpStreamProcessors(Workflow dataflow,
			Processor firstProcessor) {
		ProcessorSplit splitProcessors = splitProcessors(dataflow
				.getProcessors(), firstProcessor);
		Set<Processor> possibles = new HashSet<Processor>(splitProcessors
				.getUnconnected());
		possibles.addAll(splitProcessors.getUpStream());
		return possibles;
	}

	/**
	 * 
	 * @param processors
	 * @param splitPoint
	 * @return
	 */
	public ProcessorSplit splitProcessors(
			Collection<Processor> processors, Processor splitPoint) {
		Set<Processor> upStream = new HashSet<Processor>();
		Set<Processor> downStream = new HashSet<Processor>();
		Set<Processor> queue = new HashSet<Processor>();
	
		queue.add(splitPoint);
	
		// First let's go upstream
		while (!queue.isEmpty()) {
			Processor processor = queue.iterator().next();
			queue.remove(processor);
			List<BlockingControlLink> preConditions = controlLinksBlocking(processor);
			for (BlockingControlLink condition : preConditions) {
				Processor upstreamProc = condition.getUntilFinished();
				if (!upStream.contains(upstreamProc)) {
					upStream.add(upstreamProc);
					queue.add(upstreamProc);
				}
			}
			for (InputProcessorPort inputPort : processor.getInputPorts()) {
				for (DataLink incomingLink : datalinksTo(inputPort)) {
					SenderPort source = incomingLink.getReceivesFrom();
					if (! (source instanceof OutputProcessorPort)) {
						continue;
					}
					Processor upstreamProc = ((OutputProcessorPort) source)
							.getParent();
					if (!upStream.contains(upstreamProc)) {
						upStream.add(upstreamProc);
						queue.add(upstreamProc);
					}
				}
			}
		}
		// Our split
		queue.add(splitPoint);
		// Then downstream
		while (!queue.isEmpty()) {
			Processor processor = queue.iterator().next();
			queue.remove(processor);			
			List<BlockingControlLink> controlledConditions = controlLinksWaitingFor(processor);				
			for (BlockingControlLink condition : controlledConditions) {
				Processor downstreamProc = condition.getBlock();
				if (!downStream.contains(downstreamProc)) {
					downStream.add(downstreamProc);
					queue.add(downstreamProc);
				}
			}
			for (OutputProcessorPort outputPort : processor.getOutputPorts()) {
				for (DataLink datalink : datalinksFrom(outputPort)) {
					ReceiverPort sink = datalink.getSendsTo();
					if (! (sink instanceof InputProcessorPort)) {
						continue;
					}
					Processor downstreamProcc = ((InputProcessorPort) sink)
							.getParent();
					if (!downStream.contains(downstreamProcc)) {
						downStream.add(downstreamProcc);
						queue.add(downstreamProcc);
					}
				}
			}
		}
		Set<Processor> undecided = new HashSet<Processor>(processors);
		undecided.remove(splitPoint);
		undecided.removeAll(upStream);
		undecided.removeAll(downStream);
		return new ProcessorSplit(splitPoint, upStream, downStream, undecided);
	}

	/**
	 * Result bean returned from
	 * {@link Scufl2Tools#splitProcessors(Collection, Processor)}.
	 * 
	 * @author Stian Soiland-Reyes
	 * 
	 */
	public static class ProcessorSplit {

		private final Processor splitPoint;
		private final Set<Processor> upStream;
		private final Set<Processor> downStream;
		private final Set<Processor> unconnected;

		/**
		 * Processor that was used as a split point.
		 * 
		 * @return Split point processor
		 */
		public Processor getSplitPoint() {
			return splitPoint;
		}

		/**
		 * Processors that are upstream from the split point.
		 * 
		 * @return Upstream processors
		 */
		public Set<Processor> getUpStream() {
			return upStream;
		}

		/**
		 * Processors that are downstream from the split point.
		 * 
		 * @return Downstream processors
		 */
		public Set<Processor> getDownStream() {
			return downStream;
		}

		/**
		 * Processors that are unconnected to the split point.
		 * <p>
		 * These are processors in the dataflow that are neither upstream,
		 * downstream or the split point itself.
		 * <p>
		 * Note that this does not imply a total graph separation, for instance
		 * processors in {@link #getUpStream()} might have some of these
		 * unconnected processors downstream, but not along the path to the
		 * {@link #getSplitPoint()}, or they could be upstream from any
		 * processor in {@link #getDownStream()}.
		 * 
		 * @return Processors unconnected from the split point
		 */
		public Set<Processor> getUnconnected() {
			return unconnected;
		}

		/**
		 * Construct a new processor split result.
		 * 
		 * @param splitPoint
		 *            Processor used as split point
		 * @param upStream
		 *            Processors that are upstream from split point
		 * @param downStream
		 *            Processors that are downstream from split point
		 * @param unconnected
		 *            The rest of the processors, that are by definition
		 *            unconnected to split point
		 */
		public ProcessorSplit(Processor splitPoint, Set<Processor> upStream,
				Set<Processor> downStream, Set<Processor> unconnected) {
			this.splitPoint = splitPoint;
			this.upStream = upStream;
			this.downStream = downStream;
			this.unconnected = unconnected;
		}

	}

	/**
	 * Returns true if processor contains a nested workflow in any of its
	 * activities in any of its profiles.
	 */
	public boolean containsNestedWorkflow(Processor processor) {
		for (Profile profile : processor.getParent().getParent().getProfiles()) {
			if (containsNestedWorkflow(processor, profile)) { 
				return true;
			}
		}
		return false;		
	}
	
	/**
	 * Returns true if processor contains a nested workflow in the specified profile.
	 * 
	 */
	public boolean containsNestedWorkflow(Processor processor, Profile profile) {
		for (ProcessorBinding binding : 
			processorBindingsForProcessor(processor, profile)) {
			if (binding.getBoundActivity().getConfigurableType().equals(NESTED_WORKFLOW)) {
				return true;
			}
		}
		return false;		
	}
	
}
