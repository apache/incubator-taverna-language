Data bundles API
================

API for Taverna Data Bundles

Work in progress.

See [Data bundle requirements](http://dev.mygrid.org.uk/wiki/display/TAVOSGI/2013-02+Data+bundle+requirements)
and [TestDataBundles.java](src/test/java/uk/org/taverna/databundle/TestDataBundles.java)

This API is built on the Java 7 NIO Files and uses the [Java 7 ZIP file provider](http://docs.oracle.com/javase/7/docs/technotes/guides/io/fsp/zipfilesystemprovider.html) to generate the Data Bundle.

The class [uk.org.taverna.databundle.DataBundle](src/main/java/uk/org/taverna/databundle/DataBundle.java) complements the Java 7 [java.nio.Files](http://docs.oracle.com/javase/7/docs/api/java/nio/file/Files.html) API with more specific helper methods to work with Data Bundles.


Example of use
--------------

Example in full is at [uk.org.taverna.databundle.TestExample](src/test/java/uk/org/taverna/databundle/TestExample.java)

```java
        // Create a new (temporary) data bundle
        DataBundle dataBundle = DataBundles.createDataBundle();

        // Get the inputs
        Path inputs = DataBundles.getInputs(dataBundle);

        // Get an input port:
        Path portIn1 = DataBundles.getPort(inputs, "in1");

        // Setting a string value for the input port:
        DataBundles.setStringValue(portIn1, "Hello");

        // And retrieving it
        if (DataBundles.isValue(portIn1)) {
            System.out.println(DataBundles.getStringValue(portIn1));
        }

        // Or just use the regular Files methods:
        for (String line : Files
                .readAllLines(portIn1, Charset.forName("UTF-8"))) {
            System.out.println(line);
        }

        // Binaries and large files are done through the Files API
        try (OutputStream out = Files.newOutputStream(portIn1,
                StandardOpenOption.APPEND)) {
            out.write(32);
        }
        // Or Java 7 style
        Path localFile = Files.createTempFile("", ".txt");
        Files.copy(portIn1, localFile, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Written to: " + localFile);

        // Either way works, of course
        Files.copy(localFile,
                DataBundles.getPort(DataBundles.getOutputs(dataBundle), "out1"));


        // When you get a port, it can become either a value or a list
        Path port2 = DataBundles.getPort(inputs, "port2");
        DataBundles.createList(port2); // empty list
        List<Path> list = DataBundles.getList(port2);
        assertTrue(list.isEmpty());

        // Adding items sequentially
        Path item0 = DataBundles.newListItem(port2);
        DataBundles.setStringValue(item0, "item 0");
        DataBundles.setStringValue(DataBundles.newListItem(port2), "item 1");
        DataBundles.setStringValue(DataBundles.newListItem(port2), "item 2");

        
        // Set by explicit position:
        // TODO: Add convenience method with over-write to DataBundles
        DataBundles.setStringValue(port2.resolve("12"), "item 12");
        
        
        // The list is sorted numerically (e.g. 2, 5, 10) and
        // will contain nulls for empty slots
        System.out.println(DataBundles.getList(port2));

        // Ports can be browsed as a map by port name
        NavigableMap<String, Path> ports = DataBundles.getPorts(inputs);
        System.out.println(ports.keySet());
    
        // Saving a data bundle:
        Path zip = Files.createTempFile("databundle", "zip");
        DataBundles.closeAndSaveDataBundle(dataBundle, zip);
        // NOTE: From now dataBundle and its Path's are CLOSED 
        // and can no longer be accessed
        
        
        System.out.println("Saved to " + zip);
        if (Desktop.isDesktopSupported()) {
            // Open ZIP file for browsing
            Desktop.getDesktop().open(zip.toFile());
        }
        
        // Loading a data bundle back from disk
        try (DataBundle dataBundle2 = DataBundles.openDataBundle(zip)) {
            // Any modifications here will be saved on (here automatic) close
            
        }     
```