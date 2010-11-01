package uk.org.taverna.scufl2.usecases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.translator.t2flow.ParseException;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;

public class TestProcessorNames {

	@Test
	public void processorNames() throws JAXBException, IOException,
			ParseException {
		InputStream workflow = getClass()
		.getResourceAsStream(
				"/workflows/biomartandembossanalysis_904962.t2flow");
		assertNotNull(workflow);
		
		T2FlowParser t2flowParser = new T2FlowParser();
		WorkflowBundle ro = t2flowParser.parseT2Flow(workflow);
		
		List<String> expected = Arrays.asList("CreateFasta",
				"FlattenImageList", "GetUniqueHomolog", "emma",
				"getHSapSequence", "getMMusSequence", "getRNorSequence",
				"hsapiensGeneEnsembl", "plot", "seqret");
		ProcessorNames processorNames = new ProcessorNames();
		assertEquals(expected, processorNames.showProcessorNames(ro));
		System.out.println(processorNames.showProcessorTree(ro));
	}
}
