package org.apache.taverna.scufl2.rdfxml;
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


import static org.apache.taverna.scufl2.rdfxml.RDFXMLReader.APPLICATION_RDF_XML;
import static org.apache.taverna.scufl2.rdfxml.RDFXMLReader.APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.taverna.scufl2.api.annotation.Revision;
import org.apache.taverna.scufl2.api.annotation.Revisioned;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.WorkflowBundleWriter;
import org.apache.taverna.scufl2.api.io.WriterException;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.apache.taverna.scufl2.ucfpackage.UCFPackage;


public class RDFXMLWriter implements WorkflowBundleWriter {
	private static final String WF = "wf-";
	private static final String REVISIONS = "-revisions";
	protected static final String RDF = ".rdf";
	protected static final String WORKFLOW = "workflow/";
	protected static final String HISTORY = "history/";
	protected static final String PROFILE = "profile/";
	protected static final String WORKFLOW_BUNDLE_RDF = "workflowBundle.rdf";

	private static URITools uriTools = new URITools();
	
	public static final URITools getUriTools() {
		return uriTools;
	}

	public static final void setUriTools(URITools uriTools) {
		RDFXMLWriter.uriTools = uriTools;
	}

	/**
	 * Version of Workflow Bundle format
	 */
    public final String WORKFLOW_BUNDLE_VERSION = "0.4.0";

	@Override
	public Set<String> getMediaTypes() {
		return Collections
				.singleton(APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);
	}

	@Override
	public void writeBundle(WorkflowBundle wfBundle, File destination,
			String mediaType) throws WriterException, IOException {
		UCFPackage ucfPackage = makeUCFPackage(wfBundle);
		ucfPackage.save(destination);
	}

	protected UCFPackage makeUCFPackage(WorkflowBundle wfBundle)
			throws IOException, WriterException {
		//UCFPackage ucfPackage = new UCFPackage();
		UCFPackage ucfPackage = wfBundle.getResources();		
		if (ucfPackage.getPackageMediaType() == null)
			ucfPackage
				.setPackageMediaType(APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);

		RDFXMLSerializer serializer = new RDFXMLSerializer(wfBundle);
		
		for (Workflow wf : wfBundle.getWorkflows()) {
			String path = WORKFLOW + uriTools.validFilename(wf.getName()) + RDF;

			try (OutputStream outputStream = ucfPackage
					.addResourceUsingOutputStream(path, APPLICATION_RDF_XML)) {
				serializer.workflowDoc(outputStream, wf, URI.create(path));
			} catch (JAXBException e) {
				throw new WriterException("Can't generate " + path, e);
			}
			
			path = HISTORY + WF +  
					uriTools.validFilename(wf.getName()) + REVISIONS + RDF;
			addRevisions(wf, path, wfBundle);
		}

		for (Profile pf : wfBundle.getProfiles()) {
			String path = PROFILE + uriTools.validFilename(pf.getName()) + RDF;
			try (OutputStream outputStream = ucfPackage
					.addResourceUsingOutputStream(path, APPLICATION_RDF_XML)) {
				serializer.profileDoc(outputStream, pf, URI.create(path));
			} catch (JAXBException e) {
				throw new WriterException("Can't generate " + path, e);
			}
			path = HISTORY + "pf-" +  
					uriTools.validFilename(pf.getName()) + REVISIONS + RDF;
			addRevisions(pf, path, wfBundle);
		}

		try (OutputStream outputStream = ucfPackage
				.addResourceUsingOutputStream(WORKFLOW_BUNDLE_RDF,
						APPLICATION_RDF_XML)) {
			serializer.workflowBundleDoc(outputStream,
					URI.create(WORKFLOW_BUNDLE_RDF));
		} catch (JAXBException e) {
			throw new WriterException("Can't generate " + WORKFLOW_BUNDLE_RDF,
					e);
		}
		
		if (ucfPackage.getPackageMediaType().equals(
				APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE))
			ucfPackage
					.setRootFile(WORKFLOW_BUNDLE_RDF, WORKFLOW_BUNDLE_VERSION);

		String path = HISTORY + "wfbundle" + REVISIONS + RDF;
		addRevisions(wfBundle, path, wfBundle);
		
		return ucfPackage;
	}


	protected void addRevisions(Revisioned revisioned, String path, WorkflowBundle wfBundle) throws WriterException {
		@SuppressWarnings("unused")
		URI uriBase = uriTools.uriForBean(wfBundle).resolve(path);		
		Revision currentRevision = revisioned.getCurrentRevision();
		if (currentRevision == null)
			return;
//		try {
//			wfBundle.getResources()
//					.addResource(visitor.getDoc(), path, APPLICATION_RDF_XML);
//		} catch (IOException e) {
//			throw new WriterException("Can't write revisions to " + path, e);
//		}
	}

	@Override
	public void writeBundle(WorkflowBundle wfBundle, OutputStream output,
			String mediaType) throws WriterException, IOException {
		UCFPackage ucfPackage = makeUCFPackage(wfBundle);
		ucfPackage.save(output);
	}
}
