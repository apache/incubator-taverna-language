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


public class ROEvoSerializer {
	//private URITools uriTools = new URITools();
	
	public void workflowHistory(Workflow mainWorkflow, OutputStream output) throws WriterException {	
		Repository repository = new SailRepository(new MemoryStore());
		try {
			repository.initialize();
		} catch (RepositoryException e1) {
			throw new IllegalStateException("Can't initialize memory SAIL repository", e1);
		}
		ValueFactory factory = repository.getValueFactory();
	
		
		RepositoryConnection con;
		try {
			con = repository.getConnection();
		} catch (RepositoryException e1) {
			throw new IllegalStateException("Can't get repository connection", e1);
		}
		Revision revision = mainWorkflow.getCurrentRevision();
		Revision previous = revision.getPreviousRevision();
		addRevision(factory, con, revision);
		while (previous != null) {
			addRevision(factory, con, previous);
			addPrevious(factory, con, revision, previous);			
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

	private void addRevision(ValueFactory factory, RepositoryConnection con,
			Revision revision) {
		URI revisionURI = factory.createURI(revision.getIdentifier().toASCIIString());			
		
		URI version = factory.createURI("http://purl.org/wf4ever/roevo#VersionableResource");		
		URI entity = factory.createURI("http://www.w3.org/ns/prov#Entity");		
		try {
			con.add(revisionURI, RDF.TYPE, version);
			con.add(revisionURI, RDF.TYPE, entity);
		} catch (RepositoryException e1) {
			throw new IllegalStateException("Can't add triple to repository", e1);
		}
		
	}

	private void addPrevious(ValueFactory factory, RepositoryConnection con,
			Revision revision, Revision previous) {
		URI revisionURI = factory.createURI(revision.getIdentifier().toASCIIString());			
		URI previousURI = factory.createURI(previous.getIdentifier().toASCIIString());
		
		URI prev = factory.createURI("http://www.w3.org/ns/prov#wasRevisionOf");
		
		try {
			con.add(revisionURI, prev, previousURI);
		} catch (RepositoryException e1) {
			throw new IllegalStateException("Can't add triple to repository", e1);
		}
	}
	
}
