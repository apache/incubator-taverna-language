package uk.org.taverna.scufl2.api.change;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

public class TestChanges {
	@Test
	public void testName() throws Exception {
		
		VersionableResource v2 = new VersionableResource("v3");
		v2.generatedAtTime = today();		
		v2.wasChangedBy = new ChangeSpecification();
		VersionableResource v5 = new VersionableResource("v2");
		v2.wasChangedBy.fromVersion = v5;		
		v2.wasChangedBy.change.add(new Addition("nested"));
		
		v5.wasChangedBy = new ChangeSpecification();
		v5.wasChangedBy.fromVersion = new VersionableResource("v1");
		v5.wasChangedBy.change.add(new Modification());
		
		
		
	}

	private Calendar today() {
		return new GregorianCalendar();
	}
}
