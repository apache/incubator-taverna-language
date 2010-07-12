package uk.org.taverna.scufl2.usecases;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import uk.org.taverna.scufl2.api.container.TavernaResearchObject;
import uk.org.taverna.scufl2.translator.t2flow.ParseException;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;

public class ConvertT2flowScufl2 {
	public static void main(String[] args) throws JAXBException, IOException,
			ParseException {
		T2FlowParser t2flowParser = new T2FlowParser();
		for (String filepath : args) {
			File t2File = new File(filepath);
			String filename = t2File.getName();
			filename = filename.replaceFirst("\\..*", ".scufl2");
			File scufl2File = new File(t2File.getParentFile(), filename);
			TavernaResearchObject ro = t2flowParser.parseT2Flow(t2File);
			writeTavernaResearchObject(ro, scufl2File);
		}
	}

	public static void writeTavernaResearchObject(TavernaResearchObject ro,
			File scufl2File) throws JAXBException, FileNotFoundException {
		JAXBContext jc = JAXBContext.newInstance(TavernaResearchObject.class);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(ro, scufl2File);
	}

}
