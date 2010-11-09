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

import uk.org.taverna.scufl2.bundle.impl.odfdom.pkg.OdfPackage;

public class Scufl2Bundle {

	private static final String MIMETYPE = "mimetype";
	public static final String MIME_SCUFL2_BUNDLE = "application/vnd.taverna.bundle";
	public static final String MIME_WORKFLOW_BUNDLE = "application/vnd.taverna.workflow-bundle";
	public static final String MIME_DATA_BUNDLE = "application/vnd.taverna.data-bundle";
	public static final String MIME_WORKFLOW_RUN_BUNDLE = "application/vnd.taverna.workflow-run-bundle";
	public static final String MIME_SERVICE_BUNDLE = "application/vnd.taverna.service-bundle";

	private static Charset ASCII = Charset.forName("ascii");
	private final OdfPackage odfPackage;

	public Scufl2Bundle() throws Exception {
		odfPackage = OdfPackage.create();
		odfPackage.setMediaType(MIME_SCUFL2_BUNDLE);
	}

	public String getMimeType() {
		return odfPackage.getMediaType();
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
		odfPackage.setMediaType(mimeType);
	}

	public void save(File bundleFile) throws IOException {
		File tempFile = File.createTempFile(bundleFile.getName(), ".tmp",
				bundleFile.getParentFile());

		try {
			odfPackage.setMediaType(getMimeType());
			odfPackage.save(tempFile);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("Could not save bundle to " + bundleFile, e);
		}
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

	public void insert(String path, String mimeType, String stringValue) {

	}
}
