package org.apache.taverna.scufl2.api.io;

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


import static org.apache.taverna.scufl2.api.io.structure.StructureReader.TEXT_VND_TAVERNA_SCUFL2_STRUCTURE;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.taverna.scufl2.api.ExampleWorkflow;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WorkflowBundleReader;
import org.apache.taverna.scufl2.api.io.WorkflowBundleWriter;
import org.apache.taverna.scufl2.api.io.structure.StructureReader;
import org.apache.taverna.scufl2.api.io.structure.StructureWriter;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.junit.Ignore;
import org.junit.Test;


public class TestWorkflowBundleIO extends ExampleWorkflow {

	private static final String UTF_8 = "utf-8";
	protected WorkflowBundleIO bundleIO = new WorkflowBundleIO();
	protected WorkflowBundle wfBundle = makeWorkflowBundle();

	@Test
	public void createBundle() throws Exception {
		WorkflowBundle wb = bundleIO.createBundle();
		assertEquals(wb, wb.getMainWorkflow().getParent());
		assertEquals(wb, wb.getMainProfile().getParent());
		assertEquals("bundle1", wb.getName());
		assertEquals("workflow1", wb.getMainWorkflow().getName());
		assertEquals("profile1", wb.getMainProfile().getName());
		assertNotNull(wb.getCurrentRevision());
		assertNotNull(wb.getMainWorkflow().getCurrentRevision());
		assertNotNull(wb.getMainProfile().getCurrentRevision());
	}
	
