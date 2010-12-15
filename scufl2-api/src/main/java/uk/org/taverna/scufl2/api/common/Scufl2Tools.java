package uk.org.taverna.scufl2.api.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * Utility methods for dealing with SCUFL2 models
 * 
 * @author Stian Soiland-Reyes
 * 
 */
public class Scufl2Tools {

	/**
	 * Compare {@link ProcessorBinding}s by their {@link ProcessorBinding#getActivityPosition()}.
	 * 
	 * Note: this comparator imposes orderings that are inconsistent with equals.
	 * 
	 * @author Stian Soiland-Reyes
	 *
	 */
	public static class BindingComparator implements
	Comparator<ProcessorBinding> {

		@Override
		public int compare(ProcessorBinding o1, ProcessorBinding o2) {
			return o1.getActivityPosition() - o2.getActivityPosition();
		}

	}

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

	public Configuration configurationForActivityBoundToProcessor(Processor concat,
			Profile profile) {
		ProcessorBinding binding = processorBindingForProcessor(concat, profile);
		Configuration config = configurationFor(binding.getBoundActivity(), profile);
		return config;
	}


	public List<Configuration> configurationsFor(Configurable configurable, Profile profile) {
		List<Configuration> configurations = new ArrayList<Configuration>();
		for (Configuration config : profile.getConfigurations()) {
			if (config.getConfigures().equals(configurable)) {
				configurations.add(config);
			}
		}
		return configurations;
	}

	public ProcessorBinding processorBindingForProcessor(Processor processor,
			Profile profile) {
		List<ProcessorBinding> bindings = processorBindingsForProcessor(processor, profile);
		if (bindings.isEmpty()) {
			throw new IndexOutOfBoundsException("Could not find bindings for " + processor);
		}
		if (bindings.size() > 1) {
			throw new IllegalStateException("More than one proc binding for " + processor);
		}
		return bindings.get(0);
	}

	public List<ProcessorBinding> processorBindingsForProcessor(Processor processor,
			Profile profile) {
		List<ProcessorBinding> bindings = new ArrayList<ProcessorBinding>();
		for (ProcessorBinding pb : profile.getProcessorBindings()) {
			if (pb.getBoundProcessor().equals(processor)) {
				bindings.add(pb);
			}
		}
		Collections.sort(bindings, new BindingComparator());
		return bindings;
	}

	public List<ProcessorBinding> processorBindingsToActivity(
			Activity activity, Profile profile) {
		List<ProcessorBinding> bindings = new ArrayList<ProcessorBinding>();
		for (ProcessorBinding pb : profile.getProcessorBindings()) {
			if (pb.getBoundActivity().equals(activity)) {
				bindings.add(pb);
			}
		}
		Collections.sort(bindings, new BindingComparator());
		return bindings;
	}


}
