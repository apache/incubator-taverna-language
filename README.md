Convert SCUFL2 to wfdesc
========================

This is an extension to Scufl2 [1] which provides the export capability
to wfdesc ontology from the Wf4Ever [2] RO wfdesc ontology v0.1 [3].

This is exposed as a command line tool "scufl2-to-wfdesc", but can also
be accessed programmatically using the SCUFL2 APIs.






Building
--------

Requirements:
 * Java JDK 6 or higher [6], or OpenJDK 7 or higher [7]
 * Maven 2 or higher [9]  (only for building)

To build, simply use "mvn clean install"

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

After building, the folder "target/scufl2-wfdesc" contains a
distribution of the command line tool "scufl2-to-wfdesc", with launchers
generated for Unix and Windows.

You may copy and distribute this folder for a standalone usage of the
conversion tool. 

Alternatively, you can use the
target/scufl2-wfdesc-0.3.0-SNAPSHOT-standalone.jar directly with 
"java -jar" - this JAR embeds all required libraries.

The command line tool takes a list of Taverna workflow filenames (supported file
types are *.t2flow and *.wfbundle). If no filenames are given, the tool
will read a workflow definition from STDIN and write wfdesc to STDOUT.
The argument -h  and --help should give a minimal help.



Example:

    : stain@ralph ~/src/wf4ever/scufl2-wfdesc; target/scufl2-wfdesc/bin/scufl2-to-wfdesc src/test/resources/helloworld.t2flow 
    Converted src/test/resources/helloworld.t2flow to src/test/resources/helloworld.wfdesc.ttl


    : stain@ralph ~/src/wf4ever/scufl2-wfdesc; cat src/test/resources/helloworld.wfdesc.ttl 

    @base <http://ns.taverna.org.uk/2010/workflowBundle/01348671-5aaa-4cc2-84cc-477329b70b0d/workflow/Hello_Anyone/> .
    @prefix wfdesc: <http://purl.org/wf4ever/wfdesc#> .
    @prefix wf4ever: <http://purl.org/wf4ever/wf4ever#> .
    @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .

    <processor/Concatenate_two_strings/> a wfdesc:Process , wfdesc:Description , <http://www.w3.org/2002/07/owl#Thing> , wf4ever:BeanshellScript ;
        wf4ever:script "output = string1 + string2;" ;
        rdfs:label "Concatenate_two_strings" ;
        wfdesc:hasInput <processor/Concatenate_two_strings/in/string1> , <processor/Concatenate_two_strings/in/string2> ;
        wfdesc:hasOutput <processor/Concatenate_two_strings/out/output> .

    <datalink?from=in/name&to=processor/Concatenate_two_strings/in/string2> a wfdesc:DataLink ;
        wfdesc:hasSource <in/name> ;
        wfdesc:hasSink <processor/Concatenate_two_strings/in/string2> .

    <datalink?from=processor/Concatenate_two_strings/out/output&to=out/greeting> a wfdesc:DataLink ;
        wfdesc:hasSource <processor/Concatenate_two_strings/out/output> ;
        wfdesc:hasSink <out/greeting> .

    <datalink?from=processor/hello/out/value&to=processor/Concatenate_two_strings/in/string1> a wfdesc:DataLink ;
        wfdesc:hasSource <processor/hello/out/value> ;
        wfdesc:hasSink <processor/Concatenate_two_strings/in/string1> .

    <> a wfdesc:Workflow , wfdesc:Description , wfdesc:Process ;
        rdfs:label "Hello_Anyone" ;
        wfdesc:hasInput <in/name> ;
        wfdesc:hasOutput <out/greeting> ;
        wfdesc:hasSubProcess <processor/Concatenate_two_strings/> , <processor/hello/> ;
        wfdesc:hasDataLink <datalink?from=in/name&to=processor/Concatenate_two_strings/in/string2> , <datalink?from=processor/Concatenate_two_strings/out/output&to=out/greeting> , <datalink?from=processor/hello/out/value&to=processor/Concatenate_two_strings/in/string1> .

    <in/name> a wfdesc:Input , wfdesc:Description , wfdesc:Output ;
        rdfs:label "name" .

    <out/greeting> a wfdesc:Output , wfdesc:Description , wfdesc:Input ;
        rdfs:label "greeting" .

    <processor/Concatenate_two_strings/in/string1> a wfdesc:Input , wfdesc:Description ;
        rdfs:label "string1" .

    <processor/Concatenate_two_strings/in/string2> a wfdesc:Input , wfdesc:Description ;
        rdfs:label "string2" .

    <processor/Concatenate_two_strings/out/output> a wfdesc:Output , wfdesc:Description ;
        rdfs:label "output" .

    <processor/hello/> a wfdesc:Process , wfdesc:Description ;
        rdfs:label "hello" ;
        wfdesc:hasOutput <processor/hello/out/value> .

    <processor/hello/out/value> a wfdesc:Output , wfdesc:Description ;
        rdfs:label "value" .



Programmatic use
----------------

Add a Maven dependency to "scufl2-wfdesc":

    <dependency>
        <groupId>org.purl.wf4ever</groupId>
        <artifactId>scufl2-wfdesc</artifactId>
        <version>0.3.0-SNAPSHOT</version>
    </dependency>

See the pom.xml file here for the latest version. 

Snapshot builds are available from
http://www.mygrid.org.uk/maven/snapshot-repository/
-- to retrieve artifacts from the snapshot repository, also add
to your POM:

    <repositories>
        <repository>
            <releases>
                <enabled>false</enabled>
            </releases>
            <snapshots />
            <id>mygrid-snapshot-repository</id>
            <name>myGrid Snapshot Repository</name>
            <url>http://www.mygrid.org.uk/maven/snapshot-repository</url>
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
 * SCUFL2 [4]
 * RO ontologies wfdesc [5]



[1]: https://github.com/mygrid/scufl2/
[2]: http://www.wf4ever-project.org/
[3]: https://github.com/wf4ever/ro/tree/0.1
[4]: http://www.mygrid.org.uk/dev/wiki/display/developer/2010-07+SCUFL2
[5]: http://www.wf4ever-project.org/wiki/display/docs/Research+Object+Vocabulary+Specification+v0.1
[6]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[7]: http://openjdk.java.net/install/
[8]: http://maven.apache.org/download.html
