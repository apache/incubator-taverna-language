Convert SCUFL2 to wfdesc
========================

This is an extension to [Scufl2 API](../taverna-scufl2-api) which
provides the export capability to wfdesc ontology from the
[Wf4Ever](http://www.wf4ever-project.org/) [2] RO
[wfdesc ontology](https://w3id.org/ro#wfdesc).

You can use the [tavlang convert](../taverna-tavlang-tool/) command line tool for converting Taverna
workflows to wfdesc.


## Example


The output is a [RDF Turtle](http://www.w3.org/TR/turtle/) document containing statements about the workflow structure
according to the [RO ontology wfdesc](https://w3id.org/ro#wfdesc) ontology, with additional service
descriptions using the [wf4ever ontology](https://w3id.org/ro#wf4ever) where supported.

    : stain@ralph ~/src/wf4ever/scufl2-wfdesc; cat src/test/resources/helloworld.wfdesc.ttl

```turtle
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
```



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

```turtle
	<> a wfdesc:Workflow , wfdesc:Description , wfdesc:Process ;
		comp:fits comp:MigrationAction ;
		comp:migrates _:node18musbm56x1 .

	_:node18musbm56x1 a comp:MigrationPath ;
		comp:fromMimetype "image/tiff" ;
		comp:toMimetype "image/tiff" .
```

See [`valid_component_imagemagickconvert.wfdesc.ttl`](src/test/resources/valid_component_imagemagickconvert.wfdesc.ttl)
for the complete example.



Example:

```java
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WriterException;

..
File original = new File("helloworld.t2flow");
File output = new File("helloworld.wfdesc.ttl");
String original = null; // to guess filetype
WorkflowBundle wfBundle = io.readBundle(original, null);
io.writeBundle(wfBundle, output, "text/vnd.wf4ever.wfdesc+turtle");
```

# Common queries

These queries can be executed using [SPARQL](http://www.w3.org/TR/sparql11-query/) on the returned wfdesc Turtle, and assume these PREFIXes:

    PREFIX prov: <http://www.w3.org/ns/prov#>
    PREFIX wfdesc: <http://purl.org/wf4ever/wfdesc#>
    PREFIX wfprov: <http://purl.org/wf4ever/wfprov#>
    PREFIX tavernaprov: <http://ns.taverna.org.uk/2012/tavernaprov/>
    PREFIX cnt:  <http://www.w3.org/2011/content#>
    PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
    PREFIX wf4ever: <http://purl.org/wf4ever/wf4ever#>
    PREFIX comp: <http://purl.org/DP/components#>

## How do I exclude nested workflows from a query?

The wfdesc model does not have a concept of "top-level" workflow, however you can
use a SPARQL `NOT EXISTS` filter to skip any nested workflows:

    SELECT ?wf WHERE {
      ?wf a wfdesc:Workflow .

      FILTER NOT EXISTS {
          ?processInParent prov:specializationOf ?wf .
          ?parent wfdesc:hasSubProcess ?processInParent
      } .
    }

Thus any workflow `?wf` which (specialization) has been used as a sub-process in `?parent` are excluded from the result. As SCUFL2 workflows have unique URIs within each workflow bundle, e.g. http://ns.taverna.org.uk/2010/workflowBundle/01348671-5aaa-4cc2-84cc-477329b70b0d/workflow/Hello_Anyone/ - the above would *not* be excluding a workflow just because its structure has been reused elsewhere.

## How do I select processors in the top-level workflows?

A variant of the above is if you are only interested in processors in top-level workflows:

    SELECT ?proc WHERE {
      ?wf a wfdesc:Workflow ;
          wfdesc:hasSubProcess ?proc .

      FILTER NOT EXISTS {
          ?processInParent prov:specializationOf ?wf .
          ?parent wfdesc:hasSubProcess ?processInParent
      } .
    }
