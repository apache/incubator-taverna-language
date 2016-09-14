RO bundle API
=============

API for building researchobject.org RO bundles.

Complies with [RO bundle specification](https://w3id.org/bundle) version [1.0](https://w3id.org/bundle/2014-11-05/).

This API is built on the Java NIO Files and uses the
[Java ZIP file provider](http://docs.oracle.com/javase/8/docs/technotes/guides/io/fsp/zipfilesystemprovider.html) to generate the RO Bundle.

The class
[org.apache.taverna.robundle.Bundles](src/main/java/org/apache/taverna/robundle/Bundles.java) complements the
Java [java.nio.Files](http://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html) API
with more specific helper methods to work with RO Bundles.

This API is the basis for the [Taverna Data Bundles API](../taverna-databundle).


Usage
-----

If you use [Maven 3](http://maven.apache.org/), then add to your `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.taverna.language</groupId>
        <artifactId>taverna-robundle</artifactId>
        <version>0.15.1-incubating</version>
    </dependency>
</dependencies>
```

To find the latest `<version>` to use above (this README might
not have been updated), see the
[Apache Taverna Language downloads](http://taverna.incubator.apache.org/download/language/).



Supported bundle formats
------------------------

* [RO bundle specification](https://w3id.org/bundle).
* [Adobe UFC](https://web.archive.org/web/20110625081756/http://learn.adobe.com/wiki/display/PDFNAV/Universal+Container+Format)
* [ePub OCF](http://www.idpf.org/epub3/latest/ocf)
* [Open Document package (ODF)](http://docs.oasis-open.org/office/v1.2/os/OpenDocument-v1.2-os-part3.html#__RefHeading__752807_826425813)
* [COMBINE Archive (OMEX)](http://co.mbine.org/documents/archive)
* [ZIP](http://www.pkware.com/documents/casestudies/APPNOTE.TXT)

The `Bundles` API will load a bundle in any of the formats above, converging
them to a [Research Object Bundle](https://w3id.org/bundle),
while still maintaining the manifests of the other formats,
if they exist within the bundle.

Thus, if you open say a [COMBINE Archive](http://co.mbine.org/documents/archive) and add a couple of resources,
indicating their mediatype using `bundle.getManifest().getAggregation(path).setMediaType("a/b")`, then
when closing this bundle, the API will generate both an RO Bundle manifest and a COMBINE manifest
that reflect this.




Example of use
--------------

Example in full is at [org.apache.taverna.robundle.TestExample](src/test/java/org/apache/taverna/robundle/TestExample.java)

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

		// Loading a bundle back from disk
		try (Bundle bundle2 = Bundles.openBundle(zip)) {
			assertEquals(zip, bundle2.getSource());
		}
 ```
