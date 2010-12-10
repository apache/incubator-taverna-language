package uk.org.taverna.scufl2.api.common;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.configurations.DataProperty;
import uk.org.taverna.scufl2.api.configurations.ObjectProperty;
import uk.org.taverna.scufl2.api.configurations.Property;
import uk.org.taverna.scufl2.api.configurations.PropertyNotFoundException;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;

/**
 * Utillity methods for dealing with SCUFL2 models
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

	public String getPropertyData(List<Property> properties, URI predicate) throws PropertyNotFoundException {
		for (Property prop : properties) {
			if (prop.getPredicate().equals(predicate)) {
				if (! (prop instanceof DataProperty)) {
					throw new IllegalStateException("Not a DataProperty: " + predicate);
				}
				return ((DataProperty)prop).getDataValue();
			}
		}
		throw new PropertyNotFoundException("Could not find property for "+ predicate);
	}

	public Set<String> getPropertyDatas(List<Property> properties, URI predicate) {
		Set<String> results = new HashSet<String>();
		for (Property prop : properties) {
			if (prop.getPredicate().equals(predicate)) {
				if (!(prop instanceof ObjectProperty)) {
					throw new IllegalStateException("Not a ObjectProperty: "
							+ predicate);
				}
				results.add(((DataProperty) prop).getDataValue());
			}
		}
		return results;
	}


	public ObjectProperty getPropertyObject(List<Property> properties, URI predicate) throws PropertyNotFoundException {
		for (Property prop : properties) {
			if (prop.getPredicate().equals(predicate)) {
				if (! (prop instanceof ObjectProperty)) {
					throw new IllegalStateException("Not a ObjectProperty: " + predicate);
				}
				return (ObjectProperty) prop;
			}
		}
		throw new PropertyNotFoundException("Could not find property for "+ predicate);
	}


	public Set<ObjectProperty> getPropertyObjects(List<Property> properties,
			URI predicate) {
		Set<ObjectProperty> results = new HashSet<ObjectProperty>();
		for (Property prop : properties) {
			if (prop.getPredicate().equals(predicate)) {
				if (!(prop instanceof ObjectProperty)) {
					throw new IllegalStateException("Not a ObjectProperty: "
							+ predicate);
				}
				results.add((ObjectProperty) prop);
			}
		}
		return results;
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
