/**
 * 
 */
package net.sf.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.assertNotNull;

import java.io.FileOutputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import net.sf.taverna.scufl2.api.container.TavernaResearchObject;

import org.junit.Test;

/**
 * @author alanrw
 *
 */
public class TestT2FlowTranslator {

	private static final String AS_T2FLOW = "/as.t2flow";

	@Test
	public void translateSimpleWorkflow() throws Exception {
		URL wfResource = getClass().getResource(AS_T2FLOW);
		assertNotNull("Could not find workflow " + AS_T2FLOW, wfResource);
		T2FlowParser parser = new T2FlowParser();
		TavernaResearchObject researchObj = parser.parseT2Flow(wfResource.openStream());

		JAXBContext jc = JAXBContext.newInstance(TavernaResearchObject.class );
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, 
	              Boolean.TRUE );
	    marshaller.marshal( researchObj, new FileOutputStream("foo.xml") );

	}

}
