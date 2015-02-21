package org.apache.taverna.examples;

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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.apache.taverna.examples.ConvertT2flowToWorkflowBundle;
import org.junit.Test;


public class TestConvertT2flowScufl2 {
	@Test
	public void convertToScufl2() throws Exception {
		File tmp = File.createTempFile("helloworld", ".t2flow");
		tmp.deleteOnExit();
		InputStream ebi = getClass().getResourceAsStream("/workflows/t2flow/helloworld.t2flow");
		FileOutputStream output = new FileOutputStream(tmp);
		IOUtils.copy(ebi, output);
		output.close();
		
		ConvertT2flowToWorkflowBundle.main(new String[]{tmp.getAbsolutePath()});		
		File scufl2File = new File(tmp.getAbsolutePath().replace(".t2flow", ".wfbundle"));
		assertTrue(scufl2File.isFile());
		try (ZipFile zip = new ZipFile(scufl2File)) {
			assertNotNull(zip.getEntry("workflowBundle.rdf"));
		}
		scufl2File.deleteOnExit();
//		System.out.println(scufl2File);
	}
}
