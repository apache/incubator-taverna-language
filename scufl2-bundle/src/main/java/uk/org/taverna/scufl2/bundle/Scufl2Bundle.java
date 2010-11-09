package uk.org.taverna.scufl2.bundle;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Scufl2Bundle {

	private static final String MIMETYPE = "mimetype";
	public static final String MIME_SCUFL2_BUNDLE = "application/vnd.taverna.bundle";
	public static final String MIME_WORKFLOW_BUNDLE = "application/vnd.taverna.workflow-bundle";
	public static final String MIME_DATA_BUNDLE = "application/vnd.taverna.data-bundle";
	public static final String MIME_WORKFLOW_RUN_BUNDLE = "application/vnd.taverna.workflow-run-bundle";
	public static final String MIME_SERVICE_BUNDLE = "application/vnd.taverna.service-bundle";

	private String mimeType = MIME_SCUFL2_BUNDLE;

	private static Charset ASCII = Charset.forName("ascii");

	public Scufl2Bundle() throws Exception {

		// OdfPackage odfPackage = OdfPackage.loadPackage("quick.odt");
		// System.out.println(odfPackage.getFileEntries());
		//
		// for (String odfFileEntry : new ArrayList<String>(
		// odfPackage.getFileEntries())) {
		// if (odfFileEntry.equals("/")) {
		// continue;
		// }
		// odfPackage.remove(odfFileEntry);
		// }
		//
		// odfPackage.insert(URI.create("foo.xml"), "workflows/deadbeef.xml",
		// "application/vnd.taverna.scufl2.workflow+xml");
		// odfPackage
		// .setMediaType("application/vnd.taverna.scufl2.research-object");
		// odfPackage.save("template.scufl2");

	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		if (mimeType == null || !mimeType.contains("/")) {
			throw new IllegalArgumentException("Invalid media type "
					+ mimeType);
		}
		if (!ASCII.newEncoder().canEncode(mimeType)) {
			throw new IllegalArgumentException("Media type must be ASCII: "
					+ mimeType);
		}
		try {
			mimeType.getBytes("ASCII");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mimeType = mimeType;
	}

	public void save(File bundleFile) throws IOException {
		File tempFile = File.createTempFile(bundleFile.getName(), ".tmp",
				bundleFile.getParentFile());
		FileOutputStream fileOut = new FileOutputStream(tempFile);
		ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(
				fileOut));
		addMimeType(zipOut);
		addManifest();
		addFiles();
		zipOut.flush();
		zipOut.close();
		tempFile.renameTo(bundleFile);
	}

	protected void addFiles() {
		// TODO Auto-generated method stub

	}

	protected void addManifest() {
		// TODO Auto-generated method stub

	}

	protected void addMimeType(ZipOutputStream zipOut) throws IOException {
		ZipEntry entry = new ZipEntry(MIMETYPE);
		entry.setMethod(ZipEntry.STORED);
		byte[] mimeType = getMimeType().getBytes("ASCII");
		entry.setSize(mimeType.length);
		CRC32 crc = new CRC32();
		crc.update(mimeType);
		entry.setCrc(crc.getValue());
		zipOut.putNextEntry(entry);
		zipOut.write(mimeType);


	}
}
