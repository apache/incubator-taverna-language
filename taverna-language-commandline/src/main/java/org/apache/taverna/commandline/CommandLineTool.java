package org.apache.taverna.commandline;


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

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;


/*
 * The command line options for convert, validate and inspect workflows.
 * Use the airlift/airline library
 * Author: Menaka Madushanka <menaka12350@gmail.com>
 * */

public class CommandLineTool {
	
	private Cli<TvnLangTool> parser(){
		CliBuilder<TvnLangTool> build = Cli.<TvnLangTool>builder("tavlang")
				.withDescription("Convert, manage workflows")
				.withDefaultCommand(HelpCommand.class)
				.withCommand(CommandConvert.class)
				.withCommand(HelpCommand.class)
				.withCommand(CommandInspect.class)
				.withCommand(CommandValidate.class)
				.withCommand(CommandVersion.class);
		
		return build.build();
	}

	public CommandLineTool(){};
	public void parse(String... args)
    {
        System.out.println("$ tavlang " + Joiner.on(" ").join(args));
        TvnLangTool command = parser().parse(args);
        command.execute();
        System.out.println();
    }
	
	public static abstract class TvnLangTool{

		public abstract void execute();
	}
	
	//placeholder for output file types
	public static class Filetypes{
		@Option(name= "-wfdesc", description="Convert the workflow file to wfdesc-turtle")
		public static boolean isWfdesc = false;
		
		@Option(name="-wfbundel", description="Convert the workflow file to wfbundel")
		public static boolean isWfbundel = false;
		
		@Option(name =  "-robundel", description = "Convert given bundel in to Research Object bundel")
		public static boolean isRo = false;
		
		@Option(name= "-structure", description = "Convert the workflow into *.structure")
		public static boolean isStructure = false;

		@Option(name = "-json", description = "Convert the workflow into json")
		public static boolean isJson = false;
		
		//The tool can only handle one output format at a time.
		//Return the file type which is selected
		public static String isTrue(){
			if(isWfdesc) return "wfdesc";
			else if(isWfbundel) return "wfbundel";
			else if(isRo) return "ro";
			else if(isStructure) return "structure";
			else if(isJson) return "json";
			else return null;
		}
		
	}
	
	public static class Inspect{
		@Option(name = "-servicetypes", description = "List the service types")
		public static boolean servicetypes = false;
		
		@Option(name = "-processornames", description = "List the processor names")
		public static boolean processor = false;
		
		public String getWay(){
			if(servicetypes) return "servicetypes";
			else if (processor) return "processornames";
			else return null;
		}
		
	}
	
	
	//Placeholder for optional parameters: Ex: -i, -o 
	public static class Optional{
		
		
		@Option(name={"-l", "--log"}, description = "Save a results to a file")
		public boolean log = false;
		
		//The input file or directory
		@Option(name = {"-i", "--input"}, description="Input file/ file dir for convertion")
		public static String in_file_dir;
		
		//The out put file or directory. When this is set, all the converted files will be saved into the directory that specified.
		@Option(name = {"-o", "--output"}, description="Output file/ directory")
		public static String out_file_dir;
		
		public static String getInFile(){
			return in_file_dir;
		}
		
		public static String getOutFile(){
			return out_file_dir;
		}

	}
	
	@Command(name = "help", description = "Display help information about Tvarna")
	public static class HelpCommand extends TvnLangTool{
		@Inject
	    public Help help;

	    @Override
	    public void execute(){
	    	help.call();
	    }
	}
	
	//Command for convert workflows
	@Command(name="convert", description="Convert the given workflow")
	public static class CommandConvert extends TvnLangTool{
		@Inject
		Optional optional = new Optional();
		
		@Inject
		Filetypes filetypes = new Filetypes();
		
		@Arguments(usage = "<output format> <input files> ", description = "List of files to be converted.\n "
				+ "Give the list of files to be converted without -i/-o and the converted files will be saved in to /converted folder in the same dir")
        public final List<String> files = Lists.newArrayList();
		
		//When this is true, the command will run recursively in a directory.
		@Option(name={"-r", "--recursive"}, description="Execute the command recursively")
		public boolean recurse = false;
		
		//Option for validate the given workflow when converting
		@Option(name = {"-V", "--validate"}, description="Validate the workflow before convert")
		public boolean validate = false;
		
		@Override
		public void execute(){
//			
//			if(Filetypes.isWfbundel){
//				if(!files.isEmpty() && optional.getInFile()!=null){
//					System.err.println("Unexpected arguments:"+" " + files.toString() + " "+ optional.getInFile());
//					return;
//				}
//				File dir = new File(Optional.getInFile());
//				File odir = new File(Optional.getOutFile());
//				if(!odir.exists()){
//					odir.mkdirs();
//				}
//				for(File f : dir.listFiles()){
////					(new ToWfbundel(f.toString(), odir.toString())).run();
////					System.out.println(f);
//					
//				}
//					
//			}
			
			
			
		}
		
	}
	
	//Version command
	@Command(name="version", description = "Show version informantion")
	public static class CommandVersion extends TvnLangTool{

		@Override
		public void execute() {
			// TODO Auto-generated method stub
			System.out.println("Apache Taverna Language Command line tool. \nVersion 1.0 ");
		}
		
	}
	
	//Command for inspection of workflows....!!
	@Command(name="inspect", description="Inspect the given workflow")
	public static class CommandInspect extends TvnLangTool{

		@Inject
		Optional optional = new Optional();
		
		@Inject
		Inspect inspect = new Inspect();
		
		@Override
		public void execute() {
			// TODO Auto-generated method stub
			
			
		}
		
	}
	
	//Command for validation
	@Command(name = "validate", description = "validate the given workflow")
	public static class CommandValidate extends TvnLangTool{

		@Inject
		Optional optional = new Optional();
		
		@Override
		public void execute() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
}
