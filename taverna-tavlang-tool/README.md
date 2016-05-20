<!--
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->

# Apache Taverna tavlang tool


This is a command line for the [Apache Taverna
Language](https://github.com/apache/incubator-taverna-language/),
an API for the
[Apache Taverna](http://taverna.incubator.apache.org/) workflows.


# Usage

	usage: tavlang <command> [<args>]

	The most commonly used tavlang commands are:
	    convert    Convert the given workflow
	    help       Display help information about Taverna
	    inspect    Inspect the given workflow and show the results on the terminal
	    validate   Validate the given workflow
	    version    Show version informantion

	See 'tavlang help <command>' for more information on a specific command.

# Documentation

The Taverna language command line tool is for accessing the features of the Taverna language modules.
The tool has the following functions:

* Conversion
* Inspection
* Validation
* Viewing workflow statistics.

## Command line structure

	$tavlang <command> <options> [arguments and parameters]

Each command has its own set of options.

###Commands and Options
The tool has the following major commands:

* convert: Options {-r -- recursive, -i --input, -o --output,}
* inspect: Options {-l --log}
* validate: Options {-l --log, -v --verbose}
* stats: Options {-l --log, -v --verbose}

## Commands in detail

### convert ------------------------------------------------------

Convert the given workflow file/s into a specified output format.

Usage:

	tavlang convert (--output_fomat) ([[options] [arguments]]|files...)

 Supported output formats are,

 * wfbundle
 * json
 * robundle
 * structure

Options:

 * -r, --recursive : Convert all workflow files in a given directory recursively
 * -i, --input     : Used with recursive case. Specify the input directory
 * -o, --output    : Specify the out put directory

*The tool can only convert into one format at a time

There are two methods for using the conversion command.

####  1. Non-recursive method

Usage:

	$tavlang convert (--output_format) [options] [arguments]

####### Example 1: Without specifying any options

	$tavlang convert --structure /helloworld.t2flow

Output:

	/converted/helloworld.structure is created

Convert the helloworld.t2flow into helloworld.structure format and store in the /converted directory

###### Example 2: Convert multiple workflows

	$tavlang convert --json /helloworld.t2flow /hello.t2flow

Output:

	/converted/helloworld.json is created
	/converted/hello.json is created

Convert both workflow files and store them in /converted directory

###### Example 3: with options and arguments

	$tavlang convert --wfbundle /helloworld.t2flow -o /output/workflows

Output:

	/output/workflows/helloworld.wfbundle is created

Convert the workflow file/s and store them in the specified output directory

####  2. Recursive method

If there are many workflows in a directory, which are needed to be converted into one single format use this method.

Usage:

	$tavlang convert -r (--output_format) -i workflow_src_dir <options> [arguments]

###### Example 4: Without options and arguments

	$tavlang convert -r --json -i /home/user/workflows

Convert all the workflows in the input directory into the specified format and store them in the "/home/user/workflows/converted" directory.

Output: Suppose that there are 2 workflow files in the dir 1.t2flow and 2.t2flow

	/home/workflows/converted/1.json is created
	/home/workflows/converted/2.json is created

###### Example 5: with output dir is specified

	$tavlang convert -r --json -i /home/workflows -o /home/final

Convert all the workflows in the input directory and store them in /home/final directory

Output:

	/home/final/1.json is created
	/home/final/2.json is created

### inspect -----------------------------------------------------------------

Inspect the given workflow file/s and give the inspection report.
Supported workflow bundle formats: .wfbundle, .t2flow

Usage:-

	$tavlang inspect (--inspection_options) <-other-options> [arguments] workflow_undles_to_be_inspected

Inspection options:

 * --servicetypes :- List all the service-types used in the workflow
 * --processornames :- List the tree of the processor names used in the workflow

Other Options:

 * -l, --log :- save the inspection report in a text file

###### Example 1: Without any other-options

1. Inspection result for Service types


	$tavlang inspect --servicetypes helloworld.wfbundle
	Service types used in helloworld.wfbundle :

	http://ns.taverna.org.uk/2010/activity/constant
	**************************************************

2. inspection result for Processor names


	$tavlang inspect --processornames helloworld.wfbundle
	Processor tree of helloworld.wfbundle
	+ Hello_World
	  - hello

###### Example 2: With other options
The output is the same but the results will be saved in the given file.

	$tavlang inspect --processornames helloworld.wfbundle -l results.txt
	Processor tree of helloworld.wfbundle
	+ Hello_World
	 - hello


The output will be saved in results.txt in the same format.

### validate ----------------------------------------------------------------------

Validates the given workflow file or files and gives the validation report.
Validate tool checks for the following problematic conditions.

 * Empty Iteration Strategy Top-Node Problems
 * Mismatch Configurable Type Problems
 * Non-Absolute URI Problems
 * Null Field Problems
 * Out-Of-Scope Value Problems
 * Port Mentioned Twice Problems
 * Port Missing From Iteration Strategy Stack Problems
 * Wrong Parent Problems
 * Incompatible Granular Depth Problems

Usage:-

	$tavlang validate [options][arguments] input_files

Options:-

 * -l, --log: Save the validation report in a text file
 * -v, --verbose: Verbose mode

Supported workflow bundle formats: .t2flow and .wfbundle

###### Example 1: Normal mode

  Validate one workflow bundle

	$tavlang validate helloworld.t2flow

Output:

	Validation started....
	Validating helloworld.t2flow
	The workflow helloworld.t2flow has no errors.

	Validation completed.......

  Validate more than one workflow bundles

	$tavlang validate ../../workflow2.t2flow ../../workflow3.wfbundle

	Validation started....
	Validating ../../workflow2.t2flow
	The workflow workflow2.t2flow has no errors.

	Validating ../../workflow3.wfbundle
	The workflow workflow3.t2flow has no errors.

	Validation completed.......


###### Example 2: Verbose mode

The report is more explanatory.

	$tavlang validate -v ../../workflow2.t2flow

	Validation started....
	Validating ../../workflow2.t2flow
	The workflow workflow2.t2flow has no errors.

	The validation report for defaultActivitiesTaverna2.wfbundle......
	--------------------------------------------------------------------------------
	-->NegativeValueProblems:- null

	-->EmptyIterationStrategyTopNodeProblems:- null

	-->MismatchConfigurableTypeProblems:- null

	-->NonAbsoluteURIProblems:- null

	-->NullFieldProblems:- null

	-->OutOfScopeValueProblems:- null

	-->PortMentionedTwiceProblems:- null

	-->PortMissingFromIterationStrategyStackProblems:- null

	-->WrongParentProblems:- null

	-->IncompatibleGranularDepthProblems:- null

	---------------------------------------------------------------------------------

###### Example 3: Saving results to a file

	$tavlang validate workflow2.t2flow -l log2.txt
	Validation started....
	Validating workflow2.t2flow
	The workflow helloworld.wfbundle has no errors.
	Validation completed.......

	Results were saved into log2.txt

### stats ----------------------------------------------------------------------------------

A workflow contains several resources.

* Processors
* Input ports
* Output ports
* Data links
* Control links

The stats command gives a report of the resources used in the workflow.

Usage:-

	$tavlang stats <options>[arguments] input_files...

Options:-

 * -l, – – log : Save results in a log file
 * -v, – – verbose : verbose mode

Supported workflow bundle formats:- .t2flow, .wfbundle

###### Example 1: Normal mode

	$tavlang helloworld.wfbundle
	>>> Statistics of the workflow bundle: helloworld.wfbundle <<<
	Name of the workflow = Hello_World
	 |--> Number of Processors = 1
	 |--> Number of Data Links = 1
	 |--> Number of Control Links = 0
	 |--> Number of Input ports = 0
	 |--> Number of Output Ports = 1

###### Example 2: Verbose mode

	$tavlang -v ../../../helloworld.wfbundle
	>>> Statistics of the workflow bundle: helloworld.wfbundle <<<
	Name of the workflow = Hello_World
	 |--> Number of Processors = 1
	 | |--> Processors:
	 |      |--> hello
	 |
	 |--> Number of Data Links = 1
	 | |--> Data Links
	 |      |--> DataLink value=>greeting
	 |
	 |--> Number of Control Links = 0
	 |--> Number of Input ports = 0
	 |--> Number of Output Ports = 1
	 | |--> Output Ports
	 |      |--> OutputWorkflowPort "greeting"

###### Example 3: Saving results in a file

	$tavlang -l ../../results.txt ../../../helloworld.wfbundle
	>>> Statistics of the workflow bundle: helloworld.wfbundle <<<
	Name of the workflow = Hello_World
	 |--> Number of Processors = 1
	 |--> Number of Data Links = 1
	 |--> Number of Control Links = 0
	 |--> Number of Input ports = 0
	 |--> Number of Output Ports = 1

	Results were saved into ../../results.txt

-----------------------------------------------------------------------------------------------------------------------------------------
