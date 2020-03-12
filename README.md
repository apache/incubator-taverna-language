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


## Taverna Project Retired

> tl;dr: The Taverna code base is **no longer maintained** 
> and is provided here for archival purposes.

From 2014 till 2020 this code base was maintained by the 
[Apache Incubator](https://incubator.apache.org/) project _Apache Taverna (incubating)_
(see [web archive](https://web.archive.org/web/20200312133332/https://taverna.incubator.apache.org/)
and [podling status](https://incubator.apache.org/projects/taverna.html)).

In 2020 the Taverna community 
[voted](https://lists.apache.org/thread.html/r559e0dd047103414fbf48a6ce1bac2e17e67504c546300f2751c067c%40%3Cdev.taverna.apache.org%3E)
to **retire** Taverna as a project and withdraw the code base from the Apache Software Foundation. 

This code base remains available under the Apache License 2.0 
(see _License_ below), but is now simply called 
_Taverna_ rather than ~~Apache Taverna (incubating)~~.

While the code base is no longer actively maintained, 
Pull Requests are welcome to the 
[GitHub organization taverna](http://github.com/taverna/), 
which may infrequently be considered by remaining 
volunteer caretakers.


### Previous releases

Releases 2015-2018 during incubation at Apache Software Foundation
are available from the ASF Download Archive <https://archive.apache.org/dist/incubator/taverna/>

Releases 2014 from the University of Manchester are on BitBucket <https://bitbucket.org/taverna/>

Releases 2009-2013 from myGrid are on LaunchPad <https://launchpad.net/taverna/>

Releases 2003-2009 are on SourceForge <https://sourceforge.net/projects/taverna/files/taverna/>

Binary JARs for Taverna are available from 
Maven Central <https://repo.maven.apache.org/maven2/org/apache/taverna/>
or the myGrid Maven repository <https://repository.mygrid.org.uk/>



# Taverna Language

Taverna Language is a set of APIs for workflow definitions (SCUFL2),
Research Object Bundles and workflow inputs/outputs/run (DataBundle), 
as consumed and produced by the 
[Taverna](https://web.archive.org/web/*/https://taverna.incubator.apache.org/) workflow system.

The API includes support for the legacy formats from Taverna 2 and Taverna
1, and can be also used independently of Taverna 3.

The command line tool `tavlang` can be used for conversion and
inspection of research objects and workflow bundles.

## License

* (c) 2010-2014 University of Manchester
* (c) 2014-2020 Apache Software Foundation

This product includes software developed at The [Apache Software
Foundation](http://www.apache.org/).

Licensed under the
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0), see the file
[LICENSE](LICENSE) for details.

The file [NOTICE](NOTICE) contains any additional attributions and
details about embedded third-party libraries and source code.


# Contribute

Any contributions received are assumed to be covered by the [Apache License
2.0](https://www.apache.org/licenses/LICENSE-2.0). 



## Modules

Official modules:

* [taverna-robundle](taverna-robundle/) load/save/manage [Research Object bundles](https://w3id.org/bundle), (ZIP-based container)
* [taverna-databundle](taverna-databundle/) load/save RO bundle with workflow
  inputs/outputs/workflow run provenance
* [taverna-scufl2-api](taverna-scufl2-api/) Inspect and build SCUFL2 workflow
  definitions
* [taverna-scufl2-wfbundle](taverna-scufl2-wfbundle/) load/save Taverna 3 `.wfbundle` workflows
* [taverna-scufl2-t2flow](taverna-scufl2-t2flow/) load Taverna 2 `.t2flow` workflows
* [taverna-scufl2-schemas](taverna-scufl2-schemas) SCUFL2 XML Schemas and OWL ontologies
* [taverna-scufl2-wfdesc](taverna-scufl2-wfdesc/) save [wfdesc](https://w3id.org/ro#wfdesc) workflow structure as RDF Turtle
* [taverna-scufl2-examples](taverna-scufl2-examples) Example usage of the SCUFL2 APIs
* [taverna-tavlang-tool](taverna-tavlang-tool) Taverna Language tool `tavlang`

Experimental modules:

* [taverna-scufl2-cwl](taverna-scufl2-cwl/) Load/save [Common Workflow Language](https://www.commonwl.org/)
* [taverna-scufl2-scufl](taverna-scufl2-scufl/) Load Taverna 1 `.xml` workflows (SCUFL 1)
* [taverna-scufl2-annotation](taverna-scufl2-annotation/) Annotation utillity functions
* [taverna-baclava-language](taverna-baclava-language) Load Taverna 2 data documents (workflow inputs/outputs)

Deprecated modules:
* [taverna-scufl2-ucfpackage](taverna-scufl2-ucfpackage/): API for [Adobe
  UCF](https://wikidocs.adobe.com/wiki/display/PDFNAV/Universal+Container+Format)
  ZIP files - superseeded by `taverna-robundle`


For more details, see the READMEs of:

* [taverna-robundle](taverna-robundle/)
* [taverna-databundle](taverna-databundle/)
* [taverna-scufl2-api](taverna-scufl2-api/)


## Prerequisites

* Java JDK 8 or 9
* [Apache Maven](https://maven.apache.org/download.html) 3.5.3 or newer (older
  versions probably also work)


# Building

To build Taverna Language, use:

    mvn clean install

This will build each module and run their tests, producing JARs like
`taverna-scufl2-api/target/taverna-scufl2-api-0.16.0-incubating.jar`.
Some of the experimental modules are not built automatically, to build
them separately, run the same command from within their folder.


## Building on Windows

If you are building on Windows, ensure you unpack this source code
to a folder with a [short path name](http://stackoverflow.com/questions/1880321/why-does-the-260-character-path-length-limit-exist-in-windows) 
lenght, e.g. `C:\src` - as 
Windows has a [limitation on the total path length](https://msdn.microsoft.com/en-us/library/aa365247%28VS.85%29.aspx#maxpath) 
which might otherwise prevent this code from building successfully.

## Skipping tests

To skip the tests (these can be timeconsuming), use:

    mvn clean install -DskipTests


If you are modifying this source code independent of the
Taverna project, you may not want to run the
[Rat Maven plugin](https://creadur.apache.org/rat/apache-rat-plugin/)
that enforces Apache headers in every source file - to disable it, try:

    mvn clean install -Drat.skip=true

## Building for Android

To use the workflow parsers in Android you need to use the `android` profile. This excludes any  
modules which use Java 8 features (Java 7 source is ok for Android API 19 and above eg `scufl2-ufcpackage` uses the diamond operator)  

    mvn -P android clean install


Usage
-----

Taverna Language is a Maven project, and the easiest way to use it is
from other Maven projects.

Released binaries of Taverna Language are published in [Maven
Central](http://search.maven.org/).

Typical users of this API will depend on these modules:

		<dependency>
			<groupId>org.apache.taverna.language</groupId>
			<artifactId>taverna-robundle</artifactId>
			<version>0.16.0-incubating</version>
		</dependency>
		<dependency>
			<groupId>org.apache.taverna.language</groupId>
			<artifactId>taverna-databundle</artifactId>
			<version>0.16.0-incubating</version>
		</dependency>
		<dependency>
			<groupId>org.apache.taverna.language</groupId>
			<artifactId>taverna-scufl2-api</artifactId>
			<version>0.16.0-incubating</version>
		</dependency>
		<dependency>
			<groupId>org.apache.taverna.language</groupId>
			<artifactId>taverna-scufl2-wfbundle</artifactId>
			<version>0.16.0-incubating</version>
		</dependency>
		<dependency>
			<groupId>org.apache.taverna.language</groupId>
			<artifactId>taverna-scufl2-t2flow</artifactId>
			<version>0.16.0-incubating</version>
		</dependency>

To find the latest `<version>` to use above (this README might
not have been updated), see the [Taverna Language downloads]
(https://web.archive.org/web/*/https://taverna.incubator.apache.org/download/language/).

All Scufl2 modules are also valid [OSGi](http://www.osgi.org/) bundles,
providing [OSGi services](taverna-scufl2-api#osgi-services).


You can alternatively copy and add these JARs from the build to add
to your classpath:

* `taverna-robundle/target/taverna-scufl2-robundle-0.16.0-incubating.jar`
* `taverna-databundle/target/taverna-scufl2-databundle-0.16.0-incubating.jar`
* `taverna-scufl2-api/target/taverna-scufl2-api-0.16.0-incubating.jar`
* `taverna-scufl2-wfbundle/target/taverna-scufl2-wfbundle-0.16.0-incubating.jar`
* `taverna-scufl2-ucfpackage/target/taverna-scufl2-ucfpackage-0.16.0-incubating.jar`
* `taverna-scufl2-t2flow/target/taverna-scufl2-t2flow-0.16.0-incubating.jar`

## Javadoc

See the [Taverna Language
Javadoc](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/) for
documentation of classes and methods of Taverna Language.  Good starting
points:
 * [org.apache.taverna.scufl2.api](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/scufl2/api/package-summary.html)
 * [org.apache.taverna.robundle](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/robundle/package-summary.html)
 * [org.apache.taverna.databundle](https://web.archive.org/web/*/https://taverna.incubator.apache.org/javadoc/taverna-language/org/apache/taverna/databundle/package-summary.html)


# Export restrictions

This distribution includes cryptographic software.
The country in which you currently reside may have restrictions
on the import, possession, use, and/or re-export to another country,
of encryption software. BEFORE using any encryption software,
please check your country's laws, regulations and policies
concerning the import, possession, or use, and re-export of
encryption software, to see if this is permitted.
See <http://www.wassenaar.org/> for more information.

The U.S. Government Department of Commerce, Bureau of Industry and Security (BIS),
has classified this software as Export Commodity Control Number (ECCN) 5D002.C.1,
which includes information security software using or performing
cryptographic functions with asymmetric algorithms.
The form and manner of this Apache Software Foundation distribution makes
it eligible for export under the License Exception
ENC Technology Software Unrestricted (TSU) exception
(see the BIS Export Administration Regulations, Section 740.13)
for both object code and source code.

The following provides more details on the included cryptographic software:

* The shaded JAR of [taverna-tavlang-tool](taverna-tavlang-tool) include
  [Apache HttpComponents](https://hc.apache.org/)
  Core and Client,
  which can initiate encrypted `https://` connections using
  [Java Secure Socket Extension](https://docs.oracle.com/javase/8/docs/technotes/guides/security/jsse/JSSERefGuide.html)
  (JSSE).
