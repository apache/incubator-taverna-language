/**
 * 
 */
package org.apache.taverna.scufl2.validation.integration.scufl;
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


import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.translator.scufl.ScuflParser;
import org.apache.taverna.scufl2.validation.correctness.CorrectnessValidator;
import org.apache.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener;
import org.apache.taverna.scufl2.validation.structural.ReportStructuralValidationListener;
import org.apache.taverna.scufl2.validation.structural.StructuralValidator;

/**
 * @author alanrw
 * 
 */
@RunWith(value = Parameterized.class)
public class Test172StarterPack {

	private final static String WORKFLOW_LIST = "/t172starterpacklist";

	private ScuflParser parser;

	private final String url;

	public Test172StarterPack(String url) {
		this.url = url;
	}

	@Before
	public void makeParser() throws JAXBException {
		parser = new ScuflParser();
		parser.setValidating(false);
		parser.setStrict(false);

	}

	@Parameters
	public static List<Object[]> data() throws IOException {
		List<Object[]> result = new ArrayList<Object[]>();
		URL workflowListResource = Test172StarterPack.class
				.getResource(WORKFLOW_LIST);
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(
					workflowListResource.openStream()));

			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				if (!inputLine.startsWith("#") && !inputLine.isEmpty()) {
					result.add(new Object[] { inputLine });
				}
			}
		} catch (IOException e) {
			// TODO
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return result;
	}

	@Test
	public void testWorkflow() throws IOException, JAXBException,
			ReaderException {
		URL workflowURL = new URL(url);
		WorkflowBundle bundle = null;
		bundle = parser.parseScufl(workflowURL.openStream());

		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();

		cv.checkCorrectness(bundle, true, rcvl);
		assertEquals(Collections.EMPTY_SET,
				rcvl.getEmptyIterationStrategyTopNodeProblems());
		assertEquals(Collections.EMPTY_SET,
				rcvl.getIncompatibleGranularDepthProblems());
		assertEquals(Collections.EMPTY_SET,
				rcvl.getMismatchConfigurableTypeProblems());
		assertEquals(Collections.EMPTY_SET, rcvl.getNegativeValueProblems());
		assertEquals(Collections.EMPTY_SET, rcvl.getNonAbsoluteURIProblems());
// FIXME		assertEquals(Collections.EMPTY_SET, rcvl.getNullFieldProblems());
		assertEquals(Collections.EMPTY_SET, rcvl.getOutOfScopeValueProblems());
		assertEquals(Collections.EMPTY_SET,
				rcvl.getPortMentionedTwiceProblems());
		assertEquals(Collections.EMPTY_SET,
				rcvl.getPortMissingFromIterationStrategyStackProblems());
		assertEquals(Collections.EMPTY_SET, rcvl.getWrongParentProblems());

		StructuralValidator sv = new StructuralValidator();
		ReportStructuralValidationListener rsvl = new ReportStructuralValidationListener();
		sv.checkStructure(bundle, rsvl);
		assertEquals(Collections.EMPTY_SET,
				rsvl.getDotProductIterationMismatches());
		assertEquals(Collections.EMPTY_SET, rsvl.getEmptyCrossProducts());
		assertEquals(Collections.EMPTY_SET, rsvl.getEmptyDotProducts());
		assertEquals(Collections.EMPTY_SET, rsvl.getFailedProcessors());
		assertEquals(Collections.EMPTY_SET, rsvl.getIncompleteWorkflows());
		assertEquals(Collections.EMPTY_SET,
				rsvl.getMissingIterationStrategyStacks());
// FIXME 		assertEquals(Collections.EMPTY_SET, rsvl.getMissingMainIncomingDataLinks());
		assertEquals(Collections.EMPTY_SET,
				rsvl.getUnrecognizedIterationStrategyNodes());
// FIXME		assertEquals(Collections.EMPTY_SET, rsvl.getUnresolvedOutputs());
		assertEquals(Collections.EMPTY_SET, rsvl.getUnresolvedProcessors());

	}

}
