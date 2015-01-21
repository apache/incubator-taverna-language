package org.apache.taverna.scufl2.api.io;

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


import static org.apache.taverna.scufl2.api.io.structure.StructureReader.TEXT_VND_TAVERNA_SCUFL2_STRUCTURE;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Test;


public class TestStructureReader {

	private static final String UTF_8 = "utf-8";
	protected WorkflowBundleIO bundleIO = new WorkflowBundleIO();
	
	public String getStructureFormatWorkflowBundle() throws IOException {
		InputStream helloWorldStream = getClass().getResourceAsStream(
				"HelloWorld.txt");
		return IOUtils.toString(helloWorldStream);
	}
	
	@Test
	public void configurationReadTwice() throws Exception {
		InputStream inputStream = new ByteArrayInputStream(
				getStructureFormatWorkflowBundle().getBytes("utf-8"));
		WorkflowBundle readBundle = bundleIO.readBundle(inputStream,
				TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
		assertEquals(1, readBundle.getMainProfile().getConfigurations().size());
		new Scufl2Tools().setParents(readBundle);
		assertEquals(1, readBundle.getMainProfile().getConfigurations().size());
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bundleIO.writeBundle(readBundle, output, TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
		assertEquals(1, readBundle.getMainProfile().getConfigurations().size());
		String bundleTxt = new String(output.toByteArray(), UTF_8);
                String getStructureFormatWorkflowBundle = getStructureFormatWorkflowBundle();
                bundleTxt = bundleTxt.replaceAll("\r", "").replaceAll("\n", "");
                getStructureFormatWorkflowBundle = getStructureFormatWorkflowBundle.replaceAll("\r", "").replaceAll("\n", "");
		assertEquals(getStructureFormatWorkflowBundle, bundleTxt);
		
	}
	
	@Test
    public void multiLineJson() throws Exception {
	    String struct = getStructureFormatWorkflowBundle();
	    // Make JSON multi-line by adding some whitespace
	    struct = struct.replace("{", "{\n         ");
	    struct = struct.replace("\":\"", "\":\n             \"");
	    struct = struct.replace("}", "\n        }\n");
	    // EG: 
//       {
//        "script":
//            "hello = \"Hello, \" + personName;\nJOptionPane.showMessageDialog(null, hello);"
//       }
	    
//	    System.out.println(struct);
	    
	    InputStream inputStream = new ByteArrayInputStream(
	            struct.getBytes("utf-8"));
        WorkflowBundle readBundle = bundleIO.readBundle(inputStream,
                TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
        assertEquals(1, readBundle.getMainProfile().getConfigurations().size());
        
        Configuration config = readBundle.getMainProfile().getConfigurations().getByName("Hello");
        String script = config.getJson().get("script").asText();
        String expected = "hello = \"Hello, \" + personName;\n"
                + "JOptionPane.showMessageDialog(null, hello);";
        assertEquals(expected, script);        
    }


}
