
require 'rubygems'
require 'rdf'
require 'rdf/raptor'
require 'zip/zipfilesystem'

scufl2 = RDF::Vocabulary.new("http://ns.taverna.org.uk/2010/scufl2#")
dc = RDF::Vocabulary.new("http://purl.org/dc/elements/1.1/")

#graph = RDF::Graph.load("../resources/workflows/example.ttl")

graph = RDF::Graph.new()
Zip::ZipFile.open("../resources/workflows/example.scufl2") {
    |zipfile|
    a = "@base <http://example.org/> .\n" + zipfile.file.read("workflowBundle.ttl")
    RDF::Reader.for(:turtle).new(a) do |reader|
      reader.each_statement do |statement|
        graph << statement
      end
    end

    base = "http://example.org/" 
    graph.query([nil,scufl2.sameBaseAs,nil]) do |s,p,base|
    end 

    a = "@base <" + base + "workflowBundle.ttl> .\n" + zipfile.file.read("workflowBundle.ttl")
    RDF::Reader.for(:turtle).new(a) do |reader|
      reader.each_statement do |statement|
        graph << statement
      end
    end
    # TODO: FOR-loop like in Python

    a = "@base <" + base + "workflow/HelloWorld.ttl> .\n" + zipfile.file.read("workflow/HelloWorld.ttl")
    RDF::Reader.for(:turtle).new(a) do |reader|
      reader.each_statement do |statement|
        graph << statement
      end
    end
    a = "@base <" + base + "annotation/workflow/HelloWorld.ttl> .\n" + zipfile.file.read("annotation/workflow/HelloWorld.ttl")
    RDF::Reader.for(:turtle).new(a) do |reader|
      reader.each_statement do |statement|
        graph << statement
      end
    end
}

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


