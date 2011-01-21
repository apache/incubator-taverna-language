package uk.org.taverna.scufl2.usecases;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class TestConvertT2flowScufl2 {
	@Test
	public void testname() throws Exception {
		File tmp = File.createTempFile("scufl2-ebi-interproscan", ".t2flow");
		tmp.deleteOnExit();
		InputStream ebi = getClass().getResourceAsStream("/workflows/t2flow/ebi_interproscan_for_taverna_2_317472.t2flow");
		FileOutputStream output = new FileOutputStream(tmp);
		IOUtils.copy(ebi, output);
		output.close();
		
		ConvertT2flowScufl2.main(new String[]{tmp.getAbsolutePath()});		
		File scufl2File = new File(tmp.getAbsolutePath().replace(".t2flow", ".scufl2"));
		assertTrue(scufl2File.isFile());
		assertNotNull(new ZipFile(scufl2File).getEntry("workflowBundle.rdf"));
		scufl2File.deleteOnExit();
//		System.out.println(scufl2File);
	}
}
