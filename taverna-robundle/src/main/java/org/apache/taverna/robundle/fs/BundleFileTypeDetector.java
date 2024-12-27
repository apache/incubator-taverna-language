package org.apache.taverna.robundle.fs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.spi.FileTypeDetector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipError;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

public class BundleFileTypeDetector extends FileTypeDetector {

	private static final String APPLICATION_ZIP = "application/zip";
	private static final Charset UTF_8 = Charset.forName("UTF-8");
	private static final String MIMETYPE = "mimetype";
	private static final String ZIP_MAGIC_NUMBER = "PK";

	@Override
	public String probeContentType(Path path) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(256);

		try (SeekableByteChannel byteChannel = Files.newByteChannel(path, StandardOpenOption.READ)) {
			int read = byteChannel.read(buf);
			if (read < 38) {
				return null;
			}
		}

		buf.flip();
		byte[] firstBytes = buf.array();

		// Look for ZIP magic number ("PK")
		String pk = new String(firstBytes, 0, 2, UTF_8);
		if (!pk.equals(ZIP_MAGIC_NUMBER) || firstBytes[2] != 3 || firstBytes[3] != 4) {
			// Not a ZIP file
			return null;
		}

		String mimetype = new String(firstBytes, 30, 8, UTF_8);
		if (!mimetype.equals(MIMETYPE)) {
			return APPLICATION_ZIP;
		}

		// Read the 'mimetype' file from the ZIP
		try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(firstBytes))) {
			ZipEntry entry = zipStream.getNextEntry();
			if (entry == null || !MIMETYPE.equals(entry.getName())) {
				return APPLICATION_ZIP;
			}

			byte[] mediaTypeBuffer = new byte[256];
			int size = zipStream.read(mediaTypeBuffer);
			if (size < 1) {
				return APPLICATION_ZIP;
			}
			return new String(mediaTypeBuffer, 0, size, UTF_8);
		} catch (ZipException | ZipError e) {
			// Log the error (optional)
			return null;
		}
	}
}
