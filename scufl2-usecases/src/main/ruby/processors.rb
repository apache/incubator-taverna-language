
require 'rubygems'
require 'rdf'
require 'rdf/raptor'
include RDF

scufl2 = RDF::Vocabulary.new("http://ns.taverna.org.uk/2010/scufl2/ontology/")

graph = RDF::Graph.load("../resources/workflows/example.ttl")

graph.query([nil, scufl2.workflow, nil]) do |statement|
  workflow = statement.object
  graph.query([workflow, scufl2.name, nil]) do |statement|
    workflow_name = statement.object
    puts workflow_name
  end
  graph.query([workflow, scufl2.processor, nil]) do |statement|
    processor = statement.object
    graph.query([statement.object, scufl2.name, nil]) do |statement|
        processor_name = statement.object
        puts processor_name
    end
  end


  
end
