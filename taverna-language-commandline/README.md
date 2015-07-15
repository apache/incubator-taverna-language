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
# Apache Taverna Language Command Line


This is planned as a command line for the
[Apache Taverna Language](https://github.com/apache/incubator-taverna-language/),
an API for the 
[Apache Taverna](http://taverna.incubator.apache.org/) workflows.

This module is **work in progress** as part of Google Summer of Code 2015.



## License

(c) 2015 Apache Software Foundation

This product includes software developed at The [Apache Software
Foundation](http://www.apache.org/).

Licensed under the [Apache License
2.0](https://www.apache.org/licenses/LICENSE-2.0), see the file
[../LICENSE](../LICENSE) for details.

The file [NOTICE](src/main/resources/NOTICE) contain any additional attributions and
details about embedded third-party libraries and source code.


# Contribute

Please subscribe to and contact the 
[dev@taverna](http://taverna.incubator.apache.org/community/lists#dev mailing list)
for any questions, suggestions and discussions about 
Apache Taverna Language Commandline.

Bugs and feature plannings are tracked in the Jira
[Issue tracker](https://issues.apache.org/jira/browse/TAVERNA/component/12326903)
under the `TAVERNA` component _GSOC Taverna Language Command line_. Feel free
to add an issue!

To suggest changes to this source code, feel free to raise a 
[GitHub pull request](https://github.com/apache/incubator-taverna-mobile/pulls).
Any contributions received are assumed to be covered by the [Apache License
2.0](https://www.apache.org/licenses/LICENSE-2.0). We might ask you 
to sign a [Contributor License Agreement](https://www.apache.org/licenses/#clas)
before accepting a larger contribution.


# Building and install requirements

## Requisites

* Java 1.7 or newer
* [Apache Maven](https://maven.apache.org/download.html) 3.2.5 or newer (older
  versions probably also work)


## Building

To build, run:

    mvn clean install


# Usage

	usage: tavlang <command> [<args>]
	
	The most commonly used tavlang commands are:
	    convert    Convert the given workflow
	    help       Display help information about Tvarna
	    inspect    Inspect the given workflow and show the results on the terminal
	    validate   validate the given workflow
	    version    Show version informantion
	
	See 'tavlang help <command>' for more information on a specific command.

# Documentation

Taverna language command line tool is for access the features of the Taverna language modules. 
The tool has following functionalities.

* Conversion
* Inspection
* Validation and Viewing workflow statistics.
	
## Command line structure 
	tavlang <command> <options> [arguments and parameters]
	
Each command has it's own set of options.

###Commands and Options


