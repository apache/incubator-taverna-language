
require 'rubygems'
require 'rdf'
require 'rdf/raptor'

scufl2 = RDF::Vocabulary.new("http://ns.taverna.org.uk/2010/scufl2/ontology/")
dc = RDF::Vocabulary.new("http://purl.org/dc/elements/1.1/")

graph = RDF::Graph.load("../resources/workflows/example.ttl")

graph.query([nil, scufl2.workflow, nil]) do |bundle,p,workflow|
  workflow_name = ""
  graph.query([workflow, scufl2.name, nil]) do |wf,p,workflow_name|
  end

  description = ""
  graph.query([workflow, dc.description, nil]) do |workflow,p,description|
  end

  puts workflow_name, description

  graph.query([workflow, scufl2.processor, nil]) do |workflow,p,processor|
    processor_name = ""

    graph.query([processor, scufl2.name, nil]) do |processor,p,processor_name|
    end

    graph.query([processor, dc.description, nil]) do |processor,p,description|
    end

    puts processor_name, description
  end
end
