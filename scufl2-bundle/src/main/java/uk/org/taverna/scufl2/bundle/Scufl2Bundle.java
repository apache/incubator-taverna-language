package uk.org.taverna.scufl2.bundle;

import java.net.URI;
import java.util.ArrayList;

import org.odftoolkit.odfdom.pkg.OdfPackage;

public class Scufl2Bundle {
	public Scufl2Bundle() throws Exception {

		OdfPackage odfPackage = OdfPackage.loadPackage("quick.odt");
		System.out.println(odfPackage.getFileEntries());

		for (String odfFileEntry : new ArrayList<String>(
				odfPackage.getFileEntries())) {
			if (odfFileEntry.equals("/")) {
				continue;
			}
			odfPackage.remove(odfFileEntry);
		}

		odfPackage.insert(URI.create("foo.xml"), "workflows/deadbeef.xml",
				"application/vnd.taverna.scufl2.workflow+xml");
		odfPackage
				.setMediaType("application/vnd.taverna.scufl2.research-object");
		odfPackage.save("template.scufl2");

	}
}
