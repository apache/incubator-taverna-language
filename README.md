RO bundle API
=============

API for building Wf4Ever RO bundles. 

Work in progress.

See [RO bundle specification](http://purl.org/wf4ever/ro-bundle).

This API is built on the Java 7 NIO Files and uses the 
[Java 7 ZIP file provider](http://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html) to generate the RO Bundle.

The class 
[org.purl.wf4ever.robundle.Bundle](src/main/java/org/purl/wf4ever/robundle/Bundle.java) complements the 
Java 7 [java.nio.Files](http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html) API 
with more specific helper methods to work with RO Bundles.

This API is the basis for the [Taverna Data Bundles API](https://github.com/myGrid/databundles).



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