package org.purl.wf4ever.robundle;

import java.awt.Desktop;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.purl.wf4ever.robundle.manifest.Agent;
import org.purl.wf4ever.robundle.manifest.Manifest;
import org.purl.wf4ever.robundle.manifest.PathAnnotation;

public class MotifAnalysisIT {
    @Test
    public void motifAnalysis() throws Exception {

        // The new RO bundle
        Path ro = Files.createTempFile("motifAnalysis", ".robundle.zip");        
        try (Bundle bundle = Bundles.createBundle(ro)) {

            Path orig = Paths.get(getClass().getResource("/motifAnalysis.zip").toURI());
            
            // Copy the motifAnalysis/ folder
            try (FileSystem origfs = FileSystems.newFileSystem(orig, null)) {
                Path origFolder = origfs.getPath("motifAnalysis/");
                Bundles.copyRecursively(origFolder, 
                        bundle.getRoot(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            }

            
            // TODO: Generating manifest should be automatic!
            
            // Generate manifest
            Manifest manifest = new Manifest(bundle);
            manifest.populateFromBundle();


            // attributions

            // Stian made the RO bundle
            Agent stian = new Agent();
            stian.setUri(URI.create("http://soiland-reyes.com/stian/#me"));
            stian.setOrcid(URI.create("http://orcid.org/0000-0001-9842-9718"));
            stian.setName("Stian Soiland-Reyes");            
            manifest.getCreatedBy().add(stian);
            // RO bundle was created "now"
            manifest.setCreatedOn(Files.getLastModifiedTime(ro));

            
            
            // but it was *authored* by Daniel et al
            
            Agent daniel = new Agent();
            daniel.setUri(URI.create("http://delicias.dia.fi.upm.es/members/DGarijo/#me"));
            daniel.setOrcid(URI.create("http://orcid.org/0000-0003-0454-7145"));
            daniel.setName("Daniel Garijo");

            List<Agent> authors = new ArrayList<>();
            authors.add(daniel);
            authors.add(new Agent("Pinar Alper"));
            authors.add(new Agent("Khalid Belhajjame"));
            authors.add(new Agent("Oscar Corcho"));
            authors.add(new Agent("Yolanda Gil"));
            authors.add(new Agent("Carole Goble"));
            manifest.setAuthoredBy(authors);

            // when was the RO authored? We'll say when the README was made..
            Path readme = bundle.getRoot().resolve("README.txt");
            manifest.setAuthoredOn(Files.getLastModifiedTime(readme));
            
            // And who made the README file?
            manifest.getAggregation(readme).setCreatedBy(Arrays.asList(daniel));
            manifest.getAggregation(readme).setMediatype("text/plain");



            // Annotations
            
            PathAnnotation readmeAnnotation = new PathAnnotation();
            readmeAnnotation.setAbout(URI.create("/"));
            readmeAnnotation.setContent(URI.create("/README.txt"));
            readmeAnnotation.generateAnnotationId();
            manifest.getAnnotations().add(readmeAnnotation);

            PathAnnotation website = new PathAnnotation();
            website.setAbout(URI.create("/"));
            website.setContent(URI.create("http://www.oeg-upm.net/files/dgarijo/motifAnalysisSite/"));
            website.generateAnnotationId();
            manifest.getAnnotations().add(website);

            // Write out manifest
            // TODO: This should be done automatically on close()
            manifest.writeAsJsonLD();
        }

        System.out.println("Generated " + ro);
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(ro.toFile());
        }
    }

}
