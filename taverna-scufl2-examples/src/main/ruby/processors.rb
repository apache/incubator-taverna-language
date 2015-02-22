#
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
#

require 'rubygems'
require 'rdf'
#require 'rdf/raptor'
require 'zip/zipfilesystem'

scufl2 = RDF::Vocabulary.new("http://ns.taverna.org.uk/2010/scufl2#")
dc = RDF::Vocabulary.new("http://purl.org/dc/elements/1.1/")

graph = RDF::Graph.load("../../../../taverna-scufl2-wfbundle/src/test/resources/org/apache/taverna/scufl2/rdfxml/example/workflowBundle.rdf")

graph = RDF::Graph.new()
Zip::ZipFile.open("../../../../taverna-scufl2-wfbundle/src/test/resources/org/apache/taverna/scufl2/rdfxml/example.wfbundle") {
    |zipfile|
    a = zipfile.file.read("workflowBundle.rdf")
    RDF::Reader.for(:rdfxml).new(a) do |reader|
      reader.each_statement do |statement|
        graph << statement
      end
    end

    base = "http://example.org/" 
    graph.query([nil,scufl2.sameBaseAs,nil]) do |s,p,base|
    end 

    a = zipfile.file.read("workflowBundle.rdf")
    RDF::Reader.for(:rdfxml).new(a) do |reader|
      reader.each_statement do |statement|
        graph << statement
      end
    end
    # TODO: FOR-loop like in Python

    a = zipfile.file.read("workflow/HelloWorld.rdf")
    RDF::Reader.for(:rdfxml).new(a) do |reader|
      reader.each_statement do |statement|
        graph << statement
      end
    end
    a = zipfile.file.read("annotation/workflow/HelloWorld.rdf")
    RDF::Reader.for(:rdfxml).new(a) do |reader|
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


