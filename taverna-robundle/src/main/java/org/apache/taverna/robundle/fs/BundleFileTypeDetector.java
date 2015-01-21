package org.apache.taverna.robundle.fs;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


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
	private static final Charset ASCII = Charset.forName("ASCII");
	private static final Charset LATIN1 = Charset.forName("ISO-8859-1");
	private static final String MIMETYPE = "mimetype";

	@Override
	public String probeContentType(Path path) throws IOException {

		ByteBuffer buf = ByteBuffer.allocate(256);
		try (SeekableByteChannel byteChannel = Files.newByteChannel(path,
				StandardOpenOption.READ)) {
			int read = byteChannel.read(buf);
			if (read < 38) {
				return null;
			}
			;
		}
		buf.flip();

		// Look for PK

		byte[] firstBytes = buf.array();
		String pk = new String(firstBytes, 0, 2, LATIN1);
		if (!(pk.equals("PK") && firstBytes[2] == 3 && firstBytes[3] == 4)) {
			// Did not match magic numbers of ZIP as specified in ePub OCF
			// http://www.idpf.org/epub/30/spec/epub30-ocf.html#app-media-type
			return null;
		}

		String mimetype = new String(firstBytes, 30, 8, LATIN1);
		if (!mimetype.equals(MIMETYPE)) {
			return APPLICATION_ZIP;
		}
		// Read the 'mimetype' file.
		try (ZipInputStream is = new ZipInputStream(new ByteArrayInputStream(
				firstBytes))) {
			ZipEntry entry = is.getNextEntry();
			if (!MIMETYPE.equals(entry.getName())) {
				return APPLICATION_ZIP;
			}
			byte[] mediaTypeBuffer = new byte[256];
			int size = is.read(mediaTypeBuffer);
			if (size < 1) {
				return APPLICATION_ZIP;
			}
			return new String(mediaTypeBuffer, 0, size, ASCII);
		} catch (ZipException | ZipError e) {
			return null;
		}
	}

}
