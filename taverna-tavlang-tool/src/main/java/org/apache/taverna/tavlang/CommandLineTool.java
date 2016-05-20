package org.apache.taverna.tavlang;

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

import io.airlift.airline.Arguments;
import io.airlift.airline.Cli;
import io.airlift.airline.Cli.CliBuilder;
import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.Option;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.tavlang.tools.convert.Scufl2Convert;
import org.apache.taverna.tavlang.tools.convert.ToRobundle;
import org.apache.taverna.tavlang.tools.inspect.ProcessorNames;
import org.apache.taverna.tavlang.tools.inspect.ServiceTypes;
import org.apache.taverna.tavlang.tools.stats.GetWfStat;
import org.apache.taverna.tavlang.tools.validate.Validate;

import com.google.common.collect.Lists;

/*
 * The command line options for convert, validate and inspect workflows.
 * Use the airlift/airline library
 * */

public class CommandLineTool {

	private static Cli<TvnLangTool> parser() {
		CliBuilder<TvnLangTool> build = Cli.<TvnLangTool> builder("tavlang")
				.withDescription("Convert, manage workflows")
				.withDefaultCommand(HelpCommand.class)
				.withCommand(CommandConvert.class) // Conversion
				.withCommand(HelpCommand.class) // Help
				.withCommand(CommandInspect.class) // Inspect
				.withCommand(CommandValidate.class) // Validate
				.withCommand(CommandVersion.class) // Version
				.withCommand(CommandStat.class); // Statistics

		return build.build();
	}

	public CommandLineTool() {
	};

	public void parse(String... args) {
		TvnLangTool command = parser().parse(args);
		command.execute();
		System.out.println();
	}

	public static abstract class TvnLangTool {

		public abstract void execute();
	}

	// placeholder for output file types
	public static class Filetypes {
		@Option(name = "--wfdesc", description = "Convert the workflow file to wfdesc-turtle")
		public static boolean isWfdesc = false;

		@Option(name = "--wfbundle", description = "Convert the workflow file to wfbundle")
		public static boolean isWfbundle = false;

		@Option(name = "--robundle", description = "Convert the given bundle into Research Object bundle")
		public static boolean isRo = false;

		@Option(name = "--structure", description = "Convert the workflow into *.structure")
		public static boolean isStructure = false;

		@Option(name = "--json", description = "Convert the workflow into json")
		public static boolean isJson = false;


		@Option(name = "--iwir", description = "Convert scufl2 workflows into IWIR 1.1 specification")
		public static boolean isIwir = false;


		// The tool can only handle one output format at a time.
		// Return the file type which is selected
		public static String isTrue() {
			if (isWfdesc)
				return "wfdesc";
			else if (isWfbundle)
				return "wfbundle";
			else if (isRo)
				return "robundle";
			else if (isStructure)
				return "structure";
			else if (isJson)
				return "json";

			else if (isIwir)
				return "iwir";


			else{
				System.out.println("Invalid argument....");
				TvnLangTool command = parser().parse("help", "convert");
				command.execute();
				return null;
			}

		}

	}

	public static class Inspect {
		@Option(name = "--servicetypes", description = "List the service types used in workflow")
		public static boolean servicetypes = false;

		@Option(name = "--processornames", description = "List a tree of processor names used in workflow")
		public static boolean processor = false;

		public String getWay() {
			if (servicetypes)
				return "servicetypes";
			else if (processor)
				return "processornames";
			else{
				System.out.println("Invalid argument....");
				TvnLangTool command = parser().parse("help", "inspect");
				command.execute();
				return null;

			}

		}

	}

	// Placeholder for optional parameters: Ex: -i, -o
	public static class Optional {

		// The input file or directory
		@Option(name = { "-i", "--input" }, description = "Input file/ file dir for conversion")
		public static String in_file_dir;

		// The out put file or directory. When this is set, all the converted
		// files will be saved into the directory that specified.
		@Option(name = { "-o", "--output" }, description = "Output file/ directory")
		public static String out_file_dir;

		public static String getInFile() {
			return in_file_dir;
		}

		public static String getOutFile() {
			return out_file_dir;
		}

	}

	@Command(name = "help", description = "Display help information about Taverna")
	public static class HelpCommand extends TvnLangTool {
		@Inject
		public Help help;

		@Override
		public void execute() {
			help.call();
		}
	}

	// Command for convert workflows
	@Command(name = "convert", description = "Convert the given workflow")
	public static class CommandConvert extends TvnLangTool {
		@Inject
		Optional optional = new Optional();

