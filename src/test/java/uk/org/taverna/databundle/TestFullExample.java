package uk.org.taverna.databundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Desktop;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import org.junit.Test;
import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.manifest.Manifest;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;

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
		    Files.copy(runPath.resolve(filename), 
		               inputs.resolve(filename));
		}
		
		// Outputs
        Path outputs = DataBundles.getOutputs(dataBundle);
        for (String filename : Arrays.asList("getResult_3_output_output.xml",
                "getResult_output_output.octet-stream",
                "Graphical_output.png",
                "Workflow16_getStatus_output_status.txt")) {
            Files.copy(runPath.resolve(filename), 
                    outputs.resolve(filename));
        }
		
        // Provenance
        Files.copy(runPath.resolve("workflowrun.prov.ttl"), 
                DataBundles.getWorkflowRunProvenance(dataBundle));

        // Workflow
        WorkflowBundle wfBundle = wfBundleIO.readBundle(runPath.resolveSibling("ebi_interproscan_newservices_900329.t2flow").toFile(), null);
		DataBundles.setWorkflowBundle(dataBundle, wfBundle);
        
        
        // Intermediate values
		DataBundles.copyRecursively(runPath.resolve("intermediates"),
		        DataBundles.getIntermediates(dataBundle), 
		        StandardCopyOption.REPLACE_EXISTING 
		        //,Bundles.RecursiveCopyOption.THREADED
		        );
		
		// Generate Manifest		
		// TODO: This should be done automatically on close/save
        Manifest manifest = new Manifest(dataBundle);
        manifest.populateFromBundle();
        Path jsonld = manifest.writeAsJsonLD();
        String manifestStr = new String(Files.readAllBytes(jsonld), "UTF8");
		System.out.println(manifestStr);
		
		
		// Saving a data bundle:
		Path zip = Files.createTempFile("databundle", ".zip");
		DataBundles.closeAndSaveBundle(dataBundle, zip);
		// NOTE: From now dataBundle and its Path's are CLOSED 
		// and can no longer be accessed
		
		
		System.out.println("Saved to " + zip);
		if (Desktop.isDesktopSupported()) {
			// Open ZIP file for browsing
			Desktop.getDesktop().open(zip.toFile());
		}
		
		// Loading a data bundle back from disk
		try (Bundle dataBundle2 = DataBundles.openBundle(zip)) {
			assertEquals(zip, dataBundle2.getSource());
			Path loadedInputs = DataBundles.getInputs(dataBundle2);
			
			for (Path port : DataBundles.getPorts(loadedInputs).values()) {
				if (DataBundles.isValue(port)) {
					System.out.print("Value " + port + ": ");
					System.out.println(DataBundles.getStringValue(port));
				} else if (DataBundles.isList(port)) {
					System.out.print("List " + port + ": ");
					for (Path item : DataBundles.getList(port)) {
						// We'll assume depth 1 here
						System.out.print(DataBundles.getStringValue(item));
						System.out.print(", ");
					}
					System.out.println();
				}				
			}			
		}				
	}
}
