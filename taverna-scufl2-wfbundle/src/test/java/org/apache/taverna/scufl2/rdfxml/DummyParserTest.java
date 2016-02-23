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


import java.net.URI;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

import org.apache.taverna.scufl2.xml.ObjectFactory;
import org.apache.taverna.scufl2.xml.Profile;
import org.apache.taverna.scufl2.xml.ProfileDocument;
import org.apache.taverna.scufl2.xml.Workflow;
import org.apache.taverna.scufl2.xml.WorkflowBundle;
import org.apache.taverna.scufl2.xml.WorkflowBundleDocument;
import org.apache.taverna.scufl2.xml.WorkflowDocument;

public class DummyParserTest {

	private JAXBContext jaxbContext;
	private Unmarshaller unmarshaller;

	@SuppressWarnings("unchecked")
	@Test
	public void parse() throws Exception {
		URL resource = getClass().getResource("example/workflowBundle.rdf");
		URI baseUri = resource.toURI();

		@SuppressWarnings("rawtypes")
		JAXBElement<WorkflowBundleDocument> workflowBundle = (JAXBElement<WorkflowBundleDocument>) unmarshaller
				.unmarshal(resource);
		WorkflowBundleDocument bundleDoc = workflowBundle.getValue();
		WorkflowBundle wfBundle = (WorkflowBundle) bundleDoc.getAny().get(0);

		//System.out.println(wfBundle.getName());
		//System.out.println(wfBundle.getMainWorkflow());
		//System.out.println(wfBundle.getSameBaseAs().getResource());
		for (WorkflowBundle.Workflow wfLink : wfBundle.getWorkflow()) {
			String about = wfLink.getWorkflow().getAbout();
			String seeAlso = wfLink.getWorkflow().getSeeAlso().getResource();

			URI wfResource = baseUri.resolve(seeAlso);
			JAXBElement<WorkflowDocument> unmarshalled = (JAXBElement<WorkflowDocument>) unmarshaller
					.unmarshal(wfResource.toURL());
			WorkflowDocument wfDoc = unmarshalled.getValue();
			Workflow wf = (Workflow) wfDoc.getAny().get(0);
			//System.out.println(about + " " + wf.getName());
		}

		for (WorkflowBundle.Profile profileLink : wfBundle.getProfile()) {
			String about = profileLink.getProfile().getAbout();
			String seeAlso = profileLink.getProfile().getSeeAlso()
					.getResource();

			URI profileResource = baseUri.resolve(seeAlso);
			JAXBElement unmarshalled = (JAXBElement) unmarshaller
					.unmarshal(profileResource.toURL());
			ProfileDocument profileDoc = (ProfileDocument) unmarshalled
					.getValue();
			Profile profile = (Profile) profileDoc.getAny().get(0);
			//System.out.println(about + " " + profile.getName());
		}
	}

	@Before
	public void makeUnmarshaller() throws JAXBException {
		
		Class<?>[] packages = { ObjectFactory.class,
				org.apache.taverna.scufl2.xml.rdf.ObjectFactory.class,
				org.apache.taverna.scufl2.xml.rdfs.ObjectFactory.class };
		jaxbContext = JAXBContext.newInstance(packages);	
		unmarshaller = jaxbContext.createUnmarshaller();
	}

}
