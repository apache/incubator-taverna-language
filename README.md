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
# Apache Taverna Language

Apache Taverna Language is a set of APIs for workflow definitions (SCUFL2)
and workflow inputs/outputs/run (DataBundle), as consumed and produced
by the [Apache Taverna](http://taverna.incubator.apache.org/) (incubating)
workflow system.

The API includes support for the legacy formats from Taverna 2 and Taverna
1, and therefore can be also used independently of Apache Taverna 3. 

## License

(c) 2015 Apache Software Foundation

This product includes software developed at The [Apache Software
Foundation](http://www.apache.org/).

Licensed under the 
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0), see the file
[LICENSE](LICENSE) for details.

The file [NOTICE](NOTICE) contain any additional attributions and
details about embedded third-party libraries and source code.


## Contribute

Feel free to contribute by [submitting an issue](https://issues.apache.org/jira/browse/TAVERNA), send a [pull request](https://github.com/apache/incubator-taverna-language/pulls), or discuss the project at the [dev@taverna mailing list](http://taverna.incubator.apache.org/community/lists#devtaverna).

All contributions are assumed to be licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0), unless otherwise noted.

## Modules

Official modules:

* [taverna-robundle](taverna-robundle/) load/save/manage [Research Object bundles](https://w3id.org/bundle), (ZIP-based container)
* [taverna-databundle](taverna-databundle/) load/save RO bundle with workflow
  inputs/outputs/workflow run provenance
* [taverna-scufl2-api](taverna-scufl2-api/) Inspect and build SCUFL2 workflow
  definitions 
* [taverna-scufl2-wfbundle](taverna-scufl2-wfbundle/) load/save Apache Taverna 3 `.wfbundle` workflows
* [taverna-scufl2-t2flow](taverna-scufl2-t2flow/) load Taverna 2 `.t2flow` workflows
* [taverna-scufl2-schemas](taverna-scufl2-schemas) SCUFL2 XML Schemas and OWL ontologies
* [taverna-scufl2-wfdesc](taverna-scufl2-wfdesc/) save [wfdesc](https://w3id.org/ro#wfdesc) workflow structure as RDF Turtle 
* [taverna-scufl2-examples](taverna-scufl2-examples) Example usage of the SCUFL2 APIs

Experimental modules:

* [taverna-scufl2-scufl](taverna-scufl2-scufl/) Load Taverna 1 `.xml` workflows (SCUFL 1)
* [taverna-scufl2-annotation](taverna-scufl2-annotation/) Annotation utillity functions
* [taverna-baclava-language](taverna-baclava-language) Load Taverna 2 data documents (wokrkflow inputs/outputs)

Deprecated modules:
* [taverna-scufl2-ucfpackage](taverna-scufl2-ucfpackage/): API for [Adobe
  UCF](https://wikidocs.adobe.com/wiki/display/PDFNAV/Universal+Container+Format)
  ZIP files - superseeded by `taverna-robundle`


For more details, see the READMEs of:

* [taverna-robundle](taverna-robundle/)
* [taverna-databundle](taverna-databundle/)
* [taverna-scufl2-api](taverna-scufl2-api/) 


## Prerequisites

* Java 1.7 or newer (tested with OpenJDK 1.8)
* [Apache Maven](https://maven.apache.org/download.html) 3.2.5 or newer (older
  versions probably also work)


# Building

To build Apache Taverna Language, use:

    mvn clean install

This will build each module and run their tests, producing JARs like
`taverna-scufl2-api/target/taverna-scufl2-api-0.15.0-incubating.jar`. 
Some of the experimental modules are not built automatically, to build
them separately, run the same command from within their folder.

## Skipping tests

To skip the tests (these can be timeconsuming), use:

    mvn clean install -DskipTests


If you are modifying this source code independent of the
Apache Taverna project, you may not want to run the
[Rat Maven plugin](https://creadur.apache.org/rat/apache-rat-plugin/)
that enforces Apache headers in every source file - to disable it, try:

    mvn clean install -Drat.skip=true



Usage
-----

Apache Taverna Language is a Maven project, and the easiest way to use it is
from other Maven projects.

Released binaries of Apache Taverna Language are published in [Maven
Central](http://search.maven.org/).

Typical users of this API will depend on these modules:

		<dependency>
			<groupId>org.apache.taverna.language</groupId>
			<artifactId>taverna-robundle</artifactId>
			<version>0.15.0-incubating</version>
		</dependency>
		<dependency>
			<groupId>org.apache.taverna.language</groupId>
			<artifactId>taverna-databundle</artifactId>
			<version>0.15.0-incubating</version>
		</dependency>
		<dependency>
			<groupId>org.apache.taverna.language</groupId>
			<artifactId>taverna-scufl2-api</artifactId>
			<version>0.15.0-incubating</version>
		</dependency>
		<dependency>
			<groupId>org.apache.taverna.language</groupId>
			<artifactId>taverna-scufl2-wfbundle</artifactId>
			<version>0.15.0-incubating</version>
		</dependency>
		<dependency>
			<groupId>org.apache.taverna.language</groupId>
			<artifactId>taverna-scufl2-t2flow</artifactId>
			<version>0.15.0-incubating</version>
		</dependency> 

To find the latest `<version>` to use above (this README might
not have been updated), see the [Apache Taverna Language downloads]
(http://taverna.incubator.apache.org/download/language/).

All Scufl2 modules are also valid [OSGi](http://www.osgi.org/) bundles, see the
OSGi section below.  

You can alternatively copy and add these JARs from the build to add
to your classpath:

* `taverna-robundle/target/taverna-scufl2-robundle-0.15.0-incubating.jar`
* `taverna-databundle/target/taverna-scufl2-databundle-0.15.0-incubating.jar`
* `taverna-scufl2-api/target/taverna-scufl2-api-0.15.0-incubating.jar`
* `taverna-scufl2-wfbundle/target/taverna-scufl2-wfbundle-0.15.0-incubating.jar`
* `taverna-scufl2-ucfpackage/target/taverna-scufl2-ucfpackage-0.15.0-incubating.jar`
* `taverna-scufl2-t2flow/target/taverna-scufl2-t2flow-0.15.0-incubating.jar`

## Javadoc

See the [Taverna Language
Javadoc](http://taverna.incubator.apache.org/javadoc/taverna-language/) for
documentation of classes and methods of Taverna Language.  Good starting
points:
 * [org.apache.taverna.scufl2.api](http://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/scufl2/api/package-summary.html)
 * [org.apache.taverna.robundle](http://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/robundle/package-summary.html)
 * [org.apache.taverna.databundle](http://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/databundle/package-summary.html)


