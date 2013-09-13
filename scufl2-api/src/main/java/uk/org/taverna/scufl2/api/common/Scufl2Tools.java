package uk.org.taverna.scufl2.api.common;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.PropertyException;

import com.fasterxml.jackson.databind.JsonNode;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.annotation.Annotation;
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
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.InputPort;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
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

/**
 * Utility methods for dealing with SCUFL2 models
 * 
 * @author Stian Soiland-Reyes
 */
public class Scufl2Tools {

	private static final String CONSTANT_STRING = "string";
    private static final String CONSTANT_VALUE_PORT = "value";

    public static URI PORT_DEFINITION = URI
			.create("http://ns.taverna.org.uk/2010/scufl2#portDefinition");

	private static URITools uriTools = new URITools();

	public static URI NESTED_WORKFLOW = URI
			.create("http://ns.taverna.org.uk/2010/activity/nested-workflow");

	/**
	 * Compare {@link ProcessorBinding}s by their
	 * {@link ProcessorBinding#getActivityPosition()}.
	 * 
	 * Note: this comparator imposes orderings that are inconsistent with
	 * equals.
	 * 
	 * @author Stian Soiland-Reyes
	 */
	public static class BindingComparator implements
			Comparator<ProcessorBinding> {

		@Override
		public int compare(ProcessorBinding o1, ProcessorBinding o2) {
			return o1.getActivityPosition() - o2.getActivityPosition();
		}

	}
	
    public List<Annotation> annotationsFor(Child<?> bean) {
        WorkflowBundle bundle = findParent(WorkflowBundle.class, bean);
        return annotationsFor(bean, bundle);
    }

    public List<Annotation> annotationsFor(WorkflowBean bean, WorkflowBundle bundle) {
        ArrayList<Annotation> annotations = new ArrayList<Annotation>();
        if (bundle == null) {
            return annotations;
        }
        for (Annotation ann : bundle.getAnnotations()) {
            if (ann.getTarget().equals(bean)){
                annotations.add(ann);
            }
        }
        return annotations;
    }
	

	/**
	 * Returns the {@link Configuration} for a {@link Configurable} in the given
	 * {@link Profile}.
	 * 
	 * @param configurable
	 *            the <code>Configurable</code> to find a
	 *            <code>Configuration</code> for
	 * @param profile
	 *            the <code>Profile</code> to look for the
	 *            <code>Configuration</code> in
	 * @return the <code>Configuration</code> for a <code>Configurable</code> in
	 *         the given <code>Profile</code>
	 */
	public Configuration configurationFor(Configurable configurable,
			Profile profile) {
		List<Configuration> configurations = configurationsFor(configurable,
				profile);
		if (configurations.isEmpty()) {
			throw new IndexOutOfBoundsException(
					"Could not find configuration for " + configurable);
		}
		if (configurations.size() > 1) {
			throw new IllegalStateException("More than one configuration for "
					+ configurable);
		}
		return configurations.get(0);
	}

	public Configuration configurationForActivityBoundToProcessor(
			Processor processor, Profile profile) {
		ProcessorBinding binding = processorBindingForProcessor(processor,
				profile);
		Configuration config = configurationFor(binding.getBoundActivity(),
				profile);
		return config;
	}

	/**
	 * Returns the list of {@link Configuration Configurations} for a
	 * {@link Configurable} in the given {@link Profile}.
	 * 
	 * @param configurable
	 *            the <code>Configurable</code> to find a
	 *            <code>Configuration</code> for
	 * @param profile
	 *            the <code>Profile</code> to look for the
	 *            <code>Configuration</code> in
	 * @return the list of <code>Configurations</code> for a
	 *         <code>Configurable</code> in the given <code>Profile</code>
	 */
	// @SuppressWarnings("unchecked")
	public List<Configuration> configurationsFor(Configurable configurable,
			Profile profile) {
		List<Configuration> configurations = new ArrayList<Configuration>();
		for (Configuration config : profile.getConfigurations()) {
			if (configurable.equals(config.getConfigures())) {
				configurations.add(config);
			}
		}
		// Collections.sort(configurations);
		return configurations;
	}

