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

import org.apache.taverna.ro.vocabs.prov;
import org.apache.taverna.scufl2.api.annotation.Revision;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.WriterException;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.ModelFactory;


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
		model.setNsPrefix("roevo", "http://purl.org/wf4ever/roevo#");
		model.setNsPrefix("prov", "http://www.w3.org/ns/prov#");
		model.setNsPrefix("rdfs",
				"http://www.w3.org/2000/01/rdf-schema#");
			
		model.write(output, "Turtle", baseURI.toASCIIString());

//			throw new WriterException("Can't write to output", e);
		
		
	}

	private void addRevision(OntModel model,
			Revision revision) {
		OntClass VersionableResource = model.createClass("http://purl.org/wf4ever/roevo#VersionableResource");
		VersionableResource.addSuperClass(prov.Entity);
		Individual revisionResource = model.createIndividual(revision.getIdentifier().toASCIIString(), 
				VersionableResource);
		revisionResource.addRDFType(prov.Entity);
	}

	private void addPrevious(OntModel model,
			Revision revision, Revision previous) {
		OntClass VersionableResource = model.createClass("http://purl.org/wf4ever/roevo#VersionableResource");
		VersionableResource.addSuperClass(prov.Entity);
		
		Individual revisionResource = model.createIndividual(revision.getIdentifier().toASCIIString(), 
				VersionableResource);
		Individual previousResource = model.createIndividual(previous.getIdentifier().toASCIIString(), 
				VersionableResource);
		revisionResource.addProperty(prov.wasRevisionOf, previousResource);
	}
	
}
