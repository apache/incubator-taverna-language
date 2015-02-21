package org.apache.taverna.scufl2.translator.t2flow.t23activities;
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


import static org.apache.taverna.scufl2.translator.t2flow.t23activities.RESTActivityParser.ACTIVITY_URI;
import static org.apache.taverna.scufl2.translator.t2flow.t23activities.RESTActivityParser.HTTP_METHODS_URI;
import static org.apache.taverna.scufl2.translator.t2flow.t23activities.RESTActivityParser.HTTP_URI;
import static org.junit.Assert.*;

import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;
import org.apache.taverna.scufl2.translator.t2flow.T2Parser;
import org.apache.taverna.scufl2.translator.t2flow.t23activities.RESTActivityParser;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;


@SuppressWarnings("unused")
public class TestRESTActivityParser {

	private static Scufl2Tools scufl2Tools = new Scufl2Tools();
	private static URITools uriTools = new URITools();
	private static final String WF_2_2 = "/rest-2-2.t2flow";
	private static final String WF_2_2_SAVED_2_3 = "/rest-2-2-saved-2-3.t2flow";

	private static final String WF_2_3 = "/rest-2-3.t2flow";


	private void checkT2Parsers(T2FlowParser parser) {
		for (T2Parser t2Parser : parser.getT2Parsers()) {
			if (t2Parser instanceof RESTActivityParser) {
				return;
			}
		}
		fail("Could not find REST activity parser, found " + parser.getT2Parsers());
	}
/* TODO: Update test to use JSON config
	@Test
	public void default_2_2_saved() throws Exception {
		WorkflowBundle bundle_2_2_saved = parse2_2_saved_2_3();
		Profile profile = bundle_2_2_saved.getMainProfile();
		//System.out.println(bundle.getMainWorkflow().getProcessors().getNames());
		// [default, post, put]
		Processor proc = bundle_2_2_saved.getMainWorkflow().getProcessors()
				.getByName("default");
		assertNotNull(proc);
		Configuration config = scufl2Tools
				.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
		assertEquals(ACTIVITY_URI.resolve("#Config"),
				config.getType());

		Activity activity = (Activity) config.getConfigures();
		assertEquals(ACTIVITY_URI, activity.getType());

		ObjectNode configResource = config.getJsonAsObjectNode();
		ObjectNode request = configResource.get("request");

		URI toolId = request.getPropertyAsResourceURI(
				HTTP_URI.resolve("#mthd"));
		assertEquals(HTTP_METHODS_URI.resolve("#GET"),
				toolId);

		String urlSignature = request.getPropertyAsString(
				ACTIVITY_URI.resolve("#absoluteURITemplate"));
		assertEquals("http://www.myexperiment.org/user.xml?id={userID}", urlSignature);

		Map<String, String> foundHeaders = new HashMap<String, String>();
		PropertyList headers = request.getPropertyAsList(HTTP_URI.resolve("#headers"));
		for (PropertyObject header : headers) {
			PropertyResource reqHeader = (PropertyResource) header;
			String fieldName = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldName"));
			String value;
			if (reqHeader.hasProperty(HTTP_URI.resolve("#fieldValue"))) {
				value = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldValue"));
			} else if (reqHeader.hasProperty(ACTIVITY_URI.resolve("#use100Continue"))) {
				assertEquals(true,
						reqHeader.getPropertyAsLiteral(ACTIVITY_URI.resolve("#use100Continue")).getLiteralValueAsBoolean());
				value = "--use100Continue--";
			} else {
				value = "--undefinedValue--";
			}
			foundHeaders.put(fieldName, value);
			assertEquals(HTTP_URI.resolve("#RequestHeader"), reqHeader.getTypeURI());
		}
		assertEquals(1, foundHeaders.size());
		assertEquals("text/plain", foundHeaders.get("Accept"));
		// Content-Type and Expect should *not* be included if the method is GET/HEAD/DELETE
		assertFalse(foundHeaders.containsKey("Content-Type"));
		assertFalse(foundHeaders.containsKey("Expect"));
		//assertEquals("application/zip", foundHeaders.get("Content-Type"));
		// assertEquals("--use100Continue--", foundHeaders.get("Expect"))


		assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#showRedirectionOutputPort")));
		//assertTrue(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#showRedirectionOutputPort")).getLiteralValueAsBoolean());
		//assertFalse(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#escapeParameters")).getLiteralValueAsBoolean());
		assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#escapeParameters")));


		// Check ports
//		assertEquals(1, activity.getInputPorts().size());
//		InputActivityPort userID = activity.getInputPorts().getByName("userID");
//		assertEquals((Integer)0, userID.getDepth());
//
//		assertEquals(2, activity.getOutputPorts().size());
//		OutputActivityPort responseBody = activity.getOutputPorts().getByName("responseBody");
//		assertEquals((Integer)0, responseBody.getDepth());
//
//		OutputActivityPort status = activity.getOutputPorts().getByName("status");
//		assertEquals((Integer)0, status.getDepth());
//
//		PropertyResource userIDDef = scufl2Tools.portDefinitionFor(userID, profile);
//		assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), userIDDef.getTypeURI());
//		assertEquals(PropertyLiteral.XSD_STRING,
//				userIDDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));

	}

	@Test
	public void default2_2() throws Exception {
		WorkflowBundle bundle_2_2 = parse2_2();
		Profile profile = bundle_2_2.getMainProfile();
		//System.out.println(bundle.getMainWorkflow().getProcessors().getNames());
		// [default, post, put]
		Processor proc = bundle_2_2.getMainWorkflow().getProcessors()
				.getByName("default");
		assertNotNull(proc);
		Configuration config = scufl2Tools
				.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
		assertEquals(ACTIVITY_URI.resolve("#Config"),
				config.getType());

		Activity activity = (Activity) config.getConfigures();
		assertEquals(ACTIVITY_URI, activity.getType());

		PropertyResource configResource = config.getJson();
		PropertyResource request = configResource.getPropertyAsResource(
				ACTIVITY_URI.resolve("#request"));
		assertEquals(ACTIVITY_URI.resolve("#Request"), request.getTypeURI());
		// A sub-class of HTTP_URI.resolve("#Request")

		URI toolId = request.getPropertyAsResourceURI(
				HTTP_URI.resolve("#mthd"));
		assertEquals(HTTP_METHODS_URI.resolve("#GET"),
				toolId);

		String urlSignature = request.getPropertyAsString(
				ACTIVITY_URI.resolve("#absoluteURITemplate"));
		assertEquals("http://www.myexperiment.org/user.xml?id={userID}", urlSignature);

		Map<String, String> foundHeaders = new HashMap<String, String>();
		PropertyList headers = request.getPropertyAsList(HTTP_URI.resolve("#headers"));
		for (PropertyObject header : headers) {
			PropertyResource reqHeader = (PropertyResource) header;
			String fieldName = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldName"));
			String value;
			if (reqHeader.hasProperty(HTTP_URI.resolve("#fieldValue"))) {
				value = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldValue"));
			} else if (reqHeader.hasProperty(ACTIVITY_URI.resolve("#use100Continue"))) {
				assertEquals(true,
						reqHeader.getPropertyAsLiteral(ACTIVITY_URI.resolve("#use100Continue")).getLiteralValueAsBoolean());
				value = "--use100Continue--";
			} else {
				value = "--undefinedValue--";
			}
			foundHeaders.put(fieldName, value);
			assertEquals(HTTP_URI.resolve("#RequestHeader"), reqHeader.getTypeURI());
		}
		assertEquals(1, foundHeaders.size());
		assertEquals("text/plain", foundHeaders.get("Accept"));
		// Content-Type and Expect should *not* be included if the method is GET/HEAD/DELETE
		assertFalse(foundHeaders.containsKey("Content-Type"));
		assertFalse(foundHeaders.containsKey("Expect"));
		//assertEquals("application/zip", foundHeaders.get("Content-Type"));
		// assertEquals("--use100Continue--", foundHeaders.get("Expect"))


		assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#showRedirectionOutputPort")));
		//assertTrue(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#showRedirectionOutputPort")).getLiteralValueAsBoolean());
		//assertFalse(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#escapeParameters")).getLiteralValueAsBoolean());
		assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#escapeParameters")));


		// Check ports
//		assertEquals(1, activity.getInputPorts().size());
//		InputActivityPort userID = activity.getInputPorts().getByName("userID");
//		assertEquals((Integer)0, userID.getDepth());
//
//		assertEquals(2, activity.getOutputPorts().size());
//		OutputActivityPort responseBody = activity.getOutputPorts().getByName("responseBody");
//		assertEquals((Integer)0, responseBody.getDepth());
//
//		OutputActivityPort status = activity.getOutputPorts().getByName("status");
//		assertEquals((Integer)0, status.getDepth());
//
//		PropertyResource userIDDef = scufl2Tools.portDefinitionFor(userID, profile);
//		assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), userIDDef.getTypeURI());
//		assertEquals(PropertyLiteral.XSD_STRING,
//				userIDDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));

	}

	@Test
	public void default2_3() throws Exception {
		WorkflowBundle bundle_2_3 = parse2_3();
		Profile profile = bundle_2_3.getMainProfile();
		//System.out.println(bundle.getMainWorkflow().getProcessors().getNames());
		// [default, post, put]
		Processor proc = bundle_2_3.getMainWorkflow().getProcessors()
				.getByName("default");
		assertNotNull(proc);
		Configuration config = scufl2Tools
				.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
		assertEquals(ACTIVITY_URI.resolve("#Config"),
				config.getType());

		Activity activity = (Activity) config.getConfigures();
		assertEquals(ACTIVITY_URI, activity.getType());

		PropertyResource configResource = config.getJson();
		PropertyResource request = configResource.getPropertyAsResource(
				ACTIVITY_URI.resolve("#request"));
		assertEquals(ACTIVITY_URI.resolve("#Request"), request.getTypeURI());
		// A sub-class of HTTP_URI.resolve("#Request")

		URI toolId = request.getPropertyAsResourceURI(
				HTTP_URI.resolve("#mthd"));
		assertEquals(HTTP_METHODS_URI.resolve("#GET"),
				toolId);

		String urlSignature = request.getPropertyAsString(
				ACTIVITY_URI.resolve("#absoluteURITemplate"));
		assertEquals("http://www.uniprot.org/uniprot/{id}.xml", urlSignature);

		Map<String, String> foundHeaders = new HashMap<String, String>();
		PropertyList headers = request.getPropertyAsList(HTTP_URI.resolve("#headers"));
		for (PropertyObject header : headers) {
			PropertyResource reqHeader = (PropertyResource) header;
			String fieldName = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldName"));
			String value;
			if (reqHeader.hasProperty(HTTP_URI.resolve("#fieldValue"))) {
				value = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldValue"));
			} else if (reqHeader.hasProperty(ACTIVITY_URI.resolve("#use100Continue"))) {
				assertEquals(true,
						reqHeader.getPropertyAsLiteral(ACTIVITY_URI.resolve("#use100Continue")).getLiteralValueAsBoolean());
				value = "--use100Continue--";
			} else {
				value = "--undefinedValue--";
			}
			foundHeaders.put(fieldName, value);
			assertEquals(HTTP_URI.resolve("#RequestHeader"), reqHeader.getTypeURI());
		}
		assertEquals(1, foundHeaders.size());
		assertEquals("application/xml", foundHeaders.get("Accept"));
		// Content-Type and Expect should *not* be included if the method is GET/HEAD/DELETE
		assertFalse(foundHeaders.containsKey("Content-Type"));
		assertFalse(foundHeaders.containsKey("Expect"));
		//assertEquals("application/zip", foundHeaders.get("Content-Type"));
		// assertEquals("--use100Continue--", foundHeaders.get("Expect"))


		assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#showRedirectionOutputPort")));
		//assertTrue(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#showRedirectionOutputPort")).getLiteralValueAsBoolean());
		//assertFalse(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#escapeParameters")).getLiteralValueAsBoolean());
		assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#escapeParameters")));


		// Check ports
//		assertEquals(1, activity.getInputPorts().size());
//		InputActivityPort id = activity.getInputPorts().getByName("id");
//		assertEquals((Integer)0, id.getDepth());
//
//		assertEquals(2, activity.getOutputPorts().size());
//		OutputActivityPort responseBody = activity.getOutputPorts().getByName("responseBody");
//		assertEquals((Integer)0, responseBody.getDepth());
//
//		OutputActivityPort status = activity.getOutputPorts().getByName("status");
//		assertEquals((Integer)0, status.getDepth());
//
//		PropertyResource idDef = scufl2Tools.portDefinitionFor(id, profile);
//		assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), idDef.getTypeURI());
//		assertEquals(PropertyLiteral.XSD_STRING,
//				idDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));
//
	}

	public T2FlowParser makeParser() throws JAXBException {
		T2FlowParser parser = new T2FlowParser();
		parser.setValidating(true);
		parser.setStrict(true);
		checkT2Parsers(parser);
		return parser;
	}

	 Move to integration test with higher thread counts 
	@Test
	public void multiThreadParse() throws Exception {

		final boolean LOG = false;
		final int NUM_THREADS=6;

		final T2FlowParser parser = makeParser();
		final URL wf_2_2 = getClass().getResource(WF_2_2);
		final URL wf_2_2_saved = getClass().getResource(WF_2_2_SAVED_2_3);
		final URL wf_2_3 = getClass().getResource(WF_2_3);


		List<Thread> threads = new ArrayList<Thread>();
		for (int i=0; i<NUM_THREADS; i++) {
			threads.add(
			new Thread(
					new Runnable() {
				@Override
				public void run() {
					try {
						if (LOG)
							System.out.print(".");
						parser.parseT2Flow(wf_2_2.openStream());
						if (LOG)
							System.out.print("·");
						parser.parseT2Flow(wf_2_2_saved.openStream());
						if (LOG)
							System.out.print(":");
						parser.parseT2Flow(wf_2_3.openStream());
						if (LOG)
							System.out.print("'");
					} catch (Exception e) {
						throw new RuntimeException("", e);
					}
				}
			}));
		}
		Date started = new Date();
		final List<Throwable> errors = new ArrayList<Throwable>();
		for (Thread t : threads) {
			if (LOG) System.out.print("+");
			t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread t, Throwable e) {
					e.printStackTrace();
					errors.add(e);
				}
			});
			t.start();
		}
		if (LOG) System.out.print("\n");
		for (Thread t : threads) {
			t.join();
			if (LOG) System.out.print("-");
		}
		Date finished = new Date();
		if (LOG) System.out.print("\n" + (finished.getTime() - started.getTime()) + " ms");
		assertTrue(errors.size() + " errors occured", errors.isEmpty());
	}

	public WorkflowBundle parse2_2() throws Exception {
		T2FlowParser parser = makeParser();
		URL wfResource = getClass().getResource(WF_2_2);
		assertNotNull("Could not find workflow " + WF_2_2, wfResource);
		return parser
				.parseT2Flow(wfResource.openStream());
	}


	public WorkflowBundle parse2_2_saved_2_3() throws Exception {
		T2FlowParser parser = makeParser();
		URL wfResource = getClass().getResource(WF_2_2_SAVED_2_3);
		assertNotNull("Could not find workflow " + WF_2_2_SAVED_2_3, wfResource);
		return parser
				.parseT2Flow(wfResource.openStream());
	}

	public WorkflowBundle parse2_3() throws Exception {
		T2FlowParser parser = makeParser();
		URL wfResource = getClass().getResource(WF_2_3);
		assertNotNull("Could not find workflow " + WF_2_3, wfResource);
		return parser
				.parseT2Flow(wfResource.openStream());
	}





	@Test
	public void put2_2() throws Exception {
		WorkflowBundle bundle_2_2 = parse2_2();
		Profile profile = bundle_2_2.getMainProfile();
		//System.out.println(bundle.getMainWorkflow().getProcessors().getNames());
		// [default, post, put]
		Processor proc = bundle_2_2.getMainWorkflow().getProcessors()
				.getByName("put");
		assertNotNull(proc);
		Configuration config = scufl2Tools
				.configurationForActivityBoundToProcessor(proc, profile);
		assertNotNull(config);
		assertEquals(ACTIVITY_URI.resolve("#Config"),
				config.getType());

		Activity activity = (Activity) config.getConfigures();
		assertEquals(ACTIVITY_URI, activity.getType());

		PropertyResource configResource = config.getJson();
		PropertyResource request = configResource.getPropertyAsResource(
				ACTIVITY_URI.resolve("#request"));
		assertEquals(ACTIVITY_URI.resolve("#Request"), request.getTypeURI());
		// A sub-class of HTTP_URI.resolve("#Request")

		URI toolId = request.getPropertyAsResourceURI(
				HTTP_URI.resolve("#mthd"));
		assertEquals(HTTP_METHODS_URI.resolve("#PUT"),
				toolId);

		String urlSignature = request.getPropertyAsString(
				ACTIVITY_URI.resolve("#absoluteURITemplate"));
		assertEquals("http://example.com/{thing}/user.xml?id={userID}", urlSignature);

		Map<String, String> foundHeaders = new HashMap<String, String>();
		PropertyList headers = request.getPropertyAsList(HTTP_URI.resolve("#headers"));
		for (PropertyObject header : headers) {
			PropertyResource reqHeader = (PropertyResource) header;
			String fieldName = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldName"));
			String value;
			if (reqHeader.hasProperty(HTTP_URI.resolve("#fieldValue"))) {
				value = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldValue"));
			} else if (reqHeader.hasProperty(ACTIVITY_URI.resolve("#use100Continue"))) {
				assertEquals(true,
						reqHeader.getPropertyAsLiteral(ACTIVITY_URI.resolve("#use100Continue")).getLiteralValueAsBoolean());
				value = "--use100Continue--";
			} else {
				value = "--undefinedValue--";
			}
			foundHeaders.put(fieldName, value);
			assertEquals(HTTP_URI.resolve("#RequestHeader"), reqHeader.getTypeURI());
		}
		assertEquals(3, foundHeaders.size());

		assertEquals("application/xml", foundHeaders.get("Accept"));
		// Content-Type and Expect should *not* be included if the method is GET/HEAD/DELETE
//		assertFalse(foundHeaders.containsKey("Content-Type"));
//		assertFalse(foundHeaders.containsKey("Expect"));
		assertEquals("application/json", foundHeaders.get("Content-Type"));
		 assertEquals("--use100Continue--", foundHeaders.get("Expect"));


//		assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#showRedirectionOutputPort")));
		assertTrue(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#showRedirectionOutputPort")).getLiteralValueAsBoolean());
		//assertFalse(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#escapeParameters")).getLiteralValueAsBoolean());
		assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#escapeParameters")));


		// Check ports
//		assertEquals(3, activity.getInputPorts().size());
//		InputActivityPort userID = activity.getInputPorts().getByName("userID");
//		assertEquals((Integer)0, userID.getDepth());
//
//		InputActivityPort thing = activity.getInputPorts().getByName("thing");
//		assertEquals((Integer)0, thing.getDepth());
//
//		InputActivityPort inputBody = activity.getInputPorts().getByName("inputBody");
//		assertEquals((Integer)0, inputBody.getDepth());
//
//
//		assertEquals(3, activity.getOutputPorts().size());
//		OutputActivityPort responseBody = activity.getOutputPorts().getByName("responseBody");
//		assertEquals((Integer)0, responseBody.getDepth());
//
//		OutputActivityPort status = activity.getOutputPorts().getByName("status");
//		assertEquals((Integer)0, status.getDepth());
//
//		OutputActivityPort redirection = activity.getOutputPorts().getByName("redirection");
//		assertEquals((Integer)0, redirection.getDepth());
//
//
//		PropertyResource userIDDef = scufl2Tools.portDefinitionFor(userID, profile);
//		assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), userIDDef.getTypeURI());
//		assertEquals(PropertyLiteral.XSD_STRING,
//				userIDDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));
//
//		PropertyResource thingDef = scufl2Tools.portDefinitionFor(thing, profile);
//		assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), thingDef.getTypeURI());
//		assertEquals(PropertyLiteral.XSD_STRING,
//				thingDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));
//
//		PropertyResource inputBodyDef = scufl2Tools.portDefinitionFor(inputBody, profile);
//		assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), inputBodyDef.getTypeURI());
//		assertEquals(PropertyLiteral.XSD_STRING,
//				inputBodyDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));


	}

	@Test
		public void put2_2_resaved() throws Exception {
			WorkflowBundle bundle = parse2_2_saved_2_3();
			Profile profile = bundle.getMainProfile();
			//System.out.println(bundle.getMainWorkflow().getProcessors().getNames());
			// [default, post, put]
			Processor proc = bundle.getMainWorkflow().getProcessors()
					.getByName("put");
			assertNotNull(proc);
			Configuration config = scufl2Tools
					.configurationForActivityBoundToProcessor(proc, profile);
			assertNotNull(config);
			assertEquals(ACTIVITY_URI.resolve("#Config"),
					config.getType());

			Activity activity = (Activity) config.getConfigures();
			assertEquals(ACTIVITY_URI, activity.getType());

			PropertyResource configResource = config.getJson();
			PropertyResource request = configResource.getPropertyAsResource(
					ACTIVITY_URI.resolve("#request"));
			assertEquals(ACTIVITY_URI.resolve("#Request"), request.getTypeURI());
			// A sub-class of HTTP_URI.resolve("#Request")

			URI toolId = request.getPropertyAsResourceURI(
					HTTP_URI.resolve("#mthd"));
			assertEquals(HTTP_METHODS_URI.resolve("#PUT"),
					toolId);

			String urlSignature = request.getPropertyAsString(
					ACTIVITY_URI.resolve("#absoluteURITemplate"));
			assertEquals("http://example.com/{thing}/user.xml?id={userID}", urlSignature);

			Map<String, String> foundHeaders = new HashMap<String, String>();
			PropertyList headers = request.getPropertyAsList(HTTP_URI.resolve("#headers"));
			for (PropertyObject header : headers) {
				PropertyResource reqHeader = (PropertyResource) header;
				String fieldName = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldName"));
				String value;
				if (reqHeader.hasProperty(HTTP_URI.resolve("#fieldValue"))) {
					value = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldValue"));
				} else if (reqHeader.hasProperty(ACTIVITY_URI.resolve("#use100Continue"))) {
					assertEquals(true,
							reqHeader.getPropertyAsLiteral(ACTIVITY_URI.resolve("#use100Continue")).getLiteralValueAsBoolean());
					value = "--use100Continue--";
				} else {
					value = "--undefinedValue--";
				}
				foundHeaders.put(fieldName, value);
				assertEquals(HTTP_URI.resolve("#RequestHeader"), reqHeader.getTypeURI());
			}
			assertEquals(3, foundHeaders.size());

			assertEquals("application/xml", foundHeaders.get("Accept"));
			// Content-Type and Expect should *not* be included if the method is GET/HEAD/DELETE
	//		assertFalse(foundHeaders.containsKey("Content-Type"));
	//		assertFalse(foundHeaders.containsKey("Expect"));
			assertEquals("application/json", foundHeaders.get("Content-Type"));
			 assertEquals("--use100Continue--", foundHeaders.get("Expect"));


	//		assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#showRedirectionOutputPort")));
			assertTrue(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#showRedirectionOutputPort")).getLiteralValueAsBoolean());
			//assertFalse(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#escapeParameters")).getLiteralValueAsBoolean());
			assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#escapeParameters")));


			// Check ports
//			assertEquals(3, activity.getInputPorts().size());
//			InputActivityPort userID = activity.getInputPorts().getByName("userID");
//			assertEquals((Integer)0, userID.getDepth());
//
//			InputActivityPort thing = activity.getInputPorts().getByName("thing");
//			assertEquals((Integer)0, thing.getDepth());
//
//			InputActivityPort inputBody = activity.getInputPorts().getByName("inputBody");
//			assertEquals((Integer)0, inputBody.getDepth());
//
//
//			assertEquals(3, activity.getOutputPorts().size());
//			OutputActivityPort responseBody = activity.getOutputPorts().getByName("responseBody");
//			assertEquals((Integer)0, responseBody.getDepth());
//
//			OutputActivityPort status = activity.getOutputPorts().getByName("status");
//			assertEquals((Integer)0, status.getDepth());
//
//			OutputActivityPort redirection = activity.getOutputPorts().getByName("redirection");
//			assertEquals((Integer)0, redirection.getDepth());
//
//
//			PropertyResource userIDDef = scufl2Tools.portDefinitionFor(userID, profile);
//			assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), userIDDef.getTypeURI());
//			assertEquals(PropertyLiteral.XSD_STRING,
//					userIDDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));
//
//			PropertyResource thingDef = scufl2Tools.portDefinitionFor(thing, profile);
//			assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), thingDef.getTypeURI());
//			assertEquals(PropertyLiteral.XSD_STRING,
//					thingDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));
//
//			PropertyResource inputBodyDef = scufl2Tools.portDefinitionFor(inputBody, profile);
//			assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), inputBodyDef.getTypeURI());
//			assertEquals(PropertyLiteral.XSD_STRING,
//					inputBodyDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));


		}

	@Test
		public void put2_3() throws Exception {
			WorkflowBundle bundle = parse2_3();
			Profile profile = bundle.getMainProfile();
			//System.out.println(bundle.getMainWorkflow().getProcessors().getNames());
			// [default, post, put]
			Processor proc = bundle.getMainWorkflow().getProcessors()
					.getByName("put");
			assertNotNull(proc);
			Configuration config = scufl2Tools
					.configurationForActivityBoundToProcessor(proc, profile);
			assertNotNull(config);
			assertEquals(ACTIVITY_URI.resolve("#Config"),
					config.getType());

			Activity activity = (Activity) config.getConfigures();
			assertEquals(ACTIVITY_URI, activity.getType());

			PropertyResource configResource = config.getJson();
			PropertyResource request = configResource.getPropertyAsResource(
					ACTIVITY_URI.resolve("#request"));
			assertEquals(ACTIVITY_URI.resolve("#Request"), request.getTypeURI());
			// A sub-class of HTTP_URI.resolve("#Request")

			URI toolId = request.getPropertyAsResourceURI(
					HTTP_URI.resolve("#mthd"));
			assertEquals(HTTP_METHODS_URI.resolve("#PUT"),
					toolId);

			String urlSignature = request.getPropertyAsString(
					ACTIVITY_URI.resolve("#absoluteURITemplate"));
			assertEquals("http://www.uniprot.org/{db}/{id}.xml", urlSignature);

			Map<String, String> foundHeaders = new HashMap<String, String>();
			PropertyList headers = request.getPropertyAsList(HTTP_URI.resolve("#headers"));
			for (PropertyObject header : headers) {
				PropertyResource reqHeader = (PropertyResource) header;
				String fieldName = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldName"));
				String value;
				if (reqHeader.hasProperty(HTTP_URI.resolve("#fieldValue"))) {
					value = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldValue"));
				} else if (reqHeader.hasProperty(ACTIVITY_URI.resolve("#use100Continue"))) {
					assertEquals(true,
							reqHeader.getPropertyAsLiteral(ACTIVITY_URI.resolve("#use100Continue")).getLiteralValueAsBoolean());
					value = "--use100Continue--";
				} else {
					value = "--undefinedValue--";
				}
				foundHeaders.put(fieldName, value);
				assertEquals(HTTP_URI.resolve("#RequestHeader"), reqHeader.getTypeURI());
			}
			assertEquals(5, foundHeaders.size());

			assertEquals("audio/mp4", foundHeaders.get("Accept"));
			// Content-Type and Expect should *not* be included if the method is GET/HEAD/DELETE
	//		assertFalse(foundHeaders.containsKey("Content-Type"));
	//		assertFalse(foundHeaders.containsKey("Expect"));
			assertEquals("application/xml", foundHeaders.get("Content-Type"));
			 assertEquals("--use100Continue--", foundHeaders.get("Expect"));
			 assertEquals("Soup", foundHeaders.get("X-Fish"));
			 assertEquals("Very funny", foundHeaders.get("X-Taverna"));


	//		assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#showRedirectionOutputPort")));
			assertTrue(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#showRedirectionOutputPort")).getLiteralValueAsBoolean());
			//assertFalse(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#escapeParameters")).getLiteralValueAsBoolean());
			assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#escapeParameters")));


			// Check ports
//			assertEquals(3, activity.getInputPorts().size());
//			InputActivityPort db = activity.getInputPorts().getByName("db");
//			assertEquals((Integer)0, db.getDepth());
//
//			InputActivityPort id = activity.getInputPorts().getByName("id");
//			assertEquals((Integer)0, id.getDepth());
//
//			InputActivityPort inputBody = activity.getInputPorts().getByName("inputBody");
//			assertEquals((Integer)0, inputBody.getDepth());
//
//
//			assertEquals(3, activity.getOutputPorts().size());
//			OutputActivityPort responseBody = activity.getOutputPorts().getByName("responseBody");
//			assertEquals((Integer)0, responseBody.getDepth());
//
//			OutputActivityPort status = activity.getOutputPorts().getByName("status");
//			assertEquals((Integer)0, status.getDepth());
//
//			OutputActivityPort redirection = activity.getOutputPorts().getByName("redirection");
//			assertEquals((Integer)0, redirection.getDepth());
//
//
//			PropertyResource dbDef = scufl2Tools.portDefinitionFor(db, profile);
//			assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), dbDef.getTypeURI());
//			assertEquals(PropertyLiteral.XSD_STRING,
//					dbDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));
//
//			PropertyResource idDef = scufl2Tools.portDefinitionFor(id, profile);
//			assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), idDef.getTypeURI());
//			assertEquals(PropertyLiteral.XSD_STRING,
//					idDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));
//
//			PropertyResource inputBodyDef = scufl2Tools.portDefinitionFor(inputBody, profile);
//			assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), inputBodyDef.getTypeURI());
//			assertEquals(PropertyLiteral.XSD_STRING,
//					inputBodyDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));


		}

	@Test
		public void post2_2() throws Exception {
			WorkflowBundle bundle = parse2_2();
			Profile profile = bundle.getMainProfile();
			//System.out.println(bundle.getMainWorkflow().getProcessors().getNames());
			// [default, post, put]
			Processor proc = bundle.getMainWorkflow().getProcessors()
					.getByName("post");
			assertNotNull(proc);
			Configuration config = scufl2Tools
					.configurationForActivityBoundToProcessor(proc, profile);
			assertNotNull(config);
			assertEquals(ACTIVITY_URI.resolve("#Config"),
					config.getType());

			Activity activity = (Activity) config.getConfigures();
			assertEquals(ACTIVITY_URI, activity.getType());

			PropertyResource configResource = config.getJson();
			PropertyResource request = configResource.getPropertyAsResource(
					ACTIVITY_URI.resolve("#request"));
			assertEquals(ACTIVITY_URI.resolve("#Request"), request.getTypeURI());
			// A sub-class of HTTP_URI.resolve("#Request")

			URI toolId = request.getPropertyAsResourceURI(
					HTTP_URI.resolve("#mthd"));
			assertEquals(HTTP_METHODS_URI.resolve("#POST"),
					toolId);

			String urlSignature = request.getPropertyAsString(
					ACTIVITY_URI.resolve("#absoluteURITemplate"));
			assertEquals("http://www.myexperiment.org/user.xml?id={userID}", urlSignature);

			Map<String, String> foundHeaders = new HashMap<String, String>();
			PropertyList headers = request.getPropertyAsList(HTTP_URI.resolve("#headers"));
			for (PropertyObject header : headers) {
				PropertyResource reqHeader = (PropertyResource) header;
				String fieldName = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldName"));
				String value;
				if (reqHeader.hasProperty(HTTP_URI.resolve("#fieldValue"))) {
					value = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldValue"));
				} else if (reqHeader.hasProperty(ACTIVITY_URI.resolve("#use100Continue"))) {
					assertEquals(true,
							reqHeader.getPropertyAsLiteral(ACTIVITY_URI.resolve("#use100Continue")).getLiteralValueAsBoolean());
					value = "--use100Continue--";
				} else {
					value = "--undefinedValue--";
				}
				foundHeaders.put(fieldName, value);
				assertEquals(HTTP_URI.resolve("#RequestHeader"), reqHeader.getTypeURI());
			}
			assertEquals(2, foundHeaders.size());

			assertEquals("text/plain", foundHeaders.get("Accept"));
			// Content-Type and Expect should *not* be included if the method is GET/HEAD/DELETE
	//		assertFalse(foundHeaders.containsKey("Content-Type"));
			assertFalse(foundHeaders.containsKey("Expect"));
			assertEquals("application/zip", foundHeaders.get("Content-Type"));
//			 assertEquals("--use100Continue--", foundHeaders.get("Expect"));


			assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#showRedirectionOutputPort")));
//			assertTrue(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#showRedirectionOutputPort")).getLiteralValueAsBoolean());
			//assertFalse(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#escapeParameters")).getLiteralValueAsBoolean());
			assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#escapeParameters")));


			// Check ports
//			assertEquals(2, activity.getInputPorts().size());
//			InputActivityPort userID = activity.getInputPorts().getByName("userID");
//			assertEquals((Integer)0, userID.getDepth());
//
//			InputActivityPort inputBody = activity.getInputPorts().getByName("inputBody");
//			assertEquals((Integer)0, inputBody.getDepth());
//
//
//			assertEquals(2, activity.getOutputPorts().size());
//			OutputActivityPort responseBody = activity.getOutputPorts().getByName("responseBody");
//			assertEquals((Integer)0, responseBody.getDepth());
//
//			OutputActivityPort status = activity.getOutputPorts().getByName("status");
//			assertEquals((Integer)0, status.getDepth());
//
//
//			PropertyResource userIDDef = scufl2Tools.portDefinitionFor(userID, profile);
//			assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), userIDDef.getTypeURI());
//			assertEquals(PropertyLiteral.XSD_STRING,
//					userIDDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));
//
//			PropertyResource inputBodyDef = scufl2Tools.portDefinitionFor(inputBody, profile);
//			assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), inputBodyDef.getTypeURI());
//			assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#binary"),
//					inputBodyDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));


		}

	@Test
			public void post2_2_saved_2_3() throws Exception {
				WorkflowBundle bundle = parse2_2_saved_2_3();
				Profile profile = bundle.getMainProfile();
				//System.out.println(bundle.getMainWorkflow().getProcessors().getNames());
				// [default, post, put]
				Processor proc = bundle.getMainWorkflow().getProcessors()
						.getByName("post");
				assertNotNull(proc);
				Configuration config = scufl2Tools
						.configurationForActivityBoundToProcessor(proc, profile);
				assertNotNull(config);
				assertEquals(ACTIVITY_URI.resolve("#Config"),
						config.getType());

				Activity activity = (Activity) config.getConfigures();
				assertEquals(ACTIVITY_URI, activity.getType());

				PropertyResource configResource = config.getJson();
				PropertyResource request = configResource.getPropertyAsResource(
						ACTIVITY_URI.resolve("#request"));
				assertEquals(ACTIVITY_URI.resolve("#Request"), request.getTypeURI());
				// A sub-class of HTTP_URI.resolve("#Request")

				URI toolId = request.getPropertyAsResourceURI(
						HTTP_URI.resolve("#mthd"));
				assertEquals(HTTP_METHODS_URI.resolve("#POST"),
						toolId);

				String urlSignature = request.getPropertyAsString(
						ACTIVITY_URI.resolve("#absoluteURITemplate"));
				assertEquals("http://www.myexperiment.org/user.xml?id={userID}", urlSignature);

				Map<String, String> foundHeaders = new HashMap<String, String>();
				PropertyList headers = request.getPropertyAsList(HTTP_URI.resolve("#headers"));
				for (PropertyObject header : headers) {
					PropertyResource reqHeader = (PropertyResource) header;
					String fieldName = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldName"));
					String value;
					if (reqHeader.hasProperty(HTTP_URI.resolve("#fieldValue"))) {
						value = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldValue"));
					} else if (reqHeader.hasProperty(ACTIVITY_URI.resolve("#use100Continue"))) {
						assertEquals(true,
								reqHeader.getPropertyAsLiteral(ACTIVITY_URI.resolve("#use100Continue")).getLiteralValueAsBoolean());
						value = "--use100Continue--";
					} else {
						value = "--undefinedValue--";
					}
					foundHeaders.put(fieldName, value);
					assertEquals(HTTP_URI.resolve("#RequestHeader"), reqHeader.getTypeURI());
				}
				assertEquals(2, foundHeaders.size());

				assertEquals("text/plain", foundHeaders.get("Accept"));
				// Content-Type and Expect should *not* be included if the method is GET/HEAD/DELETE
		//		assertFalse(foundHeaders.containsKey("Content-Type"));
				assertFalse(foundHeaders.containsKey("Expect"));
				assertEquals("application/zip", foundHeaders.get("Content-Type"));
	//			 assertEquals("--use100Continue--", foundHeaders.get("Expect"));


				assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#showRedirectionOutputPort")));
	//			assertTrue(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#showRedirectionOutputPort")).getLiteralValueAsBoolean());
				//assertFalse(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#escapeParameters")).getLiteralValueAsBoolean());
				assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#escapeParameters")));


				// Check ports
//				assertEquals(2, activity.getInputPorts().size());
//				InputActivityPort userID = activity.getInputPorts().getByName("userID");
//				assertEquals((Integer)0, userID.getDepth());
//
//				InputActivityPort inputBody = activity.getInputPorts().getByName("inputBody");
//				assertEquals((Integer)0, inputBody.getDepth());
//
//
//				assertEquals(2, activity.getOutputPorts().size());
//				OutputActivityPort responseBody = activity.getOutputPorts().getByName("responseBody");
//				assertEquals((Integer)0, responseBody.getDepth());
//
//				OutputActivityPort status = activity.getOutputPorts().getByName("status");
//				assertEquals((Integer)0, status.getDepth());
//
//
//				PropertyResource userIDDef = scufl2Tools.portDefinitionFor(userID, profile);
//				assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), userIDDef.getTypeURI());
//				assertEquals(PropertyLiteral.XSD_STRING,
//						userIDDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));
//
//				PropertyResource inputBodyDef = scufl2Tools.portDefinitionFor(inputBody, profile);
//				assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), inputBodyDef.getTypeURI());
//				assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#binary"),
//						inputBodyDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));


			}

	@Test
			public void post2_3() throws Exception {
				WorkflowBundle bundle = parse2_3();
				Profile profile = bundle.getMainProfile();
				//System.out.println(bundle.getMainWorkflow().getProcessors().getNames());
				// [default, post, put]
				Processor proc = bundle.getMainWorkflow().getProcessors()
						.getByName("post");
				assertNotNull(proc);
				Configuration config = scufl2Tools
						.configurationForActivityBoundToProcessor(proc, profile);
				assertNotNull(config);
				assertEquals(ACTIVITY_URI.resolve("#Config"),
						config.getType());

				Activity activity = (Activity) config.getConfigures();
				assertEquals(ACTIVITY_URI, activity.getType());

				PropertyResource configResource = config.getJson();
				PropertyResource request = configResource.getPropertyAsResource(
						ACTIVITY_URI.resolve("#request"));
				assertEquals(ACTIVITY_URI.resolve("#Request"), request.getTypeURI());
				// A sub-class of HTTP_URI.resolve("#Request")

				URI toolId = request.getPropertyAsResourceURI(
						HTTP_URI.resolve("#mthd"));
				assertEquals(HTTP_METHODS_URI.resolve("#POST"),
						toolId);

				String urlSignature = request.getPropertyAsString(
						ACTIVITY_URI.resolve("#absoluteURITemplate"));
				assertEquals("http://www.uniprot.org/uniprot/{id}.xml", urlSignature);

				Map<String, String> foundHeaders = new HashMap<String, String>();
				PropertyList headers = request.getPropertyAsList(HTTP_URI.resolve("#headers"));
				for (PropertyObject header : headers) {
					PropertyResource reqHeader = (PropertyResource) header;
					String fieldName = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldName"));
					String value;
					if (reqHeader.hasProperty(HTTP_URI.resolve("#fieldValue"))) {
						value = reqHeader.getPropertyAsString(HTTP_URI.resolve("#fieldValue"));
					} else if (reqHeader.hasProperty(ACTIVITY_URI.resolve("#use100Continue"))) {
						assertEquals(true,
								reqHeader.getPropertyAsLiteral(ACTIVITY_URI.resolve("#use100Continue")).getLiteralValueAsBoolean());
						value = "--use100Continue--";
					} else {
						value = "--undefinedValue--";
					}
					foundHeaders.put(fieldName, value);
					assertEquals(HTTP_URI.resolve("#RequestHeader"), reqHeader.getTypeURI());
				}
				assertEquals(2, foundHeaders.size());

				assertEquals("application/xml", foundHeaders.get("Accept"));
				// Content-Type and Expect should *not* be included if the method is GET/HEAD/DELETE
		//		assertFalse(foundHeaders.containsKey("Content-Type"));
				assertFalse(foundHeaders.containsKey("Expect"));
				assertEquals("application/zip", foundHeaders.get("Content-Type"));
	//			 assertEquals("--use100Continue--", foundHeaders.get("Expect"));


				assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#showRedirectionOutputPort")));
	//			assertTrue(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#showRedirectionOutputPort")).getLiteralValueAsBoolean());
				//assertFalse(configResource.getPropertyAsLiteral(ACTIVITY_URI.resolve("#escapeParameters")).getLiteralValueAsBoolean());
				assertFalse(configResource.hasProperty(ACTIVITY_URI.resolve("#escapeParameters")));


				// Check ports
//				assertEquals(2, activity.getInputPorts().size());
//				InputActivityPort id = activity.getInputPorts().getByName("id");
//				assertEquals((Integer)0, id.getDepth());
//
//				InputActivityPort inputBody = activity.getInputPorts().getByName("inputBody");
//				assertEquals((Integer)0, inputBody.getDepth());
//
//
//				assertEquals(2, activity.getOutputPorts().size());
//				OutputActivityPort responseBody = activity.getOutputPorts().getByName("responseBody");
//				assertEquals((Integer)0, responseBody.getDepth());
//
//				OutputActivityPort status = activity.getOutputPorts().getByName("status");
//				assertEquals((Integer)0, status.getDepth());
//
//
//				PropertyResource idDef = scufl2Tools.portDefinitionFor(id, profile);
//				assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), idDef.getTypeURI());
//				assertEquals(PropertyLiteral.XSD_STRING,
//						idDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));
//
//				PropertyResource inputBodyDef = scufl2Tools.portDefinitionFor(inputBody, profile);
//				assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"), inputBodyDef.getTypeURI());
//				assertEquals(Scufl2Tools.PORT_DEFINITION.resolve("#binary"),
//						inputBodyDef.getPropertyAsResourceURI(Scufl2Tools.PORT_DEFINITION.resolve("#dataType")));


			}

*/
}
