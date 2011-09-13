/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener.MismatchConfigurableTypeProblem;
import uk.org.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener.NullFieldProblem;


/**
 * @author alanrw
 *
 */
public class TestConfiguration {
	
	@Test
	public void testIdenticalConfigurableTypes() {
		Configuration configuration = new Configuration();
		Activity a = new Activity();
		URI tavernaUri = null;
		try {
			tavernaUri = new URI("http://www.taverna.org.uk");
		} catch (URISyntaxException e) {
			return;
		}
		configuration.setConfigures(a);
		configuration.setConfigurableType(tavernaUri);
		a.setConfigurableType(tavernaUri);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(configuration, false, rcvl);
		
		Set<MismatchConfigurableTypeProblem> mismatchConfigurableTypeProblems = rcvl.getMismatchConfigurableTypeProblems();
		assertEquals(0, mismatchConfigurableTypeProblems.size());
	}
	
	@Ignore
	public void testEqualConfigurableTypes() {
		Configuration configuration = new Configuration();
		Activity a = new Activity();
		URI tavernaUri = null;
		URI tavernaUri2 = null;
		try {
			tavernaUri = new URI("http://www.taverna.org.uk");
			tavernaUri2 = new URI("http://www.taverna.org.uk");
		} catch (URISyntaxException e) {
			return;
		}
		configuration.setConfigures(a);
		configuration.setConfigurableType(tavernaUri);
		a.setConfigurableType(tavernaUri2);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(configuration, false, rcvl);
		
		Set<MismatchConfigurableTypeProblem> mismatchConfigurableTypeProblems = rcvl.getMismatchConfigurableTypeProblems();
		assertEquals(0, mismatchConfigurableTypeProblems.size());
	}
	
	@Ignore
	public void testMismatchingConfigurableTypes() {
		Configuration configuration = new Configuration();
		Activity a = new Activity();
		URI tavernaUri = null;
		URI myGridUri = null;
		try {
			tavernaUri = new URI("http://www.taverna.org.uk");
			myGridUri = new URI("http://www.mygrid.org.uk");
		} catch (URISyntaxException e) {
			return;
		}
		configuration.setConfigures(a);
		configuration.setConfigurableType(tavernaUri);
		a.setConfigurableType(myGridUri);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(configuration, false, rcvl);
		
		Set<MismatchConfigurableTypeProblem> mismatchConfigurableTypeProblems = rcvl.getMismatchConfigurableTypeProblems();
		assertEquals(1, mismatchConfigurableTypeProblems.size());
		boolean mismatchProblem = false;
		for (MismatchConfigurableTypeProblem nlp : mismatchConfigurableTypeProblems) {
			if (nlp.getBean().equals(configuration) && nlp.getConfigurable().equals(a)) {
				mismatchProblem = true;
			}
		}
		assertTrue(mismatchProblem);
	}	

	@Test
	public void testCorrectnessOfMissingConfigures() {
		Configuration configuration = new Configuration();
		PropertyResource pr = new PropertyResource();
		URI tavernaUri = null;
		try {
			tavernaUri = new URI("http://www.taverna.org.uk");
		} catch (URISyntaxException e) {
			return;
		}
		configuration.setConfigurableType(tavernaUri);
		configuration.setPropertyResource(pr);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(configuration, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(0, nullFieldProblems.size()); // only done when completeness check

	}	

	@Test
	public void testCompletenessOfMissingConfigures() {
		Configuration configuration = new Configuration();
		PropertyResource pr = new PropertyResource();
		URI tavernaUri = null;
		try {
			tavernaUri = new URI("http://www.taverna.org.uk");
		} catch (URISyntaxException e) {
			return;
		}
		configuration.setPropertyResource(pr);
		configuration.setConfigurableType(tavernaUri);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(configuration, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertFalse(nullFieldProblems.isEmpty()); // only done when completeness check
		
		boolean fieldProblem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(configuration) && nlp.getFieldName().equals("configures")) {
				fieldProblem = true;
			}
		}
		assertTrue(fieldProblem);
	}
	
	// Cannot check propertyResource because of SCUFL2-97
}
