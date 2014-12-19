package uk.org.taverna.databundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;
import java.util.UUID;

import org.junit.Test;
import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.manifest.Manifest;

import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
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

        System.out.println("Saved to " + zip);
        if (Desktop.isDesktopSupported()) {
            // Open ZIP file for browsing
            Desktop.getDesktop().open(zip.toFile());
        }

        // Loading a data bundle back from disk
        try (Bundle dataBundle2 = DataBundles.openBundle(zip)) {
            assertEquals(zip, dataBundle2.getSource());

            System.out.println("\n== Inputs");
            printPorts(DataBundles.getInputs(dataBundle2));
            System.out.println("\n== Outputs");
            printPorts(DataBundles.getOutputs(dataBundle2));
            
            System.out.println("\n== Intermediates");
            UUID uuid = UUID.fromString("1f536bcf-ba43-44ec-a983-b30a45f2b739");
            Path intermediate = DataBundles.getIntermediate(dataBundle2, uuid); 
            String intermediateStr = DataBundles.getStringValue(intermediate);
            assertTrue(intermediateStr.contains("<status>RUNNING</status>"));
            System.out.println(uuid + ": " + intermediateStr);
            
            Path prov = DataBundles.getWorkflowRunProvenance(dataBundle2);
            List<String> provLines = Files.readAllLines(prov, Charset.forName("UTF8"));
            System.out.println("\n== Provenance");
            for (String line : provLines.subList(13, 18)) {
                // Show a tiny abstract
                System.out.println(line);
            }
            
            System.out.println("\n== Workflow bundle");
            WorkflowBundle wfb = DataBundles.getWorkflowBundle(dataBundle2);
            System.out.print(wfb.getName());
            System.out.println(" containing processors: ");
            for (Workflow w : wfb.getWorkflows()) {
                for (Processor p : w.getProcessors()) {
                    System.out.print(p.getName() + " ");
                }
            }
            System.out.println();
            
            

        }
    }


    private void printPorts(Path path) throws IOException {
        NavigableMap<String, Path> ports = DataBundles.getPorts(path);
        for (String portName : ports.keySet()) {
            Path port = ports.get(portName);
            if (DataBundles.isValue(port)) {
                System.out.print(portName + ": ");
                long size = Files.size(port);
                if (size < 1024) {
                    // TODO: Detect binaries properly
                    System.out.println(DataBundles.getStringValue(port));
                } else {
                    System.out.println("(" + size + " bytes) " + port);
                }
            }
        }
    }
}
