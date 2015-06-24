package org.apache.taverna.scufl2.wfdesc;

/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/



import java.io.OutputStream;

import org.apache.taverna.scufl2.api.annotation.Revision;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.WriterException;
import org.apache.taverna.scufl2.wfdesc.ontologies.Prov_o;
import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.helpers.OrganizedRDFWriter;
import org.openrdf.sail.memory.MemoryStore;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;


public class ROEvoSerializer {
	//private URITools uriTools = new URITools();
	
	public void workflowHistory(Workflow mainWorkflow, OutputStream output) throws WriterException {	
		OntModel model = ModelFactory.createOntologyModel();
		Revision revision = mainWorkflow.getCurrentRevision();
		Revision previous = revision.getPreviousRevision();
		addRevision(model, revision);
		while (previous != null) {
			addRevision(model, previous);
			addPrevious(model, revision, previous);			
			revision = previous;
			previous = revision.getPreviousRevision();
		}
		
		java.net.URI baseURI = Workflow.WORKFLOW_ROOT;
		
		try {
			con.setNamespace("roevo", "http://purl.org/wf4ever/roevo#");
			con.setNamespace("prov", "http://www.w3.org/ns/prov#");
//			con.setNamespace("wfdesc",
//					"http://purl.org/wf4ever/wfdesc#");
//			con.setNamespace("wf4ever",
//					"http://purl.org/wf4ever/wf4ever#");
			con.setNamespace("rdfs",
					"http://www.w3.org/2000/01/rdf-schema#");

			
			con.export(new OrganizedRDFWriter(
					new TurtleWriterWithBase(output, baseURI)));
		} catch (OpenRDFException e) {
			throw new WriterException("Can't write to output", e);
		}
		
	}

	private void addRevision(OntModel model,
			Revision revision) {
		OntClass VersionableResource = model.createClass("http://purl.org/wf4ever/roevo#VersionableResource");
		VersionableResource.addSuperClass(Prov_o.Entity);
		Individual revisionResource = model.createIndividual(revision.getIdentifier().toASCIIString(), 
				VersionableResource);
		revisionResource.addRDFType(Prov_o.Entity);
	}

	private void addPrevious(OntModel model,
			Revision revision, Revision previous) {
		OntClass VersionableResource = model.createClass("http://purl.org/wf4ever/roevo#VersionableResource");
		VersionableResource.addSuperClass(Prov_o.Entity);
		
		Individual revisionResource = model.createIndividual(revision.getIdentifier().toASCIIString(), 
				VersionableResource);
		Individual previousResource = model.createIndividual(previous.getIdentifier().toASCIIString(), 
				VersionableResource);
		revisionResource.addProperty(Prov_o.wasRevisionOf, previousResource);
	}
	
}
