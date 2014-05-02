Convert SCUFL2 to wfdesc
========================

[![Build Status](https://travis-ci.org/wf4ever/scufl2-wfdesc.svg?branch=master)](https://travis-ci.org/wf4ever/scufl2-wfdesc)

This is an extension to [Scufl2](https://github.com/mygrid/scufl2/) which
provides the export capability to wfdesc ontology from the
[Wf4Ever](http://www.wf4ever-project.org/) [2] RO 
[wfdesc ontology](https://w3id.org/ro#wfdesc).

This is exposed as a command line tool `scufl2-to-wfdesc`, but can also be
accessed programmatically as a plugin for the 
[SCUFL2 API](http://dev.mygrid.org.uk/wiki/display/developer/SCUFL2+API).


Download
--------
You can download the latest `scufl2-wfdesc-*-standalone.jar` (command line tool)
from the myGrid Maven repository:

* http://build.mygrid.org.uk/maven/repository/org/purl/wf4ever/scufl2-wfdesc/ 

For instance, for version 0.3.7:

* http://build.mygrid.org.uk/maven/repository/org/purl/wf4ever/scufl2-wfdesc/0.3.7/scufl2-wfdesc-0.3.7-standalone.jar


License
-------
(c) 2011-2014 University of MAnchester

scufl2-wfdesc is licensed under the [MIT license](http://opensource.org/licenses/MIT). See the file [LICENCE.txt](LICENCE.txt) for details

[SCUFL2 API](https://github.com/myGrid/scufl2/) is licensed under the [LGPL 2.1 license](https://www.gnu.org/licenses/lgpl-2.1.html) which applies
to the build binary.

Building
--------

Requirements:
 * [Java JDK 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or [OpenJDK 7](http://openjdk.java.net/install/
) (higher versions not tested)
 * [Maven 3](http://maven.apache.org/download.html) or higher   (for building)

To build, simply use `mvn clean install`

Example:

    : stain@ralph ~/src/wf4ever/scufl2-wfdesc;mvn clean install
    [INFO] Scanning for projects...
    [INFO]                                                                         
    [INFO] ------------------------------------------------------------------------
    [INFO] Building SCUFL2 to wfdesc 0.1-SNAPSHOT
    [INFO] ------------------------------------------------------------------------
    [INFO] 
    [INFO] --- maven-clean-plugin:2.4.1:clean (default-clean) @ scufl2-wfdesc ---
    [INFO] 
    [INFO] --- maven-antrun-plugin:1.6:run (default) @ scufl2-wfdesc ---
    [INFO] Executing tasks

    main:
        [mkdir] Created dir: /home/stain/stuff/src/wf4ever/scufl2-wfdesc/target/generated-sources
    (..)
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 46.734s
    [INFO] Finished at: Wed Jan 04 15:22:58 WET 2012
    [INFO] Final Memory: 18M/129M
    [INFO] ------------------------------------------------------------------------


Note that the first time you build, Maven will download several
required dependencies from Maven repositories. Depending on your network
connection this might take some time to complete.



Command line tool
-----------------

After building, the folder `target/scufl2-wfdesc` contains a
distribution of the command line tool `scufl2-to-wfdesc`, with launchers
generated for Unix and Windows.  You may copy and distribute this folder for a standalone usage of the
conversion tool. 

Alternatively, you can use the `target/scufl2-wfdesc-0.3.0-SNAPSHOT-standalone.jar` directly with 
`java -jar` - this JAR embeds all required libraries and hence can
be copied anywhere on your file system. This JAR is however not recommended for programmatic
use as it embeds third party libraries.

The command line tool takes a list of Taverna workflow filenames (supported file
types are `*.t2flow` and `*.wfbundle`). If no filenames are given, the tool
will read a workflow definition from STDIN and write wfdesc to STDOUT.
The argument `-h`  or `--help` should give a minimal help.



Example:

    : stain@ralph ~/src/wf4ever/scufl2-wfdesc; target/scufl2-wfdesc/bin/scufl2-to-wfdesc src/test/resources/helloworld.t2flow 
    Converted src/test/resources/helloworld.t2flow to src/test/resources/helloworld.wfdesc.ttl

The output is a [RDF Turtle](http://www.w3.org/TR/turtle/) document containing statements about the workflow structure
according to the [RO ontology wfdesc](https://w3id.org/ro#wfdesc) ontology.

    : stain@ralph ~/src/wf4ever/scufl2-wfdesc; cat src/test/resources/helloworld.wfdesc.ttl 

	@base <http://ns.taverna.org.uk/2010/workflowBundle/8781d5f4-d0ba-48a8-a1d1-14281bd8a917/workflow/Hello_World/> .
	@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
	@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
	@prefix owl: <http://www.w3.org/2002/07/owl#> .
	@prefix prov: <http://www.w3.org/ns/prov#> .
	@prefix wfdesc: <http://purl.org/wf4ever/wfdesc#> .
	@prefix wf4ever: <http://purl.org/wf4ever/wf4ever#> .
	@prefix roterms: <http://purl.org/wf4ever/roterms#> .
	@prefix dc: <http://purl.org/dc/elements/1.1/> .
	@prefix dcterms: <http://purl.org/dc/terms/> .
	@prefix comp: <http://purl.org/DP/components#> .
	@prefix dep: <http://scape.keep.pt/vocab/dependencies#> .
	@prefix biocat: <http://biocatalogue.org/attribute/> .
	@prefix : <#> .

	<datalink?from=processor/hello/out/value&to=out/greeting> a wfdesc:DataLink ;
		wfdesc:hasSource <processor/hello/out/value> ;
		wfdesc:hasSink <out/greeting> .

	<> a wfdesc:Workflow , wfdesc:Description , wfdesc:Process ;
		dc:creator "Stian Soiland-Reyes" ;
		dcterms:description "One of the simplest workflows possible. No workflow input ports, a single workflow output port \"greeting\",  outputting \"Hello, world!\" as produced by the String Constant \"hello\"." ;
		dcterms:title "Hello World" ;
		rdfs:label "Hello_World" ;
		wfdesc:hasOutput <out/greeting> ;
		wfdesc:hasSubProcess <processor/hello/> ;
		wfdesc:hasDataLink <datalink?from=processor/hello/out/value&to=out/greeting> .

	<out/greeting> a wfdesc:Output , wfdesc:Description , wfdesc:Input ;
		rdfs:label "greeting" .

	<processor/hello/> a wfdesc:Process , wfdesc:Description ;
		rdfs:label "hello" ;
		wfdesc:hasOutput <processor/hello/out/value> .

	<processor/hello/out/value> a wfdesc:Output , wfdesc:Description ;
		rdfs:label "value" .



Extracted annotations
---------------------
Annotations in the workflow are also extracted for the workflow, processors and input ports (see namespaces above):

 * dc:creator
 * dcterms:description
 * dcterms:title
 * biocat:exampleData

Richer semantic annotations (e.g. on
 [Taverna Components](http://dev.mygrid.org.uk/wiki/display/taverna/Components)) are 
extracted verbatim, e.g.:

	<> a wfdesc:Workflow , wfdesc:Description , wfdesc:Process ;
		comp:fits comp:MigrationAction ;
		comp:migrates _:node18musbm56x1 .

	_:node18musbm56x1 a comp:MigrationPath ;
		comp:fromMimetype "image/tiff" ;
		comp:toMimetype "image/tiff" .

See [`valid_component_imagemagickconvert.wfdesc.ttl`](src/test/resources/valid_component_imagemagickconvert.wfdesc.ttl) 
for the complete example.



Programmatic use
----------------

Add a Maven dependency to "scufl2-wfdesc":

    <dependency>
        <groupId>org.purl.wf4ever</groupId>
        <artifactId>scufl2-wfdesc</artifactId>
        <version>0.3.7</version>
    </dependency>

See the pom.xml file of the latest git tag for the latest version. 

Stable builds are available from
http://www.mygrid.org.uk/maven/repository/
-- to retrieve artifacts from the myGrid repository, also add
to your POM:

    <repositories>
        <repository>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots />
            <id>mygrid-repository</id>
            <name>myGrid Repository</name>
            <url>http://www.mygrid.org.uk/maven/repository</url>
        </repository>
    </repositories>




Example:

    import uk.org.taverna.scufl2.api.container.WorkflowBundle;
    import uk.org.taverna.scufl2.api.io.ReaderException;
    import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
    import uk.org.taverna.scufl2.api.io.WriterException;

    ..
    File original = new File("helloworld.t2flow");
    File output = new File("helloworld.wfdesc.ttl");
    String original = null; // to guess filetype
    WorkflowBundle wfBundle = io.readBundle(original, null);
    io.writeBundle(wfBundle, output, "text/vnd.wf4ever.wfdesc+turtle");





References
----------
 * [SCUFL2 API](http://dev.mygrid.org.uk/wiki/display/developer/SCUFL2+API)
 * [RO ontology wfdesc](https://w3id.org/ro#wfdesc)
 * [RDF Turtle](http://www.w3.org/TR/turtle/)

