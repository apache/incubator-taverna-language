package uk.org.taverna.scufl2.rdfxml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import uk.org.taverna.scufl2.rdfxml.jaxb.Configuration;
import uk.org.taverna.scufl2.rdfxml.jaxb.ProcessorBinding.InputPortBinding;
import uk.org.taverna.scufl2.rdfxml.jaxb.ProcessorBinding.OutputPortBinding;
import uk.org.taverna.scufl2.rdfxml.jaxb.Profile;
import uk.org.taverna.scufl2.rdfxml.jaxb.ProfileDocument;

public class ProfileParser extends AbstractParser {

	public ProfileParser() {
		super();
	}

	public ProfileParser(ThreadLocal<ParserState> parserState) {
		super(parserState);
	}

	protected void parseActivity(
			uk.org.taverna.scufl2.rdfxml.jaxb.Activity original) {
		Activity activity = new Activity();

		getParserState().push(activity);
		try {

			mapBean(original.getAbout(), activity);
			if (original.getName() != null) {
				activity.setName(original.getName());
			}
			activity.setParent(getParserState().getCurrent(
					uk.org.taverna.scufl2.api.profiles.Profile.class));
			if (original.getType() != null) {
				activity.setConfigurableType(resolve(original.getType()
						.getResource()));
			}
			for (uk.org.taverna.scufl2.rdfxml.jaxb.Activity.InputActivityPort inputActivityPort : original
					.getInputActivityPort()) {
				parseInputActivityPort(inputActivityPort.getInputActivityPort());
			}
			for (uk.org.taverna.scufl2.rdfxml.jaxb.Activity.OutputActivityPort outputActivityPort : original
					.getOutputActivityPort()) {
				parseOutputActivityPort(outputActivityPort
						.getOutputActivityPort());
			}
		} finally {
			getParserState().pop();
		}

	}

	protected void parseConfiguration(Configuration original) {
		// TODO Auto-generated method stub

	}

	protected void parseInputActivityPort(
			uk.org.taverna.scufl2.rdfxml.jaxb.InputActivityPort original) {
		InputActivityPort port = new InputActivityPort();
		mapBean(original.getAbout(), port);
		port.setParent(getParserState().getCurrent(Activity.class));

		port.setName(original.getName());
		if (original.getPortDepth() != null) {
			port.setDepth(original.getPortDepth().getValue());
		}
	}

	protected void parseInputPortBinding(
			uk.org.taverna.scufl2.rdfxml.jaxb.InputPortBinding original)
			throws ReaderException {
		ProcessorInputPortBinding binding = new ProcessorInputPortBinding();
		mapBean(original.getAbout(), binding);
		binding.setParent(getParserState().getCurrent(ProcessorBinding.class));

		binding.setBoundActivityPort(resolveBeanUri(original
				.getBindInputActivityPort().getResource(),
				InputActivityPort.class));
		binding.setBoundProcessorPort(resolveBeanUri(original
				.getBindInputProcessorPort().getResource(),
				InputProcessorPort.class));

	}

	protected void parseOutputActivityPort(
			uk.org.taverna.scufl2.rdfxml.jaxb.OutputActivityPort original) {
		OutputActivityPort port = new OutputActivityPort();
		mapBean(original.getAbout(), port);
		port.setParent(getParserState().getCurrent(Activity.class));

		port.setName(original.getName());
		if (original.getPortDepth() != null) {
			port.setDepth(original.getPortDepth().getValue());
		}
		if (original.getGranularPortDepth() != null) {
			port.setGranularDepth(original.getGranularPortDepth().getValue());
		}
	}

	protected void parseOutputPortBinding(
			uk.org.taverna.scufl2.rdfxml.jaxb.OutputPortBinding original)
			throws ReaderException {
		ProcessorOutputPortBinding binding = new ProcessorOutputPortBinding();
		mapBean(original.getAbout(), binding);
		binding.setParent(getParserState().getCurrent(ProcessorBinding.class));

		binding.setBoundActivityPort(resolveBeanUri(original
				.getBindOutputActivityPort().getResource(),
				OutputActivityPort.class));
		binding.setBoundProcessorPort(resolveBeanUri(original
				.getBindOutputProcessorPort().getResource(),
				OutputProcessorPort.class));

	}

