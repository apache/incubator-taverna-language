/**
 * 
 */
package uk.org.taverna.scufl2.validation.integration.t2flow;

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

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.validation.correctness.CorrectnessValidator;
import uk.org.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener;
import uk.org.taverna.scufl2.validation.structural.ReportStructuralValidationListener;
import uk.org.taverna.scufl2.validation.structural.StructuralValidator;

/**
 * @author alanrw
 *
 */
@RunWith(value = Parameterized.class)
public class Test230StarterPack {
	
	private final static String WORKFLOW_LIST = "/t230starterpacklist";
	
	private T2FlowParser parser;

	private final String url;
	
	public Test230StarterPack(String url) {
		this.url = url;
	}

	@Before
	public void makeParser() throws JAXBException {
		parser = new T2FlowParser();
		parser.setValidating(true);
		parser.setStrict(true);
		
	}
	
	@Parameters
	public static List<Object[]> data() throws IOException {
		List<Object[]> result = new ArrayList<Object[]>();
		URL workflowListResource = Test230StarterPack.class.getResource(WORKFLOW_LIST);
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(
				workflowListResource.openStream()));

		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			if (!inputLine.startsWith("#") && !inputLine.isEmpty()) {
				result.add(new Object[] {inputLine});
			}
		 }
		}
		catch (IOException e) {
			// TODO
		}
		finally {
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
			bundle = parser.parseT2Flow(workflowURL.openStream());

			CorrectnessValidator cv = new CorrectnessValidator();
			ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
			
			cv.checkCorrectness(bundle, true, rcvl);
			assertEquals(Collections.EMPTY_SET, rcvl.getEmptyIterationStrategyTopNodeProblems());
			assertEquals(Collections.EMPTY_SET, rcvl.getIncompatibleGranularDepthProblems());
			assertEquals(Collections.EMPTY_SET, rcvl.getMismatchConfigurableTypeProblems());
			assertEquals(Collections.EMPTY_SET, rcvl.getNegativeValueProblems());
			assertEquals(Collections.EMPTY_SET, rcvl.getNonAbsoluteURIProblems());
			assertEquals(Collections.EMPTY_SET, rcvl.getNullFieldProblems());
			assertEquals(Collections.EMPTY_SET, rcvl.getOutOfScopeValueProblems());
			assertEquals(Collections.EMPTY_SET, rcvl.getPortMentionedTwiceProblems());
			assertEquals(Collections.EMPTY_SET, rcvl.getPortMissingFromIterationStrategyStackProblems());
			assertEquals(Collections.EMPTY_SET, rcvl.getWrongParentProblems());
			
			StructuralValidator sv = new StructuralValidator();
			ReportStructuralValidationListener rsvl = new ReportStructuralValidationListener();
			sv.checkStructure(bundle, rsvl);
			assertEquals(Collections.EMPTY_SET, rsvl.getDotProductIterationMismatches());
			assertEquals(Collections.EMPTY_SET, rsvl.getEmptyCrossProducts());
			assertEquals(Collections.EMPTY_SET, rsvl.getEmptyDotProducts());
			assertEquals(Collections.EMPTY_SET, rsvl.getFailedProcessors());
			assertEquals(Collections.EMPTY_SET, rsvl.getIncompleteWorkflows());
			assertEquals(Collections.EMPTY_SET, rsvl.getMissingIterationStrategyStacks());
			assertEquals(Collections.EMPTY_SET, rsvl.getMissingMainIncomingDataLinks());
			assertEquals(Collections.EMPTY_SET, rsvl.getUnrecognizedIterationStrategyNodes());
			assertEquals(Collections.EMPTY_SET, rsvl.getUnresolvedOutputs());
			assertEquals(Collections.EMPTY_SET, rsvl.getUnresolvedProcessors());

	}
	
	

}
