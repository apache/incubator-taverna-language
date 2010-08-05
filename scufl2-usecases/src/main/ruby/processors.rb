
require 'rubygems'
require 'rdf'
require 'rdf/raptor'

scufl2 = RDF::Vocabulary.new("http://ns.taverna.org.uk/2010/scufl2/ontology/")
dc = RDF::Vocabulary.new("http://purl.org/dc/elements/1.1/")

graph = RDF::Graph.load("../resources/workflows/example.ttl")

graph.query([nil, scufl2.workflow, nil]) do |bundle,p,workflow|
  graph.query([workflow, scufl2.name, nil]) do |wf,p,workflow_name|
    # Should just be one
    print workflow_name  
  end
  graph.query([workflow, dc.description, nil]) do |workflow,p,description|
    # Optional description(s)
    print "  "
    print description
  end
  print "\n"

  graph.query([workflow, scufl2.processor, nil]) do |workflow,p,processor|
    graph.query([processor, scufl2.name, nil]) do |processor,p,processor_name|
        print processor_name
    end
    graph.query([processor, dc.description, nil]) do |processor,p,description|
        print "  "
        print description
    end
    print "\n"
    
  end
end