	@Test
	public void getReaderForMediaType() throws Exception {
		WorkflowBundleReader Reader = bundleIO
		.getReaderForMediaType(TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
		assertTrue(Reader instanceof StructureReader);
	}

	@Test
	public void getReaderForUnknownMediaType() throws Exception {
		assertNull(bundleIO
				.getReaderForMediaType("application/vnd.example.unknownStuff"));
	}

	public String getStructureFormatWorkflowBundle() throws IOException {
		InputStream helloWorldStream = getClass().getResourceAsStream(
				"HelloWorld.txt");
		return IOUtils.toString(helloWorldStream);
	}

	@Test
	public void getWorkflowBundleReaders() throws Exception {
		assertEquals(1, bundleIO.getReaders().size());
		WorkflowBundleReader Reader = bundleIO.getReaders().get(0);
		assertTrue(Reader instanceof StructureReader);
	}

	@Test
	public void getWorkflowBundleWriters() throws Exception {
		assertEquals(1, bundleIO.getWriters().size());
		WorkflowBundleWriter writer = bundleIO.getWriters().get(0);
		assertTrue(writer instanceof StructureWriter);
	}

	
	@Test
	public void getWriterForMediaType() throws Exception {
		WorkflowBundleWriter writer = bundleIO
		.getWriterForMediaType(TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
		assertTrue(writer instanceof StructureWriter);
	}

	@Test
	public void getWriterForUnknownMediaType() throws Exception {
		assertNull(bundleIO
				.getWriterForMediaType("application/vnd.example.unknownStuff"));
	}
	
	@Test
	public void guessMediaType() {
		WorkflowBundleReader myReader = new WorkflowBundleReader() {
			@Override
			public Set<String> getMediaTypes() {
				return null;
			}
			@Override
			public WorkflowBundle readBundle(File bundleFile, String mediaType) {
				return null;
			}
			@Override
			public WorkflowBundle readBundle(InputStream inputStream,
					String mediaType) {
				return null;
			}
			@Override
			public String guessMediaTypeForSignature(byte[] firstBytes) {
				if (firstBytes.length == 4) {
					return "test/test";
				}
				return null;
			}
		};

		WorkflowBundleReader otherReader = new WorkflowBundleReader() {
			@Override
			public Set<String> getMediaTypes() {
				return null;
			}
			@Override
			public WorkflowBundle readBundle(File bundleFile, String mediaType) {
				return null;
			}
			@Override
			public WorkflowBundle readBundle(InputStream inputStream,
					String mediaType) {
				return null;
			}
			@Override
			public String guessMediaTypeForSignature(byte[] firstBytes) {
				if (firstBytes.length == 4) {
					return "test/other";
				}
				return null;
			}
		};

		
		bundleIO.setReaders(Arrays.asList(myReader));
		assertEquals(null, bundleIO.guessMediaTypeForSignature(new byte[16]));
		assertEquals("test/test", bundleIO.guessMediaTypeForSignature(new byte[4]));


		bundleIO.setReaders(Arrays.asList(myReader, myReader));
		// 4 bytes should not be ambiguous, they all agree
		assertEquals("test/test", bundleIO.guessMediaTypeForSignature(new byte[4]));		
		
		bundleIO.setReaders(Arrays.asList(myReader, myReader, otherReader));
		// 4 bytes should now be ambiguous
		assertEquals(null, bundleIO.guessMediaTypeForSignature(new byte[4]));		
	}


	@Test
	public void readBundleFile() throws Exception {
		File bundleFile = tempFile();
		FileUtils.writeStringToFile(bundleFile,
				getStructureFormatWorkflowBundle(),
				UTF_8);
		WorkflowBundle wfBundle = bundleIO.readBundle(bundleFile,
				TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
		assertEquals("HelloWorld", wfBundle.getName());
		assertEquals("HelloWorld", wfBundle.getMainWorkflow().getName());
		assertTrue(wfBundle.getMainWorkflow().getProcessors()
				.containsName("Hello"));
	}

	

	@Test
	public void readBundleFileNoMediaType() throws Exception {
		File bundleFile = tempFile();
		FileUtils.writeStringToFile(bundleFile,
				getStructureFormatWorkflowBundle(),
				UTF_8);
		WorkflowBundle wfBundle = bundleIO.readBundle(bundleFile,null);
		assertNotNull(wfBundle);

		File emptyFile = File.createTempFile("test", "txt");
		try {
			@SuppressWarnings("unused")
			WorkflowBundle none = bundleIO.readBundle(emptyFile,null);
			fail("Should throw IllegalArgumentException for unrecognized file");
		} catch (IllegalArgumentException ex) {
		}
	}

	@Test
	public void readBundleStream() throws Exception {
		InputStream inputStream = new ByteArrayInputStream(
				getStructureFormatWorkflowBundle().getBytes("utf-8"));
		WorkflowBundle wfBundle = bundleIO.readBundle(inputStream,
				TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
		assertEquals("HelloWorld", wfBundle.getName());
		assertEquals("HelloWorld", wfBundle.getMainWorkflow().getName());
		assertTrue(wfBundle.getMainWorkflow().getProcessors()
				.containsName("Hello"));
	}
	
	@Test
	public void readBundleStreamNoMediaType() throws Exception {
		InputStream inputStream = new ByteArrayInputStream(
				getStructureFormatWorkflowBundle().getBytes("utf-8"));
		WorkflowBundle wfBundle = bundleIO.readBundle(inputStream, null);
		assertNotNull(wfBundle);
		assertEquals("HelloWorld", wfBundle.getName());

	}


	@Test
	public void readToWriteRoundTrip() throws Exception {
		InputStream inputStream = new ByteArrayInputStream(
				getStructureFormatWorkflowBundle().getBytes("utf-8"));
		WorkflowBundle readBundle = bundleIO.readBundle(inputStream,
				TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bundleIO.writeBundle(readBundle, output, TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
		String bundleTxt = new String(output.toByteArray(), UTF_8);
                String getStructureFormatWorkflowBundle = getStructureFormatWorkflowBundle();
                bundleTxt = bundleTxt.replaceAll("\r", "").replaceAll("\n", "");
                getStructureFormatWorkflowBundle = getStructureFormatWorkflowBundle.replaceAll("\r", "").replaceAll("\n", "");
		assertEquals(getStructureFormatWorkflowBundle, bundleTxt);
	}

	@Test
	public void setReaders() {
		WorkflowBundleReader myReader = new WorkflowBundleReader() {
			@Override
			public Set<String> getMediaTypes() {
				return Collections.singleton("application/vnd.example.myOwn");
			}
			@Override
			public WorkflowBundle readBundle(File bundleFile, String mediaType) {
				return null;
			}
			@Override
			public WorkflowBundle readBundle(InputStream inputStream,
					String mediaType) {
				return null;
			}
			@Override
			public String guessMediaTypeForSignature(byte[] firstBytes) {
				return "test/test";
			}
		};

		bundleIO.setReaders(Collections.singletonList(myReader));
		assertEquals(1, bundleIO.getReaders().size());
		assertSame(myReader, bundleIO.getReaders().get(0));
		assertSame(myReader,
				bundleIO.getReaderForMediaType("application/vnd.example.myOwn"));

		// Should now be null
		assertNull(bundleIO
				.getReaderForMediaType(TEXT_VND_TAVERNA_SCUFL2_STRUCTURE));		
		assertEquals("test/test", bundleIO.guessMediaTypeForSignature(new byte[4]));
	}
	
	

	@Test
	public void setWriters() {
		WorkflowBundleWriter myWriter = new WorkflowBundleWriter() {
			@Override
			public Set<String> getMediaTypes() {
				return Collections.singleton("application/vnd.example.myOwn");
			}
			@Override
			public void writeBundle(WorkflowBundle wfBundle, File destination,
					String mediaType) {
			}
			@Override
			public void writeBundle(WorkflowBundle wfBundle,
					OutputStream output, String mediaType) {
			}
		};

		bundleIO.setWriters(Collections.singletonList(myWriter));
		assertEquals(1, bundleIO.getWriters().size());
		assertSame(myWriter, bundleIO.getWriters().get(0));
		assertSame(myWriter,
				bundleIO.getWriterForMediaType("application/vnd.example.myOwn"));

		// Should now be null
		assertNull(bundleIO
				.getWriterForMediaType(TEXT_VND_TAVERNA_SCUFL2_STRUCTURE));
	}

	public File tempFile() throws IOException {
		File bundleFile = File.createTempFile("scufl2", "txt");
		bundleFile.deleteOnExit();
		return bundleFile;
	}

	@Test
	public void writeBundleFile() throws Exception {
		File bundleFile = tempFile();
		bundleIO.writeBundle(wfBundle, bundleFile,
				TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
		String bundleTxt = FileUtils.readFileToString(bundleFile, UTF_8);
                String getStructureFormatWorkflowBundle = getStructureFormatWorkflowBundle();
                bundleTxt = bundleTxt.replaceAll("\r", "").replaceAll("\n", "");
                getStructureFormatWorkflowBundle = getStructureFormatWorkflowBundle.replaceAll("\r", "").replaceAll("\n", "");
		assertEquals(getStructureFormatWorkflowBundle, bundleTxt);
	}

	@Ignore
	@Test
	public void writeBundleFileSetParents() throws Exception {
		File bundleFile = tempFile();
		// Deliberately orphan a profile and a processor
		Profile profile = wfBundle.getProfiles().getByName("tavernaWorkbench");
		profile.setParent(null);		
		wfBundle.getProfiles().add(profile);		
		processor.setParent(null);
		workflow.getProcessors().add(processor);		
		
		assertNull(processor.getParent());
		assertNull(profile.getParent());		
		bundleIO.writeBundle(wfBundle, bundleFile,
				TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
		assertNotNull(processor.getParent());
		assertNotNull(profile.getParent());				
		String bundleTxt = FileUtils.readFileToString(bundleFile, UTF_8);
		assertTrue(bundleTxt.contains("Processor 'Hello'"));
		assertTrue(bundleTxt.contains("Profile 'tavernaWorkbench'"));		
	}
	
	@Test
	public void writeBundleStream() throws Exception {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bundleIO.writeBundle(wfBundle, output, TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
		String bundleTxt = new String(output.toByteArray(), UTF_8);
                String getStructureFormatWorkflowBundle = getStructureFormatWorkflowBundle();
                bundleTxt = bundleTxt.replaceAll("\r", "").replaceAll("\n", "");
                getStructureFormatWorkflowBundle = getStructureFormatWorkflowBundle.replaceAll("\r", "").replaceAll("\n", "");
		assertEquals(getStructureFormatWorkflowBundle, bundleTxt);
	}

	@Test(expected = IllegalArgumentException.class)
	public void writeBundleUnknownMediaType() throws Exception {
		File bundleFile = tempFile();
		bundleIO.writeBundle(wfBundle, bundleFile,
		"application/vnd.example.unknownStuff");
	}

	@Test(expected = IOException.class)
	public void writeBundleWrongLocation() throws Exception {
		File bundleDir = tempFile();
		bundleDir.delete();
		File bundleFile = new File(bundleDir, "nonExistingDir");
		bundleIO.writeBundle(wfBundle, bundleFile,
				TEXT_VND_TAVERNA_SCUFL2_STRUCTURE);
	}

}
