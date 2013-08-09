package uk.org.taverna.databundle;

import static org.junit.Assert.assertTrue;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import org.junit.Test;
import org.purl.wf4ever.robundle.Bundle;
import org.purl.wf4ever.robundle.manifest.Agent;
import org.purl.wf4ever.robundle.manifest.Manifest;
import org.purl.wf4ever.robundle.manifest.PathAnnotation;

import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;

public class TestMusicClassification {

    private static String RUN = "/MusicProcessTaverna/example-run/";

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
        for (String filename : Arrays.asList("MP3URL.txt", "WebServiceAuthenticationVoucher.txt", "GroundTruthURL.txt")) {
            Files.copy(runPath.resolve(filename), inputs.resolve(filename));
        }

        // Outputs
        Path outputs = DataBundles.getOutputs(dataBundle);
        for (String filename : Arrays.asList("ClassificationAccuracy.txt",
                "DetailedClassificationResults.txt")) {
            Files.copy(runPath.resolve(filename), outputs.resolve(filename));
        }

        // Provenance
        Path workflowRunProvenance = DataBundles.getWorkflowRunProvenance(dataBundle);
        Files.copy(runPath.resolve("workflowrun.prov.ttl"),
                workflowRunProvenance);

        // Workflow
        WorkflowBundle wfBundle = wfBundleIO.readBundle(
                runPath.resolveSibling(
                        "MusicClassification.t2flow").toFile(),
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
        
        // Additional metadata
        manifest.getAggregation(workflowRunProvenance).setMediatype("text/turtle");
     
        
        Agent taverna = new Agent();
        taverna.setName("Taverna Workbench 2.4.0");
        manifest.getAggregation(workflowRunProvenance).setCreatedBy(Arrays.asList(taverna));
        
        // Add annotations
        

        // This RO Bundle is about a run
        PathAnnotation bundleAboutRun = new PathAnnotation();
        bundleAboutRun.setAbout(URI.create("http://ns.taverna.org.uk/2011/run/445a1790-4b67-4e44-8287-f8a5838890e2/"));
        bundleAboutRun.setContent(URI.create("/"));
        manifest.getAnnotations().add(bundleAboutRun);

        // TODO: Do we need both the "history" link and the annotation below?
        manifest.setHistory(Arrays.asList(workflowRunProvenance));
        
        // This RO Bundle is described in the provenance file
        PathAnnotation provenanceAboutBundle = new PathAnnotation();
        provenanceAboutBundle.setAbout(URI.create("/"));
        provenanceAboutBundle.setContent(URI.create(workflowRunProvenance.toUri().getPath()));
        manifest.getAnnotations().add(provenanceAboutBundle);
        
        // The wfdesc is about the workflow definition 
        PathAnnotation wfdescAboutWfBundle = new PathAnnotation();
        Path workflow = DataBundles.getWorkflow(dataBundle);
        manifest.getAggregation(workflow).setMediatype("application/vnd.taverna.scufl2.workflow-bundle");
        Path wfdesc = DataBundles.getWorkflowDescription(dataBundle);
        wfdescAboutWfBundle.setAbout(URI.create(workflow.toUri().getPath()));
        wfdescAboutWfBundle.setContent(URI.create(wfdesc.toUri().getPath()));
        manifest.getAnnotations().add(wfdescAboutWfBundle);

        // And the workflow definition is about the workflow
        PathAnnotation wfBundleAboutWf = new PathAnnotation();
        URITools uriTools = new URITools();
        wfBundleAboutWf.setAbout(uriTools.uriForBean(wfBundle.getMainWorkflow()));
        wfBundleAboutWf.setContent(URI.create(workflow.toUri().getPath()));
        manifest.getAnnotations().add(wfBundleAboutWf);

        PathAnnotation wfBundleAboutWfB = new PathAnnotation();
        wfBundleAboutWfB.setAbout(wfBundle.getGlobalBaseURI());
        wfBundleAboutWfB.setContent(URI.create(workflow.toUri().getPath()));
        manifest.getAnnotations().add(wfBundleAboutWfB);
        
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
    }


}
