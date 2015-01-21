package org.apache.taverna.robundle.fs;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;

import org.junit.Test;

public class TestBundlePaths extends Helper {

	@Test
	public void endsWith() throws Exception {
		Path root = fs.getRootDirectory();
		Path barBazAbs = root.resolve("bar/baz");
		System.out.println(barBazAbs);
		Path barBaz = root.relativize(barBazAbs);
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

	@Test
	public void parent() throws Exception {
		Path root = fs.getRootDirectory();
		assertNull(root.getParent());
	}

}
