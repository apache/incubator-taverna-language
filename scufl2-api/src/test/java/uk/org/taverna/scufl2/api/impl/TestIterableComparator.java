package uk.org.taverna.scufl2.api.impl;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class TestIterableComparator {
	public class ReverseComparableList extends ArrayList<String> implements Comparable<ReverseComparableList> {
		public boolean called = false;
		public ReverseComparableList(String... items) {
			addAll(Arrays.asList(items));
		}

		@Override
		public int compareTo(ReverseComparableList o) {
			called = true;
			// Reverse string order
			return o.toString().compareTo(this.toString());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void listCompare() throws Exception {
		List<List<String>> values = Arrays.asList(Arrays.asList("x", "y"),
				Arrays.asList("a", "b", "c", "d", "e"),
				Arrays.asList("a", "b", "c", "d"),
				Arrays.asList("a", "b", null, "d"));

		List<List<String>> sortedValues = Arrays.asList(
				Arrays.asList("a", "b", null, "d"), // null < c
				Arrays.asList("a", "b", "c", "d"), // shorter list (but matching
													// prefix)
				Arrays.asList("a", "b", "c", "d", "e"), Arrays.asList("x", "y") // "x"
																				// >
																				// "a"
				);

		Collections.sort(values, new IterableComparator());
		assertEquals(sortedValues, values);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void nestedListCompare() throws Exception {
		List<List<List<String>>> values = Arrays.asList(
				Arrays.asList(Arrays.asList("x", "y", "z"),
						Arrays.asList("1", "2", "3"), Arrays.asList("extra")),
				Arrays.asList(Arrays.asList("x", "y", "z"),
						Arrays.asList("1", "2", "3")));

		List<List<List<String>>> sortedValues = Arrays.asList(
				Arrays.asList(Arrays.asList("x", "y", "z"),
						Arrays.asList("1", "2", "3")),
				Arrays.asList(Arrays.asList("x", "y", "z"),
						Arrays.asList("1", "2", "3"), Arrays.asList("extra")));

		Collections.sort(values, new IterableComparator());
		assertEquals(sortedValues, values);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(expected = ClassCastException.class)
	public void intsAndStrings() throws Exception {
		List<Integer> intList = Arrays.asList(1, 2, 3);
		List<String> strList = Arrays.asList("x", "y");
		List values = Arrays.asList(intList, strList);
		Collections.sort(values, new IterableComparator());
	}


	


}
