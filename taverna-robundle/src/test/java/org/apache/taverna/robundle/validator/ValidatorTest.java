package org.apache.taverna.robundle.validator;

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
import static org.junit.Assert.assertNotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.Test;

public class ValidatorTest {

	Path path;
	 	
	@Test
	public void test() throws Exception{
		
		path = Files.createTempFile("test", ".bundle.zip");
		path.toFile().deleteOnExit();
		Files.copy(getClass().getResourceAsStream("/workflowrun.bundle.zip"), path, StandardCopyOption.REPLACE_EXISTING);
		
		RoValidator validator = new RoValidator(path);
		ValidationReport r = validator.check();
		
		assertNotNull("Errors List", r.getErrorList_l());
		assertNotNull("Warnings List", r.getInfoWarnings());
		assertNotNull("Info Warnings List", r.getInfoWarnings_l());
		
		Files.delete(path);
	}

}
