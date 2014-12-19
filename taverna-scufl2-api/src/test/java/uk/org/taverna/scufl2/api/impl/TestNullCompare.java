package uk.org.taverna.scufl2.api.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class TestNullCompare {
	@Test
	public void testNullCompare() throws Exception {
		List<String> values = Arrays.asList("c", null, "b", null, "a", "c");
		Collections.sort(values, new NullSafeComparator<String>());
		assertEquals(Arrays.asList(null, null, "a", "b", "c", "c"), values);
	}
}
