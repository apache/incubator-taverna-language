package org.apache.taverna.scufl2.api.io.structure;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import static java.lang.System.arraycopy;
import static java.util.Collections.singleton;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.BlockingControlLink;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleReader;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.InputWorkflowPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputWorkflowPort;
import org.apache.taverna.scufl2.api.port.ReceiverPort;
import org.apache.taverna.scufl2.api.port.SenderPort;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A <code>WorkflowBundleReader</code> that reads a {@link WorkflowBundle} in
 * Scufl2 Structure format.
 */
public class StructureReader implements WorkflowBundleReader {
	public enum Level {
		WorkflowBundle, Workflow, Processor, Activity, Links, Profile, Configuration, ProcessorBinding, OutputPortBindings, InputPortBindings, JSON, Controls
	}

	private static final String ACTIVITY_SLASH = "activity/";
	public static final String TEXT_VND_TAVERNA_SCUFL2_STRUCTURE = "text/vnd.taverna.scufl2.structure";

	private WorkflowBundle wb;
	private Level level;
	private String mainWorkflow;
	private Workflow workflow;
	private Processor processor;
	private Activity activity;
	Pattern linkPattern = Pattern
			.compile("'(.*[^\\\\])'\\s->\\s'(.*[^\\\\\\\\])'");
	Pattern blockPattern = Pattern
			.compile("\\s*block\\s+'(.*[^\\\\])'\\s+until\\s+'(.*[^\\\\\\\\])'\\s+finish");
	private String mainProfile;
	private Profile profile;
	private Configuration configuration;
	private ProcessorBinding processorBinding;
    protected Scanner scanner;