	@SuppressWarnings("unchecked")
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

	@SuppressWarnings("unchecked")
	public List<BlockingControlLink> controlLinksWaitingFor(
			Processor untilFinished) {
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<DataLink> datalinksFrom(SenderPort senderPort) {
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<DataLink> datalinksTo(ReceiverPort receiverPort) {
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

	public <T extends WorkflowBean> T findParent(Class<T> parentClass,
			Child<?> child) {
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

	public JsonNode portDefinitionFor(ActivityPort activityPort,
			Profile profile) throws PropertyException {
		Configuration actConfig = configurationFor(activityPort.getParent(),
				profile);


		JsonNode portDef = actConfig.getJson().get("portDefinition");
		if (portDef == null) { 
		    return null;
		}

		URI portPath = uriTools.relativeUriForBean(activityPort, activityPort.getParent());
		// e.g. "in/input1" or "out/output2"
		return portDef.get(portPath.toString());

	}

	public ProcessorBinding processorBindingForProcessor(Processor processor,
			Profile profile) {
		List<ProcessorBinding> bindings = processorBindingsForProcessor(
				processor, profile);
		if (bindings.isEmpty()) {
			throw new IndexOutOfBoundsException("Could not find bindings for "
					+ processor);
		}
		if (bindings.size() > 1) {
			throw new IllegalStateException("More than one proc binding for "
					+ processor);
		}
		return bindings.get(0);
	} 

	public List<ProcessorBinding> processorBindingsForProcessor(
			Processor processor, Profile profile) {
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

	public ProcessorInputPortBinding processorPortBindingForPort(
			InputPort inputPort, Profile profile) {
		return (ProcessorInputPortBinding) processorPortBindingForPortInternal(
				inputPort, profile);
	}

	public ProcessorOutputPortBinding processorPortBindingForPort(
			OutputPort outputPort, Profile profile) {
		return (ProcessorOutputPortBinding) processorPortBindingForPortInternal(
				outputPort, profile);
	}

	@SuppressWarnings("rawtypes")
	protected ProcessorPortBinding processorPortBindingForPortInternal(
			Port port, Profile profile) {

		List<ProcessorBinding> processorBindings;
		if (port instanceof ProcessorPort) {
			ProcessorPort processorPort = (ProcessorPort) port;
			processorBindings = processorBindingsForProcessor(
					processorPort.getParent(), profile);
		} else if (port instanceof ActivityPort) {
			ActivityPort activityPort = (ActivityPort) port;
			processorBindings = processorBindingsToActivity(activityPort
					.getParent());
		} else {
			throw new IllegalArgumentException(
					"Port must be a ProcessorPort or ActivityPort");
		}
		for (ProcessorBinding procBinding : processorBindings) {
			ProcessorPortBinding portBinding = processorPortBindingInternalInBinding(
					port, procBinding);
			if (portBinding != null) {
				return portBinding;
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	protected ProcessorPortBinding processorPortBindingInternalInBinding(
			Port port, ProcessorBinding procBinding) {
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
			if (port instanceof ActivityPort
					&& portBinding.getBoundActivityPort().equals(port)) {
				return portBinding;
			}
		}
		return null;
	}

	public void setParents(WorkflowBundle bundle) {
		bundle.accept(new VisitorWithPath() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
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
			if (layer.getType().equals(type)) {
				if (candidate != null) {
					throw new IllegalStateException(
							"Found multiple dispatch stack layers of type "
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
	public Set<Processor> possibleDownStreamProcessors(Workflow dataflow,
			Processor processor) {
		ProcessorSplit splitProcessors = splitProcessors(
				dataflow.getProcessors(), processor);
		Set<Processor> possibles = new HashSet<Processor>(
				splitProcessors.getUnconnected());
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
		ProcessorSplit splitProcessors = splitProcessors(
				dataflow.getProcessors(), firstProcessor);
		Set<Processor> possibles = new HashSet<Processor>(
				splitProcessors.getUnconnected());
		possibles.addAll(splitProcessors.getUpStream());
		return possibles;
	}

	/**
	 * 
	 * @param processors
	 * @param splitPoint
	 * @return
	 */
	public ProcessorSplit splitProcessors(Collection<Processor> processors,
			Processor splitPoint) {
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
					if (!(source instanceof OutputProcessorPort)) {
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
					if (!(sink instanceof InputProcessorPort)) {
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
	 * Return nested workflow for processor as configured in given profile.
	 * <p>
	 * A nested workflow is an activity bound to the processor with the
	 * configurable type equal to {@value #NESTED_WORKFLOW}.
	 * <p>
	 * This method returns <code>null</code> if no such workflow was found,
	 * otherwise the configured workflow.
	 * <p
	 * Note that even if several bindings/configurations map to a different
	 * workflow, this method throws an IllegalStateException. Most workflows
	 * will only have a single workflow for a given profile, to handle more
	 * complex cases use instead
	 * {@link #nestedWorkflowsForProcessor(Processor, Profile)}.
	 * 
	 * @throws NullPointerException
	 *             if the given profile does not have a parent
	 * @throws IllegalStateException
	 *             if a nested workflow configuration is invalid, or more than
	 *             one possible workflow is found
	 * 
	 * @param processor
	 *            Processor which might have a nested workflow
	 * @param profile
	 *            Profile to look for nested workflow activity/configuration.
	 *            The profile must have a {@link WorkflowBundle} set as its
	 *            {@link Profile#setParent(WorkflowBundle)}.
	 * @return The configured nested workflows for processor
	 */
	public Workflow nestedWorkflowForProcessor(Processor processor,
			Profile profile) {
		List<Workflow> wfs = nestedWorkflowsForProcessor(processor, profile);
		if (wfs.isEmpty()) {
			return null;
		}
		if (wfs.size() > 1) {
			throw new IllegalStateException(
					"More than one possible workflow for processor "
							+ processor);
		}
		return wfs.get(0);
	}

	/**
	 * Return list of nested workflows for processor as configured in given
	 * profile.
	 * <p>
	 * A nested workflow is an activity bound to the processor with the
	 * configurable type equal to {@value #NESTED_WORKFLOW}.
	 * <p>
	 * This method returns a list of 0 or more workflows, as every matching
	 * {@link ProcessorBinding} and every matching {@link Configuration} for the
	 * bound activity is considered. Normally there will only be a single nested
	 * workflow, in which case the
	 * {@link #nestedWorkflowForProcessor(Processor, Profile)} method should be
	 * used instead.
	 * <p>
	 * Note that even if several bindings/configurations map to the same
	 * workflow, each workflow is only included once in the list. Nested
	 * workflow configurations that are incomplete or which #workflow can't be
	 * found within the workflow bundle of the profile will be silently ignored.
	 * 
	 * @throws NullPointerException
	 *             if the given profile does not have a parent
	 * @throws IllegalStateException
	 *             if a nested workflow configuration is invalid
	 * 
	 * @param processor
	 *            Processor which might have a nested workflow
	 * @param profile
	 *            Profile to look for nested workflow activity/configuration.
	 *            The profile must have a {@link WorkflowBundle} set as its
	 *            {@link Profile#setParent(WorkflowBundle)}.
	 * @return List of configured nested workflows for processor
	 */
	public List<Workflow> nestedWorkflowsForProcessor(Processor processor,
			Profile profile) {
		WorkflowBundle bundle = profile.getParent();
        if (bundle == null) {
			throw new NullPointerException("Parent must be set for " + profile);
		}
		ArrayList<Workflow> workflows = new ArrayList<Workflow>();
		for (ProcessorBinding binding : processorBindingsForProcessor(
				processor, profile)) {
			if (!binding.getBoundActivity().getType()
					.equals(NESTED_WORKFLOW)) {
				continue;
			}
			for (Configuration c : configurationsFor(
					binding.getBoundActivity(), profile)) {
				JsonNode nested = c.getJson().get("nestedWorkflow");
				Workflow wf = bundle.getWorkflows().getByName(nested.asText());
				if (wf != null && !workflows.contains(wf)) {
					workflows.add(wf);
				}
			}
		}
		return workflows;
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
	 * Returns true if processor contains a nested workflow in the specified
	 * profile.
	 * 
	 */
	public boolean containsNestedWorkflow(Processor processor, Profile profile) {
		for (ProcessorBinding binding : processorBindingsForProcessor(
				processor, profile)) {
			if (binding.getBoundActivity().getType()
					.equals(NESTED_WORKFLOW)) {
				return true;
			}
		}
		return false;
	}

	public void createActivityPortsFromProcessor(Activity activity,
			Processor processor) {
		for (InputProcessorPort processorPort : processor.getInputPorts()) {
			new InputActivityPort(activity, processorPort.getName())
					.setDepth(processorPort.getDepth());
		}
		for (OutputProcessorPort processorPort : processor.getOutputPorts()) {
			OutputActivityPort activityPort = new OutputActivityPort(activity,
					processorPort.getName());
			activityPort.setDepth(processorPort.getDepth());
			activityPort.setGranularDepth(processorPort.getGranularDepth());
		}
	}

	public void createProcessorPortsFromActivity(Processor processor,
			Activity activity) {
		for (InputActivityPort activityPort : activity.getInputPorts()) {
			new InputProcessorPort(processor, activityPort.getName())
					.setDepth(activityPort.getDepth());
		}
		for (OutputActivityPort activityPort : activity.getOutputPorts()) {
			OutputProcessorPort procPort = new OutputProcessorPort(processor,
					activityPort.getName());
			procPort.setDepth(activityPort.getDepth());
			procPort.setGranularDepth(activityPort.getGranularDepth());
		}
	}

	public ProcessorBinding bindActivityToProcessorByMatchingPorts(
			Activity activity, Processor processor) {
		ProcessorBinding binding = new ProcessorBinding();
		binding.setParent(activity.getParent());
		binding.setBoundActivity(activity);
		binding.setBoundProcessor(processor);
		bindActivityToProcessorByMatchingPorts(binding);
		return binding;
	}

	public void bindActivityToProcessorByMatchingPorts(ProcessorBinding binding) {
		Activity activity = binding.getBoundActivity();
		Processor processor = binding.getBoundProcessor();
		for (InputActivityPort activityPort : activity.getInputPorts()) {
			InputProcessorPort processorPort = processor.getInputPorts()
					.getByName(activityPort.getName());
			if (processorPort != null
					&& processorPortBindingInternalInBinding(processorPort,
							binding) == null) {
				new ProcessorInputPortBinding(binding, processorPort,
						activityPort);
			}
		}

		for (OutputProcessorPort processorPort : processor.getOutputPorts()) {
			OutputActivityPort activityPort = activity.getOutputPorts()
					.getByName(processorPort.getName());
			if (activityPort != null
					&& processorPortBindingInternalInBinding(activityPort,
							binding) == null) {
				new ProcessorOutputPortBinding(binding, activityPort,
						processorPort);
			}
		}
	}

	public ProcessorBinding createProcessorAndBindingFromActivity(
			Activity activity) {
		Processor proc = new Processor();
		proc.setName(activity.getName());
		createProcessorPortsFromActivity(proc, activity);
		return bindActivityToProcessorByMatchingPorts(activity, proc);
	}

	public Activity createActivityFromProcessor(Processor processor,
			Profile profile) {
		Activity activity = new Activity();
		activity.setName(processor.getName());
		activity.setParent(profile);
		createActivityPortsFromProcessor(activity, processor);
		bindActivityToProcessorByMatchingPorts(activity, processor);
		return activity;
	}

	public void removePortsBindingForUnknownPorts(ProcessorBinding binding) {
		// First, remove ports no longer owned by processor
		Iterator<ProcessorInputPortBinding> inputBindings = binding
				.getInputPortBindings().iterator();
		Activity activity = binding.getBoundActivity();
		Processor processor = binding.getBoundProcessor();
		for (ProcessorInputPortBinding ip : iterable(inputBindings)) {
			if (!activity.getInputPorts().contains(ip.getBoundActivityPort())) {
				inputBindings.remove();
				continue;
			}
			if (!processor.getInputPorts().contains(ip.getBoundProcessorPort())) {
				inputBindings.remove();
				continue;
			}
		}
		Iterator<ProcessorOutputPortBinding> outputBindings = binding
				.getOutputPortBindings().iterator();
		for (ProcessorOutputPortBinding op : iterable(outputBindings)) {
			if (!activity.getOutputPorts().contains(op.getBoundActivityPort())) {
				outputBindings.remove();
				continue;
			}
			if (!processor.getOutputPorts()
					.contains(op.getBoundProcessorPort())) {
				outputBindings.remove();
				continue;
			}
		}

	}

	public void updateBindingByMatchingPorts(ProcessorBinding binding) {
		removePortsBindingForUnknownPorts(binding);
		bindActivityToProcessorByMatchingPorts(binding);
	}

	private <T> Iterable<T> iterable(final Iterator<T> it) {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return it;
			}
		};
	}

    public static URI CONSTANT = URI
            .create("http://ns.taverna.org.uk/2010/activity/constant");
    
    public static URI CONSTANT_CONFIG = CONSTANT.resolve("#Config");

    public Processor createConstant(Workflow workflow, Profile profile,
            String name) {
        Processor processor = new Processor(null, name);
        workflow.getProcessors().addWithUniqueName(processor);
        processor.setParent(workflow);
        OutputProcessorPort valuePort = new OutputProcessorPort(processor, CONSTANT_VALUE_PORT);
        valuePort.setDepth(0);
        valuePort.setGranularDepth(0);
        
        Activity activity = createActivityFromProcessor(processor, profile);
        activity.setType(CONSTANT);
        createConfigurationFor(activity, CONSTANT_CONFIG);
        return processor;
    }

    private Configuration createConfigurationFor(Activity activity, URI configType) {
        Profile profile = activity.getParent();
        
        Configuration config = new Configuration(activity.getName());
        profile.getConfigurations().addWithUniqueName(config);
        config.setParent(profile);
        
        config.setConfigures(activity);
        config.setType(configType);
        return config;
    }

    public void setConstantStringValue(Processor constant, String value, Profile profile) {
        Configuration config = configurationForActivityBoundToProcessor(constant, profile);
        config.getJsonAsObjectNode().put(CONSTANT_STRING, value);
    }
    
    public String getConstantStringValue(Processor constant, Profile profile) {
        Configuration config = configurationForActivityBoundToProcessor(constant, profile);
        return config.getJson().get(CONSTANT_STRING).asText();
    }
    
    public Set<Processor> getConstants(Workflow workflow, Profile profile) {        
        Set<Processor> procs = new LinkedHashSet<Processor>();
        for (Configuration config : profile.getConfigurations()) {
            Configurable configurable = config.getConfigures();
            if (! CONSTANT.equals(configurable.getType()) || ! (configurable instanceof Activity)) {
                continue;
            }
            for (ProcessorBinding bind :  processorBindingsToActivity((Activity)configurable)) {
                procs.add(bind.getBoundProcessor());
            }
        }
        return procs;
    }

}
