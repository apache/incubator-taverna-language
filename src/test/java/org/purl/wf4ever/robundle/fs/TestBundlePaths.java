package org.purl.wf4ever.robundle.fs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;

import org.junit.Test;
import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.Bundles;

public class TestBundlePaths {
	@Test
	public void endsWith() throws Exception {
		Bundle bundle = Bundles.createBundle();
		bundle.getRoot();
		Path fred = bundle.getRoot();
		Path barBazAbs = fred.resolve("bar/baz");
		System.out.println(barBazAbs);
		Path barBaz = fred.relativize(barBazAbs);
		assertEquals("bar/baz", barBaz.toString());
		assertTrue(barBaz.endsWith("bar/baz"));
		assertFalse(barBaz.endsWith("bar/../bar/baz"));
		Path climber = barBaz.resolve("../baz");
		assertEquals("bar/baz/../baz", climber.toString());
		assertTrue(climber.endsWith("../baz"));
		assertFalse(climber.endsWith("bar/baz"));
		Path climberNorm = climber.normalize();
		assertFalse(climberNorm.endsWith("../baz"));
		assertTrue(climberNorm.endsWith("bar/baz"));
		
	}
}
