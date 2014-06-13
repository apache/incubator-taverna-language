RO bundle API
=============


[![Build Status](https://travis-ci.org/wf4ever/robundle.svg)](https://travis-ci.org/wf4ever/robundle)
[![doi:10.5281/zenodo.10440](https://zenodo.org/badge/doi/10.5281/zenodo.10440.png)](http://dx.doi.org/10.5281/zenodo.10440)

 


API for building Wf4Ever RO bundles. 

See [RO bundle specification](http://purl.org/wf4ever/ro-bundle).

This API is built on the Java 7 NIO Files and uses the 
[Java 7 ZIP file provider](http://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html) to generate the RO Bundle.

The class 
[org.purl.wf4ever.robundle.Bundles](src/main/java/org/purl/wf4ever/robundle/Bundles.java) complements the 
Java 7 [java.nio.Files](http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html) API 
with more specific helper methods to work with RO Bundles.

This API is the basis for the [Taverna Data Bundles API](https://github.com/myGrid/databundles).


Slides
------

[![Slides]()](http://www.slideshare.net/soilandreyes/diving-into-research-objects)

<iframe src="http://www.slideshare.net/slideshow/embed_code/33890214" width="427" height="356" frameborder="0" marginwidth="0" marginheight="0" scrolling="no" style="border:1px solid #CCC; border-width:1px 1px 0; margin-bottom:5px; max-width: 100%;" allowfullscreen> </iframe> <div style="margin-bottom:5px"> <strong> <a href="https://www.slideshare.net/soilandreyes/diving-into-research-objects" title="2014-04-24 A dip into Research Objects" target="_blank">2014-04-24 A dip into Research Objects</a> </strong> from <strong><a href="http://www.slideshare.net/soilandreyes" target="_blank">Stian Soiland-Reyes</a></strong> </div>

Building
--------
```mvn clean install```

should normally work, given a recent version of [Maven 3](http://maven.apache.org/download.cgi) and 
[Java 7 SDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html).

[myGrid's Jenkins installation](http://build.mygrid.org.uk/ci/) has automated builds of
[robundle](http://build.mygrid.org.uk/ci/job/robundle/), which are deployed 
to [myGrid's snapshot Maven repository](http://build.mygrid.org.uk/maven/snapshot-repository/org/purl/wf4ever/robundle/robundle/).



Example of use
--------------

Example in full is at [org.purl.wf4ever.robundle.TestExample](src/test/java/org/purl/wf4ever/robundle/TestExample.java)

```java
        // Create a new (temporary) RO bundle
        Bundle bundle = Bundles.createBundle();

        // Get the inputs
        Path inputs = bundle.getRoot().resolve("inputs");
        Files.createDirectory(inputs);

        // Get an input port:
        Path in1 = inputs.resolve("in1");

        // Setting a string value for the input port:
        Bundles.setStringValue(in1, "Hello");

        // And retrieving it
        if (Bundles.isValue(in1)) {
            System.out.println(Bundles.getStringValue(in1));
        }

        // Or just use the regular Files methods:
        for (String line : Files.readAllLines(in1, Charset.forName("UTF-8"))) {
            System.out.println(line);
        }

        // Binaries and large files are done through the Files API
        try (OutputStream out = Files.newOutputStream(in1,
                StandardOpenOption.APPEND)) {
            out.write(32);
        }
        // Or Java 7 style
        Path localFile = Files.createTempFile("", ".txt");
        Files.copy(in1, localFile, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Written to: " + localFile);

        Files.copy(localFile, bundle.getRoot().resolve("out1"));

        // Representing references
        URI ref = URI.create("http://example.com/external.txt");
        Path out3 = bundle.getRoot().resolve("out3");
        System.out.println(Bundles.setReference(out3, ref));
        if (Bundles.isReference(out3)) {
            URI resolved = Bundles.getReference(out3);
            System.out.println(resolved);
        }

        // Saving a bundle:
        Path zip = Files.createTempFile("bundle", ".zip");
        Bundles.closeAndSaveBundle(bundle, zip);
        // NOTE: From now "bundle" and its Path's are CLOSED
        // and can no longer be accessed

        System.out.println("Saved to " + zip);
        if (Desktop.isDesktopSupported()) {
            // Open ZIP file for browsing
            Desktop.getDesktop().open(zip.toFile());
        }

        // Loading a bundle back from disk
        try (Bundle bundle2 = Bundles.openBundle(zip)) {
            assertEquals(zip, bundle2.getSource());
            
        }       
 ```
