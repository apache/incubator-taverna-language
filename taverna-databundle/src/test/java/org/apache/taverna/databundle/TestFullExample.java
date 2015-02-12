package org.apache.taverna.databundle;
/*
 *
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
 *
*/


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.taverna.databundle.DataBundles;
import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.manifest.Manifest;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.Test;


public class TestFullExample {

    private static String RUN = "/full-example/ebi-wfrun-2013-05-31/";

    private static WorkflowBundleIO wfBundleIO = new WorkflowBundleIO();

    @Test
    public void writeExample() throws Exception {
        // Create a new (temporary) data bundle
        Bundle dataBundle = DataBundles.createBundle();

        // The run as currently saved to a folder by prov-taverna 1.10
        URL runResource = getClass().getResource(RUN);
        Path runPath = Paths.get(runResource.toURI());

        assertTrue(Files.isDirectory(runPath));

        // In order to preserve existing file extensions we copy as files
        // rather than using the higher-level methods like
        // DataBundles.setStringValue()

        // Inputs
        Path inputs = DataBundles.getInputs(dataBundle);
        for (String filename : Arrays.asList("email.txt", "sequence.txt")) {
            Files.copy(runPath.resolve(filename), inputs.resolve(filename));
        }

        // Outputs
        Path outputs = DataBundles.getOutputs(dataBundle);
        for (String filename : Arrays.asList("getResult_3_output_output.xml",
                "getResult_output_output.octet-stream", "Graphical_output.png",
                "Workflow16_getStatus_output_status.txt")) {
            Files.copy(runPath.resolve(filename), outputs.resolve(filename));
        }

        // Provenance
        Files.copy(runPath.resolve("workflowrun.prov.ttl"),
                DataBundles.getWorkflowRunProvenance(dataBundle));

        // Workflow
        WorkflowBundle wfBundle = wfBundleIO.readBundle(
                runPath.resolveSibling(
                        "ebi_interproscan_newservices_900329.t2flow").toFile(),
                null);
        DataBundles.setWorkflowBundle(dataBundle, wfBundle);

        // Intermediate values
        DataBundles.copyRecursively(runPath.resolve("intermediates"),
                DataBundles.getIntermediates(dataBundle),
                StandardCopyOption.REPLACE_EXISTING);

        // Generate Manifest
        // TODO: This should be done automatically on close/save
        Manifest manifest = new Manifest(dataBundle);
        manifest.populateFromBundle();
        manifest.writeAsJsonLD();

        // Saving a data bundle:
        Path zip = Files.createTempFile("databundle", ".zip");
        DataBundles.closeAndSaveBundle(dataBundle, zip);
        // NOTE: From now dataBundle and its Path's are CLOSED
        // and can no longer be accessed

        //System.out.println("Saved to " + zip);

        // Loading a data bundle back from disk
        try (Bundle dataBundle2 = DataBundles.openBundle(zip)) {
            assertEquals(zip, dataBundle2.getSource());

			List<String> s = new ArrayList<>(DataBundles.getPorts(
					DataBundles.getInputs(dataBundle2)).keySet());
			Collections.sort(s);
			assertEquals("[email, sequence]", s.toString());
			assertEquals(
					"soiland-reyes@cs.manchester.ac.uk",
					DataBundles.getStringValue(DataBundles.getPort(
							DataBundles.getInputs(dataBundle2), "email")));
			s = new ArrayList<>(DataBundles.getPorts(
					DataBundles.getOutputs(dataBundle2)).keySet());
			Collections.sort(s);
			assertEquals(
					"[Graphical_output, Workflow16_getStatus_output_status, "
					+ "getResult_3_output_output, getResult_output_output]",
					s.toString());
			assertEquals("FINISHED", DataBundles.getStringValue(DataBundles
					.getPort(DataBundles.getOutputs(dataBundle2),
							"Workflow16_getStatus_output_status")));
            
            UUID uuid = UUID.fromString("1f536bcf-ba43-44ec-a983-b30a45f2b739");
            Path intermediate = DataBundles.getIntermediate(dataBundle2, uuid); 
            String intermediateStr = DataBundles.getStringValue(intermediate);
            assertTrue(intermediateStr.contains("<status>RUNNING</status>"));
            
            Path prov = DataBundles.getWorkflowRunProvenance(dataBundle2);
            List<String> provLines = Files.readAllLines(prov, Charset.forName("UTF8"));
            assertEquals("	prov:startedAtTime \"2013-05-31T11:23:10.463+01:00\"^^xsd:dateTime ;",
            		provLines.get(15));
            
            WorkflowBundle wfb = DataBundles.getWorkflowBundle(dataBundle2);
            assertEquals("EBI_InterproScan_NewServices", wfb.getName());
            s=new ArrayList<>();
            for (Workflow w : wfb.getWorkflows()) {
                for (Processor p : w.getProcessors()) {
                	s.add(p.getName());
                }
            }
            Collections.sort(s);
            assertEquals("[Status, getResult, getResult_graphic, getResult_graphic_input, "
            		+ "getResult_graphic_output, getResult_input, getResult_output, getResult_xml, "
            		+ "getResult_xml_input, getResult_xml_output, getStatus, getStatus_input, "
            		+ "getStatus_output, run, run_input, run_input_2, run_output, text, visual_png, "
            		+ "xml]", s.toString());
        }
    }
}