	@Override
	public Set<String> getMediaTypes() {
		return singleton(TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
	}

	// synchronized because we share wb/scanner fields across the instance
	protected synchronized WorkflowBundle parse(InputStream is)
			throws ReaderException {
		scanner = new Scanner(is);
		try {
			wb = new WorkflowBundle();
			while (scanner.hasNextLine())
				parseLine(scanner.nextLine());
			return wb;
		} finally {
			scanner.close();
		}
	}

	protected void parseLine(final String nextLine) throws ReaderException {
		try (Scanner scanner = new Scanner(nextLine.trim())) {
			// In case it is the last line
			if (!scanner.hasNext())
				return;
			// allow any whitespace
			String next = scanner.next();

			if (next.isEmpty())
				return;
			switch (next) {
			case "WorkflowBundle":
				parseWorkflowBundle(scanner);
				return;
			case "MainWorkflow":
				mainWorkflow = parseName(scanner);
				return;
			case "Workflow":
				parseWorkflow(scanner);
				return;
			case "In":
			case "Out":
				parsePort(scanner, next);
				return;
			case "Links":
				level = Level.Links;
				processor = null;
				return;
			case "Controls":
				level = Level.Controls;
				return;
			case "MainProfile":
				mainProfile = parseName(scanner);
				return;
			case "Profile":
				parseProfile(scanner);
				return;
			case "Type":
				parseType(nextLine);
				return;
			case "ProcessorBinding":
				parseProcessorBinding(scanner);
				return;
			case "InputPortBindings":
				level = Level.InputPortBindings;
				return;
			case "OutputPortBindings":
				level = Level.OutputPortBindings;
				return;
			case "Configuration":
				parseConfiguration(scanner);
				return;
			case "Configures":
				parseConfigures(scanner);
				return;
			case "Activity":
				switch (level) {
				case Profile:
				case Activity:
					level = Level.Activity;
					activity = new Activity();
					activity.setName(parseName(scanner));
					profile.getActivities().add(activity);
					return;
				case ProcessorBinding:
					Activity boundActivity = profile.getActivities().getByName(
							parseName(scanner));
					processorBinding.setBoundActivity(boundActivity);
					return;
				default:
					break;
				}
				break;
			case "Processor":
				switch (level) {
				case Workflow:
				case Processor:
					level = Level.Processor;
					processor = new Processor();
					processor.setName(parseName(scanner));
					processor.setParent(workflow);
					workflow.getProcessors().add(processor);
					return;
				case ProcessorBinding:
					String[] wfProcName = parseName(scanner).split(":");
					Workflow wf = wb.getWorkflows().getByName(wfProcName[0]);
					Processor boundProcessor = wf.getProcessors().getByName(
							wfProcName[1]);
					processorBinding.setBoundProcessor(boundProcessor);
					return;
				default:
					break;
				}
				break;
			}

			if (next.equals("block")) {
				Matcher blockMatcher = blockPattern.matcher(nextLine);
				blockMatcher.find();
				String block = blockMatcher.group(1);
				String untilFinish = blockMatcher.group(2);

				Processor blockProc = workflow.getProcessors().getByName(block);
				Processor untilFinishedProc = workflow.getProcessors()
						.getByName(untilFinish);
				new BlockingControlLink(blockProc, untilFinishedProc);
			}
			if (next.startsWith("'") && level.equals(Level.Links)) {
				Matcher linkMatcher = linkPattern.matcher(nextLine);
				linkMatcher.find();
				String firstLink = linkMatcher.group(1);
				String secondLink = linkMatcher.group(2);

				SenderPort senderPort;
				if (firstLink.contains(":")) {
					String[] procPort = firstLink.split(":");
					Processor proc = workflow.getProcessors().getByName(
							procPort[0]);
					senderPort = proc.getOutputPorts().getByName(procPort[1]);
				} else
					senderPort = workflow.getInputPorts().getByName(firstLink);

				ReceiverPort receiverPort;
				if (secondLink.contains(":")) {
					String[] procPort = secondLink.split(":");
					Processor proc = workflow.getProcessors().getByName(
							procPort[0]);
					receiverPort = proc.getInputPorts().getByName(procPort[1]);
				} else
					receiverPort = workflow.getOutputPorts().getByName(
							secondLink);

				new DataLink(workflow, senderPort, receiverPort);
				return;
			}

			if (next.startsWith("'")
					&& (level == Level.InputPortBindings || level == Level.OutputPortBindings)) {
				Matcher linkMatcher = linkPattern.matcher(nextLine);
				linkMatcher.find();
				String firstLink = linkMatcher.group(1);
				String secondLink = linkMatcher.group(2);
				if (level == Level.InputPortBindings) {
					InputProcessorPort processorPort = processorBinding
							.getBoundProcessor().getInputPorts()
							.getByName(firstLink);
					InputActivityPort activityPort = processorBinding
							.getBoundActivity().getInputPorts()
							.getByName(secondLink);
					new ProcessorInputPortBinding(processorBinding,
							processorPort, activityPort);
				} else {
					OutputActivityPort activityPort = processorBinding
							.getBoundActivity().getOutputPorts()
							.getByName(firstLink);
					OutputProcessorPort processorPort = processorBinding
							.getBoundProcessor().getOutputPorts()
							.getByName(secondLink);
					new ProcessorOutputPortBinding(processorBinding,
							activityPort, processorPort);
				}
				return;
			}
			if (level == Level.JSON) {
				/*
				 * A silly reader that feeds (no more than) a single line at a
				 * time from our parent scanner, starting with the current line
				 */
				Reader reader = new Reader() {
					char[] line = nextLine.toCharArray();
					int pos = 0;

					@Override
					public int read(char[] cbuf, int off, int len)
							throws IOException {
						if (pos >= line.length) {
							// Need to read next line to fill buffer
							if (!StructureReader.this.scanner.hasNextLine())
								return -1;
							String newLine = StructureReader.this.scanner
									.nextLine();
							pos = 0;
							line = newLine.toCharArray();
							// System.out.println("Read new line: " + newLine);
						}
						int length = Math.min(len, line.length - pos);
						if (length <= 0)
							return 0;
						arraycopy(line, pos, cbuf, off, length);
						pos += length;
						return length;
					}

					@Override
					public void close() throws IOException {
						line = null;
					}
				};            

				ObjectMapper mapper = new ObjectMapper();
				try {
					JsonParser parser = mapper.getFactory()
							.createParser(reader);
					JsonNode jsonNode = parser.readValueAs(JsonNode.class);
					// System.out.println("Parsed " + jsonNode);
					configuration.setJson(jsonNode);
				} catch (IOException e) {
					throw new ReaderException("Can't parse json", e);
				}
				level = Level.Configuration;
				return;
			}
		}
    }

	private void parseWorkflowBundle(Scanner scanner) {
		level = Level.WorkflowBundle;
		String name = parseName(scanner);
		wb.setName(name);
	}

	private void parseWorkflow(Scanner scanner) {
		level = Level.Workflow;
		workflow = new Workflow();
		String workflowName = parseName(scanner);
		workflow.setName(workflowName);
		wb.getWorkflows().add(workflow);
		if (workflowName.equals(mainWorkflow))
			wb.setMainWorkflow(workflow);
	}

	private void parsePort(Scanner scanner, String next) throws ReaderException {
		boolean in = next.equals("In");
		String portName = parseName(scanner);
		switch (level) {
		case Workflow:
			if (in)
				new InputWorkflowPort(workflow, portName);
			else
				new OutputWorkflowPort(workflow, portName);
			break;
		case Processor:
			if (in)
				new InputProcessorPort(processor, portName);
			else
				new OutputProcessorPort(processor, portName);
			break;
		case Activity:
			if (in)
				new InputActivityPort(activity, portName);
			else
				new OutputActivityPort(activity, portName);
			break;
		default:
			throw new ReaderException("Unexpected " + next + " at level "
					+ level);
		}
	}

	private void parseProfile(Scanner scanner) {
		level = Level.Profile;
		profile = new Profile();
		String profileName = parseName(scanner);
		profile.setName(profileName);
		wb.getProfiles().add(profile);
		if (profileName.equals(mainProfile))
			wb.setMainProfile(profile);
	}

	private void parseType(String nextLine) {
		URI uri = URI.create(nextLine.split("[<>]")[1]);
		switch (level) {
		case Activity:
			activity.setType(uri);
			break;
		case Configuration:
			configuration.setType(uri);
			break;
		default:
			break;
		}
	}

	private void parseProcessorBinding(Scanner scanner) {
		level = Level.ProcessorBinding;
		processorBinding = new ProcessorBinding();
		String bindingName = parseName(scanner);
		processorBinding.setName(bindingName);
		profile.getProcessorBindings().add(processorBinding);
	}

	private void parseConfiguration(Scanner scanner) {
		level = Level.Configuration;
		configuration = new Configuration();
		String configName = parseName(scanner);
		configuration.setName(configName);
		profile.getConfigurations().add(configuration);
	}

	private void parseConfigures(Scanner scanner) {
		String configures = parseName(scanner);
		if (!configures.startsWith(ACTIVITY_SLASH))
			throw new UnsupportedOperationException("Unknown Configures "
					+ configures);
		activity = profile.getActivities().getByName(
				configures.substring(ACTIVITY_SLASH.length(),
						configures.length()));
		configuration.setConfigures(activity);
		level = Level.JSON;
	}


	private String parseName(Scanner scanner) {
		String name = scanner.findInLine("'(.*[^\\\\])'");
		return name.substring(1, name.length() - 1);
	}

	@Override
	public WorkflowBundle readBundle(File bundleFile, String mediaType)
			throws IOException, ReaderException {
		try (BufferedInputStream is = new BufferedInputStream(
				new FileInputStream(bundleFile))) {
			return parse(is);
		}
	}

	@Override
	public WorkflowBundle readBundle(InputStream inputStream, String mediaType)
			throws IOException, ReaderException {
		return parse(inputStream);
	}

	@Override
	public String guessMediaTypeForSignature(byte[] firstBytes) {
		if (new String(firstBytes, Charset.forName("ISO-8859-1"))
				.contains("WorkflowBundle '"))
			return TEXT_VND_TAVERNA_SCUFL2_STRUCTURE;
		return null;
	}
}