		@Inject
		Filetypes filetypes = new Filetypes();

		@Arguments(usage = "<output format> <input files> ", description = "List of files to be converted.\n "
				+ "Give the list of files to be converted without -i/-o and the converted files will be saved in to /converted folder in the same dir")
		public final List<String> files = Lists.newArrayList();

		// When this is true, the command will run recursively in a directory.
		@Option(name = { "-r", "--recursive" }, description = "Execute the command recursively")
		public boolean recurse = false;

		// Option for validate the given workflow when converting
		@Option(name = { "-V", "--validate" }, description = "Validate the workflow before convert")
		public boolean validate = false;

		@Override
		public void execute() {

			//Validate before convert
			if(validate){
				Validate validate = new Validate(files, null, false);
				if(validate.getCheck()){
					System.out.println("Conversion failed...! Workflow bundle has errors");
					return;
				}
			}
			runcommand();
		}

		public void runcommand(){
			if (Filetypes.isRo) {
				try {
					new ToRobundle(files, Optional.getOutFile());
				} catch (Exception e) {
					e.printStackTrace();
				}
//				else if(Filetypes.isIwir || Filetypes.isJson || Filetypes.isStructure || Filetypes.isWfbundle || Filetypes.isWfdesc)
			} else if(Filetypes.isIwir || Filetypes.isJson || Filetypes.isStructure || Filetypes.isWfbundle || Filetypes.isWfdesc){
				if (recurse) {
					new Scufl2Convert(Filetypes.isTrue(),
							Optional.getInFile(), Optional.getOutFile());
				} else {
					new Scufl2Convert(Filetypes.isTrue(), files,
							Optional.getOutFile());
				}
			}else{
				System.out.println("Invalid argument....");
				TvnLangTool command = parser().parse("help", "convert");
				command.execute();
			}

		}



	}

	// Version command
	@Command(name = "version", description = "Show version information")
	public static class CommandVersion extends TvnLangTool {

		@Override
		public void execute() {
			System.out.print(getClass().getPackage().getImplementationTitle());
            System.out.println(" " + getClass().getPackage().getImplementationVersion());
		}

	}

	// Command for inspection of workflows....!!
	@Command(name = "inspect", description = "Inspect the given workflow and show the results on the terminal")
	public static class CommandInspect extends TvnLangTool {

		@Inject
		Inspect inspect = new Inspect();

		@Option(name = { "-l", "--log" }, description = "Specify the file name where results should be stored ([some dir]/log.txt)")
		public String file;

		@Arguments(usage = "<option> <input files>", description = "Inspect the given workflow")
		public List<String> toInspect = Lists.newArrayList();

		@Override
		public void execute() {
			if (Inspect.processor) {
				try {
					new ProcessorNames(toInspect, file);

				} catch (ReaderException | IOException | JAXBException e) {
					e.printStackTrace();
				}
			} else if (Inspect.servicetypes) {
				try {
					new ServiceTypes(toInspect, file);
				} catch (IOException | ReaderException e) {
					e.printStackTrace();
				}

			}else{
				System.out.println("Invalid argument....");
				TvnLangTool command = parser().parse("help", "inspect");
				command.execute();
			}

		}

	}

	// Command for validation
	@Command(name = "validate", description = "Validate the given workflow")
	public static class CommandValidate extends TvnLangTool{

		@Option(name = { "-l", "--log" }, description = "Specify the file name where results should be stored ([some dir]/log.txt)")
		public String file;

		@Option(name = {"-v", "--verbose"}, description = "Verbose mode")
		public boolean verbose;

//		@Arguments(usage = "<option> <input files> <output dir>", description = "Validate the given workflow file/s")
		@Arguments(usage = "input files", description = "Validate the given workflow file/s")
		public List<String> toValidate = Lists.newArrayList();

		@Override
		public void execute() {

			Validate validate = new Validate(toValidate, file, verbose);


		}

	}

	//Command for getting workflow stats
	@Command (name = "stats", description = "Shows the workflow statistics")
	public static class CommandStat extends TvnLangTool{


		@Option(name = {"-l", "--log"}, description ="Save the workflow statistics in a file")
		public String file;

		@Option(name = {"-v", "--verbose"}, description = "Verbose mode")
		public boolean verbose;

		@Arguments(usage="input files", description = "Enter the workflow bundle files")
		public List<String> files;

		@Override
		public void execute() {

			GetWfStat stat = new GetWfStat(files, file, verbose);

		}

	}

}