	protected void parseProcessorBinding(
			uk.org.taverna.scufl2.rdfxml.jaxb.ProcessorBinding original)
			throws ReaderException {
		uk.org.taverna.scufl2.api.profiles.ProcessorBinding binding = new uk.org.taverna.scufl2.api.profiles.ProcessorBinding();
		mapBean(original.getAbout(), binding);
		getParserState().push(binding);

		if (original.getName() != null) {
			binding.setName(original.getName());
		}
		if (original.getActivityPosition() != null) {
			binding.setActivityPosition(original.getActivityPosition()
					.getValue());
		}

		URI processorUri = resolve(original.getBindProcessor().getResource());
		URI activityUri = resolve(original.getBindActivity().getResource());

		binding.setBoundProcessor((Processor) resolveBeanUri(processorUri));
		binding.setBoundActivity((Activity) resolveBeanUri(activityUri));

		for (InputPortBinding inputPortBinding : original.getInputPortBinding()) {
			parseInputPortBinding(inputPortBinding.getInputPortBinding());
		}
		for (OutputPortBinding outputPortBinding : original
				.getOutputPortBinding()) {
			parseOutputPortBinding(outputPortBinding.getOutputPortBinding());
		}


		getParserState().pop();

	}

	protected void parseProfile(Profile original, URI profileUri) {
		uk.org.taverna.scufl2.api.profiles.Profile p = new uk.org.taverna.scufl2.api.profiles.Profile();
		p.setParent(getParserState().getCurrent(WorkflowBundle.class));

		getParserState().push(p);

		if (original.getAbout() != null) {
			URI about = getParserState().getCurrentBase().resolve(
					original.getAbout());
			mapBean(about, p);
		} else {
			mapBean(profileUri, p);
		}

		if (original.getName() != null) {
			p.setName(original.getName());
		}
		// Note - we'll pop() in profileSecond() instead

	}

	protected void parseProfileSecond(Profile profileElem) {
		// TODO: Parse activates config etc.
		getParserState().pop();

	}

	protected void readProfile(URI profileUri, URI source)
			throws ReaderException, IOException {
		if (source.isAbsolute()) {
			throw new ReaderException("Can't read external profile source "
					+ source);
		}
		InputStream bundleStream = getParserState().getUcfPackage()
				.getResourceAsInputStream(source.getPath());

		JAXBElement<ProfileDocument> elem;
		try {
			elem = (JAXBElement<ProfileDocument>) unmarshaller
					.unmarshal(bundleStream);
		} catch (JAXBException e) {
			throw new ReaderException("Can't parse profile document " + source,
					e);
		}

		URI base = getParserState().getLocation().resolve(source);
		if (elem.getValue().getBase() != null) {
			base = base.resolve(elem.getValue().getBase());
		}

		getParserState().setCurrentBase(base);

		uk.org.taverna.scufl2.rdfxml.jaxb.Profile profileElem = null;
		for (Object any : elem.getValue().getAny()) {
			if (any instanceof uk.org.taverna.scufl2.rdfxml.jaxb.Profile) {
				if (profileElem != null) {
					throw new ReaderException("More than one <Profile> found");
				}
				profileElem = (uk.org.taverna.scufl2.rdfxml.jaxb.Profile) any;
				parseProfile(profileElem, profileUri);
			} else if (any instanceof uk.org.taverna.scufl2.rdfxml.jaxb.Activity) {
				if (profileElem == null) {
					throw new ReaderException("No <Profile> found");
				}
				parseActivity((uk.org.taverna.scufl2.rdfxml.jaxb.Activity) any);

			} else if (any instanceof uk.org.taverna.scufl2.rdfxml.jaxb.ProcessorBinding) {
				if (profileElem == null) {
					throw new ReaderException("No <Profile> found");
				}

				parseProcessorBinding((uk.org.taverna.scufl2.rdfxml.jaxb.ProcessorBinding) any);
			} else if (any instanceof uk.org.taverna.scufl2.rdfxml.jaxb.Configuration) {
				if (profileElem == null) {
					throw new ReaderException("No <Profile> found");
				}
				parseConfiguration((uk.org.taverna.scufl2.rdfxml.jaxb.Configuration) any);
			}
		}
		parseProfileSecond(profileElem);
	}
}
