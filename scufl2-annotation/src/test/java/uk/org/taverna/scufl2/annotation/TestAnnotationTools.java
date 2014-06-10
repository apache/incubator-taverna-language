package uk.org.taverna.scufl2.annotation;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.port.InputWorkflowPort;

public class TestAnnotationTools {

	WorkflowBundleIO bundleIO = new WorkflowBundleIO();
	WorkflowBundle helloanyone;
	AnnotationTools annotations = new AnnotationTools();
	private WorkflowBundle component;
	
	@Before
	public void loadHelloAnyone() throws ReaderException, IOException {
		URL url = getClass().getResource("/helloanyone.t2flow");
		assertNotNull(url);
		helloanyone = bundleIO.readBundle(url, null);
	}
	
	@Before
	public void loadComponent() throws ReaderException, IOException {
		URL url = getClass().getResource("/valid_component_imagemagickconvert.t2flow");
		assertNotNull(url);
		component = bundleIO.readBundle(url, null);
	}
	
	
	@Test
	public void getTitle() {
		assertEquals("Hello Anyone", annotations.getTitle(helloanyone.getMainWorkflow()));
	}
	
	@Test
	public void getCreator() {
		assertEquals("Stian Soiland-Reyes", annotations.getCreator(helloanyone.getMainWorkflow()));
	}

	
	@Test
	public void getDescription() {
		InputWorkflowPort name = helloanyone.getMainWorkflow().getInputPorts().getByName("name");
		String desc = annotations.getDescription(name);
		assertEquals("Your name for the greeting", desc);
	}
	
	@Test
	public void getExample() throws Exception {
		InputWorkflowPort name = helloanyone.getMainWorkflow().getInputPorts().getByName("name");
		String example = annotations.getExampleValue(name);
		assertEquals("World!", example);	
	}
	
	@Test
	public void componentStuff() throws Exception {
		Dataset dataset = annotations.annotationDatasetFor(component.getMainWorkflow());
		String query = "PREFIX comp: <http://purl.org/DP/components#> "
				+ "SELECT ?fits ?from ?to WHERE { "
				+ " GRAPH ?any { "
				+ "?w comp:fits ?fits ; "
				+ "   comp:migrates ?path . "
				+ "?path comp:fromMimetype ?from ; "
				+ "      comp:toMimetype ?to . "
				+ "  }"
				+ "}";
		
		ResultSet select = QueryExecutionFactory.create(query, dataset).execSelect();
		assertTrue(select.hasNext());
		QuerySolution solution = select.next();
		assertEquals("image/tiff", solution.getLiteral("from").toString());
		assertEquals("image/tiff", solution.getLiteral("to").toString());
		assertEquals("MigrationAction", solution.getResource("fits").getLocalName());
	}
	
}
