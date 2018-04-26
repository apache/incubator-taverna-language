package org.apache.taverna.databundle;
/*
 *
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
 *
*/


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.taverna.databundle.DataBundles.ResolveOptions;
import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

//import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class TestDataBundles {
	private Bundle dataBundle;

    protected void checkSignature(Path zip) throws IOException {
		String MEDIATYPE = "application/vnd.wf4ever.robundle+zip";
		/*
		 * Check position 30++ according to RO Bundle specification
		 * http://purl.org/wf4ever/ro-bundle#ucf
		 */
		byte[] expected = ("mimetype" + MEDIATYPE + "PK").getBytes("ASCII");

		try (InputStream in = Files.newInputStream(zip)) {
			byte[] signature = new byte[expected.length];
			int MIME_OFFSET = 30;
			assertEquals(MIME_OFFSET, in.skip(MIME_OFFSET));
			assertEquals(expected.length, in.read(signature));
			assertArrayEquals(expected, signature);
		}
	}
	
	@Before
	public void createDataBundle() throws IOException {
	    dataBundle = DataBundles.createBundle();
	}
	
	@After
	public void closeDataBundle() throws IOException {
	    dataBundle.close();
	}
	
	@Test
    public void clear() throws Exception {        
        Path inputs = DataBundles.getInputs(dataBundle);
        Path file1 = inputs.resolve("file1");
        Path file1Txt = inputs.resolve("file1.txt");
        Path file1Png = inputs.resolve("file1.png");
        Path file1Else = inputs.resolve("file1somethingelse.txt");
        
        Files.createFile(file1);
        Files.createFile(file1Txt);
        Files.createFile(file1Png);
        Files.createFile(file1Else); 
        
        DataBundles.deleteAllExtensions(file1);
        
        assertFalse(Files.exists(file1));
        assertFalse(Files.exists(file1Txt));
        assertFalse(Files.exists(file1Png));
        assertTrue(Files.exists(file1Else));
	}

	   
    @Test
    public void clearRecursive() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path file1 = inputs.resolve("file1");
        Path file1Dir = inputs.resolve("file1.dir");
        
        Files.createDirectory(file1);
        Files.createDirectory(file1Dir);
        Path nested = file1Dir.resolve("nested");
        Files.createDirectory(nested);
        
        
        Path filePng = file1Dir.resolve("file.png");
        Path fileTxt = nested.resolve("file1somethingelse.txt");
        
        Files.createFile(filePng);
        Files.createFile(fileTxt); 
        
        DataBundles.deleteAllExtensions(file1);
        
        assertFalse(Files.exists(file1));
        assertFalse(Files.exists(nested));
        assertFalse(Files.exists(file1Dir));
        assertFalse(Files.exists(filePng));
        assertFalse(Files.exists(fileTxt));
    }

	
	@Test
	public void createList() throws Exception {
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		assertTrue(Files.isDirectory(list));
	}
	
	@Test
	public void getError() throws Exception {	
		Path inputs = DataBundles.getInputs(dataBundle);
		Path portIn1 = DataBundles.getPort(inputs, "in1");
		DataBundles.setError(portIn1, "Something did not work", "A very\n long\n error\n trace");		
		
		ErrorDocument error = DataBundles.getError(portIn1);
		assertTrue(error.getCausedBy().isEmpty());
		
		assertEquals("Something did not work", error.getMessage());
		// Notice that the lack of trailing \n is preserved 
		assertEquals("A very\n long\n error\n trace", error.getTrace());	
		
		assertEquals(null, DataBundles.getError(null));
	}

	@Test
	public void getErrorCause() throws Exception {		
		Path inputs = DataBundles.getInputs(dataBundle);
		Path portIn1 = DataBundles.getPort(inputs, "in1");
		Path cause1 = DataBundles.setError(portIn1, "Something did not work", "A very\n long\n error\n trace");
		Path portIn2 = DataBundles.getPort(inputs, "in2");
		Path cause2 = DataBundles.setError(portIn2, "Something else did not work", "Shorter trace");
		
		
		Path outputs = DataBundles.getOutputs(dataBundle);
		Path portOut1 = DataBundles.getPort(outputs, "out1");
		DataBundles.setError(portOut1, "Errors in input", "", cause1, cause2);

		ErrorDocument error = DataBundles.getError(portOut1);
		assertEquals("Errors in input", error.getMessage());
		assertEquals("", error.getTrace());
		assertEquals(2, error.getCausedBy().size());
		
		assertTrue(Files.isSameFile(cause1, error.getCausedBy().get(0)));
		assertTrue(Files.isSameFile(cause2, error.getCausedBy().get(1)));
	}

	@Test
	public void getInputs() throws Exception {
		Path inputs = DataBundles.getInputs(dataBundle);
		assertTrue(Files.isDirectory(inputs));
		// Second time should not fail because it alreadresolvy exists
		inputs = DataBundles.getInputs(dataBundle);
		assertTrue(Files.isDirectory(inputs));
		assertEquals(dataBundle.getRoot(), inputs.getParent());
	}

	@Test
	public void getList() throws Exception {
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		for (int i = 0; i < 5; i++) {
			Path item = DataBundles.newListItem(list);
			DataBundles.setStringValue(item, "test" + i);
		}
		List<Path> paths = DataBundles.getList(list);
		assertEquals(5, paths.size());
		assertEquals("test0", DataBundles.getStringValue(paths.get(0)));
		assertEquals("test4", DataBundles.getStringValue(paths.get(4)));
		
		assertEquals(null, DataBundles.getList(null));
	}

	@Test
	public void getListItem() throws Exception {
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		for (int i = 0; i < 5; i++) {
			Path item = DataBundles.newListItem(list);
			DataBundles.setStringValue(item, "item " + i);
		}
		// set at next available position
		Path item5 = DataBundles.getListItem(list, 5);
		assertTrue(item5.getFileName().toString().contains("5"));
		DataBundles.setStringValue(item5, "item 5");
	
		
		// set somewhere later
		Path item8 = DataBundles.getListItem(list, 8);
		assertTrue(item8.getFileName().toString().contains("8"));
		DataBundles.setStringValue(item8, "item 8");
		
		Path item7 = DataBundles.getListItem(list, 7);
		assertFalse(Files.exists(item7));
		assertFalse(DataBundles.isList(item7));
		assertFalse(DataBundles.isError(item7));
		assertFalse(DataBundles.isValue(item7));
		// TODO: Is it really missing? item1337 is also missing..
		assertTrue(DataBundles.isMissing(item7));
		
		
		// overwrite #2
		Path item2 = DataBundles.getListItem(list, 2);		
		DataBundles.setStringValue(item2, "replaced");
		
		
		List<Path> listItems = DataBundles.getList(list);
		assertEquals(9, listItems.size());
		assertEquals("item 0", DataBundles.getStringValue(listItems.get(0)));
		assertEquals("item 1", DataBundles.getStringValue(listItems.get(1)));
		assertEquals("replaced", DataBundles.getStringValue(listItems.get(2)));
		assertEquals("item 3", DataBundles.getStringValue(listItems.get(3)));
		assertEquals("item 4", DataBundles.getStringValue(listItems.get(4)));
		assertEquals("item 5", DataBundles.getStringValue(listItems.get(5)));
		assertNull(listItems.get(6));
		assertNull(listItems.get(7));
		assertEquals("item 8", DataBundles.getStringValue(listItems.get(8)));
		
	}

    @Test
    public void getListSize() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path list = DataBundles.getPort(inputs, "in1");
        DataBundles.createList(list);
        for (int i = 0; i < 5; i++) {
            Path item = DataBundles.newListItem(list);
            DataBundles.setStringValue(item, "item " + i);
        }
        assertEquals(5, DataBundles.getListSize(list));

        // set at next available position
        Path item5 = DataBundles.getListItem(list, 5);
        assertTrue(item5.getFileName().toString().contains("5"));
        DataBundles.setStringValue(item5, "item 5");
        assertEquals(6, DataBundles.getListSize(list));

        // set somewhere beyond the end
        Path item8 = DataBundles.getListItem(list, 8);
        assertTrue(item8.getFileName().toString().contains("8"));
        DataBundles.setStringValue(item8, "item 8");
        assertEquals(9, DataBundles.getListSize(list));
        
        // Evil test - very high number
        long highNumber = 3l * Integer.MAX_VALUE;
        Path itemHigh = DataBundles.getListItem(list, highNumber);
        assertTrue(itemHigh.getFileName().toString().contains(Long.toString(highNumber)));
        DataBundles.setStringValue(itemHigh, "item 6442450941");
        assertEquals(highNumber+1l, DataBundles.getListSize(list));
    }
	
	@Test
    public void getListItemChecksExtension() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path portIn1 = DataBundles.getPort(inputs, "in1");
        Path list = DataBundles.newListItem(portIn1);
        
        Path item = DataBundles.newListItem(list);
        
        Path ref = DataBundles.setReference(item, URI.create("http://example.com/"));
        Path itemAgain = DataBundles.getListItem(list, 0);
        assertEquals(ref, itemAgain);
        assertFalse(itemAgain.equals(portIn1));
        assertTrue(Files.exists(itemAgain));
    }

	@Test
	public void getOutputs() throws Exception {
		Path outputs = DataBundles.getOutputs(dataBundle);
		assertTrue(Files.isDirectory(outputs));
		// Second time should not fail because it already exists
		outputs = DataBundles.getOutputs(dataBundle);
		assertTrue(Files.isDirectory(outputs));
		assertEquals(dataBundle.getRoot(), outputs.getParent());
	}
	
	@Test
	public void getPort() throws Exception {
		Path inputs = DataBundles.getInputs(dataBundle);
		Path portIn1 = DataBundles.getPort(inputs, "in1");
		assertFalse(Files.exists(portIn1));
		assertEquals(inputs, portIn1.getParent());
	}

	@Test
    public void getPortChecksExtension() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path portIn1 = DataBundles.getPort(inputs, "in1");
        assertFalse(Files.exists(portIn1));
        Path ref = DataBundles.setReference(portIn1, URI.create("http://example.com/"));
        Path portIn1Again = DataBundles.getPort(inputs, "in1");
        assertEquals(ref, portIn1Again);
        assertFalse(portIn1Again.equals(portIn1));
        assertTrue(Files.exists(portIn1Again));
    }

	@Test
	public void getPorts() throws Exception {
		Path inputs = DataBundles.getInputs(dataBundle);
		DataBundles.createList(DataBundles.getPort(inputs, "in1"));
		DataBundles.createList(DataBundles.getPort(inputs, "in2"));
		DataBundles.setStringValue(DataBundles.getPort(inputs, "value"),
				"A value");
		Map<String, Path> ports = DataBundles.getPorts(DataBundles
				.getInputs(dataBundle));
		assertEquals(3, ports.size());
//		System.out.println(ports);
		assertTrue(ports.containsKey("in1"));
		assertTrue(ports.containsKey("in2"));
		assertTrue(ports.containsKey("value"));

		assertEquals("A value", DataBundles.getStringValue(ports.get("value")));

	}

	@Test
	public void hasInputs() throws Exception {
		assertFalse(DataBundles.hasInputs(dataBundle));
		DataBundles.getInputs(dataBundle); // create on demand
		assertTrue(DataBundles.hasInputs(dataBundle));
	}

	@Test
	public void hasOutputs() throws Exception {
		assertFalse(DataBundles.hasOutputs(dataBundle));
		DataBundles.getInputs(dataBundle); // independent
		assertFalse(DataBundles.hasOutputs(dataBundle));
		DataBundles.getOutputs(dataBundle); // create on demand
		assertTrue(DataBundles.hasOutputs(dataBundle));
	}

	@Test
	public void isError() throws Exception {
		Path inputs = DataBundles.getInputs(dataBundle);
		Path portIn1 = DataBundles.getPort(inputs, "in1");
		DataBundles.setError(portIn1, "Something did not work", "A very\n long\n error\n trace");		
		
		assertFalse(DataBundles.isList(portIn1));		
		assertFalse(DataBundles.isValue(portIn1));
		assertFalse(DataBundles.isMissing(portIn1));
		assertFalse(DataBundles.isReference(portIn1));
		assertTrue(DataBundles.isError(portIn1));		
	}

	@Test
	public void isList() throws Exception {
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		assertTrue(DataBundles.isList(list));
		assertFalse(DataBundles.isValue(list));
		assertFalse(DataBundles.isError(list));
		assertFalse(DataBundles.isReference(list));
		assertFalse(DataBundles.isMissing(list));
	}
	
	@Test
	public void isMissing() throws Exception {
		Path inputs = DataBundles.getInputs(dataBundle);
		Path portIn1 = DataBundles.getPort(inputs, "in1");
		
		assertFalse(DataBundles.isList(portIn1));		
		assertFalse(DataBundles.isValue(portIn1));
		assertFalse(DataBundles.isError(portIn1));
		assertTrue(DataBundles.isMissing(portIn1));
		assertFalse(DataBundles.isReference(portIn1));
	}

	@Test
    public void isValueOnError() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        DataBundles.setError(DataBundles.getPort(inputs, "test"),
                "error", "");
        assertFalse(DataBundles.isValue(DataBundles.getPorts(inputs).get("test")));
    }
	
	@Test
    public void isValueOnReference() throws Exception {
	    Path inputs = DataBundles.getInputs(dataBundle);
	    DataBundles.setReference(DataBundles.getPort(inputs, "test"), URI.create("http://www.example.com/"));
	    assertFalse(DataBundles.isValue(DataBundles.getPorts(inputs).get("test")));
    }
	
	@Test
	public void listOfLists() throws Exception {
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		Path sublist0 = DataBundles.newListItem(list);
		DataBundles.createList(sublist0);
		
		Path sublist1 = DataBundles.newListItem(list);
		DataBundles.createList(sublist1);
		
		assertEquals(Arrays.asList("0", "1"), ls(list));
		
		DataBundles.setStringValue(DataBundles.newListItem(sublist1), 
				"Hello");
		
		assertEquals(Arrays.asList("0"), ls(sublist1));
		
		assertEquals("Hello",DataBundles.getStringValue( 
				DataBundles.getListItem(DataBundles.getListItem(list, 1), 0)));
	}

	
	
	protected List<String> ls(Path path) throws IOException {
		List<String> paths = new ArrayList<>();
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
			for (Path p : ds) {
				paths.add(p.getFileName() + "");
			}
		}
		Collections.sort(paths);
		return paths;
	}
	
	@Test(expected=FileAlreadyExistsException.class)
    public void newListAlreadyExistsAsError() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path list = DataBundles.getPort(inputs, "in1");
        Path err = DataBundles.setError(list, "a", "b");
        assertFalse(Files.isRegularFile(list));
        assertFalse(Files.isDirectory(list));
        assertTrue(Files.isRegularFile(err));                
        DataBundles.createList(list);
    }
	
	@Test(expected=FileAlreadyExistsException.class)
    public void newListAlreadyExistsAsFile() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path list = DataBundles.getPort(inputs, "in1");
        DataBundles.setStringValue(list, "A string");
        assertTrue(Files.isRegularFile(list));
        assertFalse(Files.isDirectory(list));
        DataBundles.createList(list);
    }
	

    @Test(expected=FileAlreadyExistsException.class)
    public void newListAlreadyExistsAsReference() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path list = DataBundles.getPort(inputs, "in1");
        Path ref = DataBundles.setReference(list, URI.create("http://example.com/"));
        assertFalse(Files.isRegularFile(list));
        assertFalse(Files.isDirectory(list));
        assertTrue(Files.isRegularFile(ref));                
        DataBundles.createList(list);
    }
    
    @Test
	public void newListItem() throws Exception {
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		Path item0 = DataBundles.newListItem(list);
		assertEquals(list, item0.getParent());
		assertTrue(item0.getFileName().toString().contains("0"));
		assertFalse(Files.exists(item0));
		DataBundles.setStringValue(item0, "test");

		Path item1 = DataBundles.newListItem(list);
		assertTrue(item1.getFileName().toString().contains("1"));
		// Because we've not actually created item1 yet
		assertEquals(item1, DataBundles.newListItem(list));
		DataBundles.setStringValue(item1, "test");

		// Check that DataBundles.newListItem can deal with gaps
		Files.delete(item0);
		Path item2 = DataBundles.newListItem(list);
		assertTrue(item2.getFileName().toString().contains("2"));

		// Check that non-numbers don't interfere
		Path nonumber = list.resolve("nonumber");
		Files.createFile(nonumber);
		item2 = DataBundles.newListItem(list);
		assertTrue(item2.getFileName().toString().contains("2"));

		// Check that extension is stripped
		Path five = list.resolve("5.txt");
		Files.createFile(five);
		Path item6 = DataBundles.newListItem(list);
		assertTrue(item6.getFileName().toString().contains("6"));
	}
    
    @Test
    public void resolveString() throws Exception {
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		// 0 string value
		DataBundles.setStringValue(DataBundles.newListItem(list), "test0");
		// 1 http:// reference
		URI reference = URI.create("http://example.com/");
		DataBundles.setReference(DataBundles.newListItem(list), reference);
		// 2 file:/// reference
		Path tmpFile = Files.createTempFile("test", ".txt");
		URI fileRef = tmpFile.toUri();
		assertEquals("file", fileRef.getScheme());
		DataBundles.setReference(DataBundles.newListItem(list), fileRef);
		// 3 empty (null)
		// 4 error
		DataBundles.setError(DataBundles.getListItem(list,  4), "Example error", "1. Tried it\n2. Didn't work");
		
		
		
		
		Object resolved = DataBundles.resolve(list, ResolveOptions.STRING);
		assertTrue("Didn't resolve to a list", resolved instanceof List);
		
		List resolvedList = (List) resolved;
		assertEquals("Unexpected list size", 5, resolvedList.size());
		
		assertTrue(resolvedList.get(0) instanceof String);
		assertEquals("test0", resolvedList.get(0));
		
		assertTrue(resolvedList.get(1) instanceof URL);
		assertEquals(reference, ((URL)resolvedList.get(1)).toURI());
		
		assertTrue(resolvedList.get(2) instanceof File);
		assertEquals(tmpFile.toFile(), resolvedList.get(2));
		
		assertNull(resolvedList.get(3));
		assertTrue(resolvedList.get(4) instanceof ErrorDocument);
		assertEquals("Example error", ((ErrorDocument)resolvedList.get(4)).getMessage());
		
    }    

    @Test
    public void resolveNestedString() throws Exception {
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		
		
		Path nested0 = DataBundles.newListItem(list);
		DataBundles.newListItem(nested0);		
		DataBundles.setStringValue(DataBundles.newListItem(nested0), "test0,0");
		DataBundles.setStringValue(DataBundles.newListItem(nested0), "test0,1");
		DataBundles.setStringValue(DataBundles.newListItem(nested0), "test0,2");
		Path nested1 = DataBundles.newListItem(list);
		DataBundles.newListItem(nested1); // empty
		Path nested2 = DataBundles.newListItem(list);
		DataBundles.newListItem(nested2);
		DataBundles.setStringValue(DataBundles.newListItem(nested2), "test2,0");
		
		
		
		List<List<String>> resolved = (List<List<String>>) DataBundles.resolve(list, ResolveOptions.STRING);
		
		assertEquals("Unexpected list size", 3, resolved.size());
		assertEquals("Unexpected sublist[0] size", 3, resolved.get(0).size());
		assertEquals("Unexpected sublist[1] size", 0, resolved.get(1).size());
		assertEquals("Unexpected sublist[2] size", 1, resolved.get(2).size());

		
		assertEquals("test0,0", resolved.get(0).get(0));
		assertEquals("test0,1", resolved.get(0).get(1));
		assertEquals("test0,2", resolved.get(0).get(2));
		assertEquals("test2,0", resolved.get(2).get(0));		
    }        


    @Test
    public void resolveStream() throws Exception {
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		
		Path nested0 = DataBundles.newListItem(list);
		DataBundles.newListItem(nested0);		
		DataBundles.setStringValue(DataBundles.newListItem(nested0), "test0,0");
		DataBundles.setStringValue(DataBundles.newListItem(nested0), "test0,1");
		DataBundles.setStringValue(DataBundles.newListItem(nested0), "test0,2");
		DataBundles.setError(DataBundles.newListItem(nested0), "Ignore me", "This error is hidden");
		Path nested1 = DataBundles.newListItem(list);
		DataBundles.newListItem(nested1); // empty
		Path nested2 = DataBundles.newListItem(list);
		DataBundles.newListItem(nested2);
		DataBundles.setStringValue(DataBundles.newListItem(nested2), "test2,0");
		DataBundles.setReference(DataBundles.newListItem(nested2), URI.create("http://example.com/"));
		
		

		assertEquals(6, DataBundles.resolveAsStream(list, Object.class).count());		
		assertEquals(6, DataBundles.resolveAsStream(list, Path.class).count());
		assertEquals(5, DataBundles.resolveAsStream(list, URI.class).count());
		assertEquals(1, DataBundles.resolveAsStream(list, URL.class).count());
		assertEquals(0, DataBundles.resolveAsStream(list, File.class).count());
		assertEquals(1, DataBundles.resolveAsStream(list, ErrorDocument.class).count());
		// Let's have a look at one of the types in detail
		assertEquals(4, DataBundles.resolveAsStream(list, String.class).count());		
		Stream<String> resolved = DataBundles.resolveAsStream(list, String.class);
		Object[] strings = resolved.sorted().map(t -> t.replace("test", "X")).toArray();
		// NOTE: We can only assume the below order because we used .sorted()
		assertEquals("X0,0", strings[0]);
		assertEquals("X0,1", strings[1]);
		assertEquals("X0,2", strings[2]);
		assertEquals("X2,0", strings[3]);
    }        
    
    @Test
    public void resolveURIs() throws Exception {
    	Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		// 0 string value
		Path test0 = DataBundles.newListItem(list);
		DataBundles.setStringValue(test0, "test0");
		// 1 http:// reference
		URI reference = URI.create("http://example.com/");
		DataBundles.setReference(DataBundles.newListItem(list), reference);
		// 2 file:/// reference
		Path tmpFile = Files.createTempFile("test", ".txt");
		URI fileRef = tmpFile.toUri();
		assertEquals("file", fileRef.getScheme());
		DataBundles.setReference(DataBundles.newListItem(list), fileRef);
		// 3 empty (null)
		// 4 error
		Path error4 = DataBundles.getListItem(list,  4);
		DataBundles.setError(error4, "Example error", "1. Tried it\n2. Didn't work");
		
		List resolved = (List) DataBundles.resolve(list, ResolveOptions.URI);
		assertEquals(test0.toUri(), resolved.get(0));
		assertEquals(reference, resolved.get(1));
		assertEquals(fileRef, resolved.get(2));
		assertNull(resolved.get(3));
		// NOTE: Need to get the Path again due to different file extension
		assertTrue(resolved.get(4) instanceof ErrorDocument);		
		//assertTrue(DataBundles.getListItem(list,  4).toUri(), resolved.get(4));
    }
    

    @Test
    public void resolvePaths() throws Exception {
    	Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		// 0 string value
		Path test0 = DataBundles.newListItem(list);
		DataBundles.setStringValue(test0, "test0");
		// 1 http:// reference
		URI reference = URI.create("http://example.com/");
		Path test1 = DataBundles.setReference(DataBundles.newListItem(list), reference);
		// 2 file:/// reference
		Path tmpFile = Files.createTempFile("test", ".txt");
		URI fileRef = tmpFile.toUri();
		assertEquals("file", fileRef.getScheme());
		Path test2 = DataBundles.setReference(DataBundles.newListItem(list), fileRef);
		// 3 empty (null)
		// 4 error
		Path error4 = DataBundles.setError(DataBundles.getListItem(list,  4), "Example error", "1. Tried it\n2. Didn't work");
		
		List<Path> resolved = (List<Path>) DataBundles.resolve(list, ResolveOptions.PATH);
		assertEquals(test0, resolved.get(0));
		assertEquals(test1, resolved.get(1));
		assertEquals(test2, resolved.get(2));
		assertNull(resolved.get(3));
		assertEquals(error4, resolved.get(4));		
    }

    @Test
    public void resolveReplaceError() throws Exception {
    	Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		// 0 string value
		DataBundles.setStringValue(DataBundles.newListItem(list), "test0");
		// 1 error
		DataBundles.setError(DataBundles.newListItem(list), 
				"Example error", "1. Tried it\n2. Didn't work");
		
		List resolved = (List) DataBundles.resolve(list, ResolveOptions.STRING, ResolveOptions.REPLACE_ERRORS);
		assertEquals("test0", resolved.get(0));
		assertNull(resolved.get(1));
    }

    @Test
    public void resolveReplaceNull() throws Exception {
    	Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		// 0 string value
		Path test0 = DataBundles.newListItem(list);
		DataBundles.setStringValue(test0, "test0");
		// 1 empty
		// 2 error
		DataBundles.setError(DataBundles.getListItem(list, 2), 
				"Example error", "1. Tried it\n2. Didn't work");
		
		List resolved = (List) DataBundles.resolve(list, ResolveOptions.REPLACE_ERRORS, ResolveOptions.REPLACE_NULL);
		assertEquals(test0, resolved.get(0));
		assertEquals("", resolved.get(1));
		assertEquals("", resolved.get(2));
    }
    

    @Test
    public void resolveDefault() throws Exception {
    	Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		// 0 string value
		Path test0 = DataBundles.newListItem(list);
		DataBundles.setStringValue(test0, "test0");
		// 1 http:// reference
		URI reference = URI.create("http://example.com/");
		Path test1 = DataBundles.setReference(DataBundles.newListItem(list), reference);
		// 2 file:/// reference
		Path tmpFile = Files.createTempFile("test", ".txt");
		URI fileRef = tmpFile.toUri();
		assertEquals("file", fileRef.getScheme());
		Path test2 = DataBundles.setReference(DataBundles.newListItem(list), fileRef);
		// 3 empty (null)
		// 4 error
		Path error4 = DataBundles.setError(DataBundles.getListItem(list,  4), "Example error", "1. Tried it\n2. Didn't work");
		
		List resolved = (List) DataBundles.resolve(list, ResolveOptions.DEFAULT);
		assertEquals(test0, resolved.get(0));
		assertTrue(resolved.get(1) instanceof URL);
		assertEquals("http://example.com/", resolved.get(1).toString());
		assertTrue(resolved.get(2) instanceof File);
		assertEquals(tmpFile.toFile(), resolved.get(2));
		assertNull(resolved.get(3));
		assertTrue(resolved.get(4) instanceof ErrorDocument);
    }
    
    @Test
    public void resolveBinaries() throws Exception {
    	Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		Path item = DataBundles.newListItem(list);

		byte[] bytes = new byte[] { 
				// Those lovely lower bytes who don't work well in UTF-8
				0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31, 
				// and some higher ones for fun
				-1,-2,-3,-4,-5,-6,-7,-8,-9,-10,-11,-12,-13,-14,-15,-16,-17,-18,
				-19,-20,-21,-22,-23,-24,-25,-26,-27,-28,-29,-30,-31
		};
		Files.write(item, bytes);
		
		
		List resolvedBytes = (List)DataBundles.resolve(list, ResolveOptions.BYTES);
		assertArrayEquals(bytes, (byte[])resolvedBytes.get(0));

		List resolvedString = (List)DataBundles.resolve(list, ResolveOptions.STRING);		
		// The below will always fail as several of the above bytes are not parsed as valid UTF-8
		// but instead be substituted with replacement characters. 		
		//assertArrayEquals(bytes, ((String)resolvedString.get(0)).getBytes(StandardCharsets.UTF_8));
    }
    
    @Test
	public void setErrorArgs() throws Exception {
		Path inputs = DataBundles.getInputs(dataBundle);
		Path portIn1 = DataBundles.getPort(inputs, "in1");
		Path errorPath = DataBundles.setError(portIn1, "Something did not work", "A very\n long\n error\n trace");		
		assertEquals("in1.err", errorPath.getFileName().toString());

		List<String> errLines = Files.readAllLines(errorPath, Charset.forName("UTF-8"));
		assertEquals(6, errLines.size());
		assertEquals("", errLines.get(0));
		assertEquals("Something did not work", errLines.get(1));
		assertEquals("A very", errLines.get(2));
		assertEquals(" long", errLines.get(3));
		assertEquals(" error", errLines.get(4));
		assertEquals(" trace", errLines.get(5));
	}

    @Test
	public void setErrorCause() throws Exception {		
		Path inputs = DataBundles.getInputs(dataBundle);
		Path portIn1 = DataBundles.getPort(inputs, "in1");
		Path cause1 = DataBundles.setError(portIn1, "Something did not work", "A very\n long\n error\n trace");
		Path portIn2 = DataBundles.getPort(inputs, "in2");
		Path cause2 = DataBundles.setError(portIn2, "Something else did not work", "Shorter trace");
		
		
		Path outputs = DataBundles.getOutputs(dataBundle);
		Path portOut1 = DataBundles.getPort(outputs, "out1");
		Path errorPath = DataBundles.setError(portOut1, "Errors in input", "", cause1, cause2);
		
		List<String> errLines = Files.readAllLines(errorPath, Charset.forName("UTF-8"));
		assertEquals("../inputs/in1.err", errLines.get(0));
		assertEquals("../inputs/in2.err", errLines.get(1));
		assertEquals("", errLines.get(2));
	}

    @Test
    public void setErrorExistsAsError() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        Path err = DataBundles.setError(in1, "a", "b");
        assertFalse(Files.exists(in1));
        assertTrue(Files.isRegularFile(err));
        DataBundles.setError(in1, "c", "d");
        assertEquals("c", DataBundles.getError(in1).getMessage());
    }

    @Test(expected=FileAlreadyExistsException.class)
    public void setErrorExistsAsList() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path list = DataBundles.getPort(inputs, "in1");
        DataBundles.createList(list);
        assertFalse(Files.isRegularFile(list));
        assertTrue(Files.isDirectory(list));
        DataBundles.setError(list, "a", "b");
    }

	@Test(expected=FileAlreadyExistsException.class)
    public void setErrorExistsAsReference() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        Path ref = DataBundles.setReference(in1, URI.create("http://example.com/"));
        assertFalse(Files.exists(in1));
        assertTrue(Files.isRegularFile(ref));
        DataBundles.setError(in1, "a", "b");
    }
	
    @Test(expected=FileAlreadyExistsException.class)
    public void setErrorExistsAsValue() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        DataBundles.setStringValue(in1, "test");
        assertTrue(Files.isRegularFile(in1));
        DataBundles.setError(in1, "a", "b");
    }
	
	@Test
	public void setErrorObj() throws Exception {
		Path inputs = DataBundles.getInputs(dataBundle);

		Path portIn1 = DataBundles.getPort(inputs, "in1");
		Path cause1 = DataBundles.setError(portIn1, "a", "b");
		Path portIn2 = DataBundles.getPort(inputs, "in2");
		Path cause2 = DataBundles.setError(portIn2, "c", "d");
		
		
		Path outputs = DataBundles.getOutputs(dataBundle);
		Path portOut1 = DataBundles.getPort(outputs, "out1");

		ErrorDocument error = new ErrorDocument();
		error.getCausedBy().add(cause1);
		error.getCausedBy().add(cause2);
		
		error.setMessage("Something did not work");
		error.setTrace("Here\nis\nwhy\n");
		
		Path errorPath = DataBundles.setError(portOut1, error);		
		assertEquals("out1.err", errorPath.getFileName().toString());

		List<String> errLines = Files.readAllLines(errorPath, Charset.forName("UTF-8"));
		assertEquals(8, errLines.size());
		assertEquals("../inputs/in1.err", errLines.get(0));
		assertEquals("../inputs/in2.err", errLines.get(1));
		assertEquals("", errLines.get(2));
		assertEquals("Something did not work", errLines.get(3));
		assertEquals("Here", errLines.get(4));
		assertEquals("is", errLines.get(5));
		assertEquals("why", errLines.get(6));
		assertEquals("", errLines.get(7));
	}
	
    @Test(expected = FileAlreadyExistsException.class)
    public void setReferenceExistsAsError() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        Path err = DataBundles.setError(in1, "a", "b");
        assertFalse(Files.exists(in1));
        assertTrue(Files.isRegularFile(err));
        DataBundles.setReference(in1, URI.create("http://example.com/"));
    }
	
    @Test(expected = FileAlreadyExistsException.class)
    public void setReferenceExistsAsList() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        DataBundles.createList(in1);
        assertTrue(Files.isDirectory(in1));
        DataBundles.setReference(in1, URI.create("http://example.com/"));
    }

    @Test
    public void setReferenceExistsAsReference() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        Path ref = DataBundles.setReference(in1, URI.create("http://example.com/"));
        assertFalse(Files.exists(in1));
        assertTrue(Files.isRegularFile(ref));
        DataBundles.setReference(in1, URI.create("http://example.com/"));
    }
    

    @Test(expected = FileAlreadyExistsException.class)
    public void setReferenceExistsAsValue() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        DataBundles.setStringValue(in1, "Hello");
        assertTrue(Files.isRegularFile(in1));
        DataBundles.setReference(in1, URI.create("http://example.com/"));
    }
    
    @Test(expected=FileAlreadyExistsException.class)
    public void setStringExistsAsError() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        Path err = DataBundles.setError(in1, "x", "X");
        assertFalse(Files.exists(in1));
        assertTrue(Files.isRegularFile(err));
        DataBundles.setStringValue(in1, "Hello");
    }

    
    @Test(expected=FileAlreadyExistsException.class)
    public void setStringExistsAsList() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        DataBundles.createList(in1);
        assertTrue(Files.isDirectory(in1));
        DataBundles.setStringValue(in1, "Hello");
    }
    
    

    @Test(expected=FileAlreadyExistsException.class)
    public void setStringExistsAsReference() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        Path ref = DataBundles.setReference(in1, URI.create("http://example.com/"));
        assertFalse(Files.exists(in1));
        assertTrue(Files.isRegularFile(ref));
        DataBundles.setStringValue(in1, "Hello");
    }

    @Test
    public void setStringExistsAsString() throws Exception {
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        DataBundles.setStringValue(in1, "A");
        assertTrue(Files.isRegularFile(in1));
        DataBundles.setStringValue(in1, "B");
        assertEquals("B", DataBundles.getStringValue(in1));
    }

    @Test
    public void getIntermediates() throws Exception {
        Path intermediates = DataBundles.getIntermediates(dataBundle);
        assertEquals("/intermediates", intermediates.toString());
        assertTrue(Files.isDirectory(intermediates));
    }

    
    @Test(expected=FileAlreadyExistsException.class)
    public void getIntermediatesFails() throws Exception {
        Path intermediates = DataBundles.getIntermediates(dataBundle);
        Files.delete(intermediates);
        Files.createFile(intermediates);
        DataBundles.getIntermediates(dataBundle);
    }
    
    @Test
    public void getIntermediate() throws Exception {
        UUID uuid = UUID.randomUUID();
        Path inter = DataBundles.getIntermediate(dataBundle, uuid);
        assertFalse(Files.exists(inter));
        DataBundles.setStringValue(inter, "intermediate");
        Path parent = inter.getParent();
        assertEquals(dataBundle.getRoot().resolve("intermediates"), parent.getParent());
        String parentName = parent.getFileName().toString();
        assertEquals(2, parentName.length());
        assertTrue(uuid.toString().startsWith(parentName));
        // Filename is a valid string
        String interFileName = inter.getFileName().toString();
        assertTrue(interFileName.startsWith(parentName));
        assertEquals(uuid, UUID.fromString(interFileName));
    }
    
    @Test
    public void getWorkflow() throws Exception {
        Path wf = DataBundles.getWorkflow(dataBundle);
        assertEquals("/workflow", wf.toString());
    }

    @Test
    public void setWorkflowBundle() throws Exception {
        WorkflowBundleIO wfBundleIO = new WorkflowBundleIO();
        WorkflowBundle wfBundle = wfBundleIO.createBundle();
        DataBundles.setWorkflowBundle(dataBundle, wfBundle);
        
        Path wf = DataBundles.getWorkflow(dataBundle);
        assertEquals("/workflow.wfbundle", wf.toString());        
        assertEquals("application/vnd.taverna.scufl2.workflow-bundle", 
                Files.probeContentType(wf));
    }

    // TODO: Why was this ignored? Check with taverna-language-0.15.x RC emails
    @Ignore
    @Test
    public void getWorkflowBundle() throws Exception {
        WorkflowBundleIO wfBundleIO = new WorkflowBundleIO();
        WorkflowBundle wfBundle = wfBundleIO.createBundle();
        
        String name = wfBundle.getName();
        String wfName = wfBundle.getMainWorkflow().getName();
        URI id = wfBundle.getIdentifier();
        
        DataBundles.setWorkflowBundle(dataBundle, wfBundle);

        // Reload the bundle
        wfBundle = DataBundles.getWorkflowBundle(dataBundle);        
        assertEquals(name, wfBundle.getName());
        assertEquals(wfName, wfBundle.getMainWorkflow().getName());        
        assertEquals(id, wfBundle.getIdentifier());        
    }

    @Test
    public void getWorkflowReport() throws Exception {
        Path runReport = DataBundles.getWorkflowRunReport(dataBundle);
        assertEquals("/workflowrun.json", runReport.toString());
    }
    
    @Test
    public void getWorkflowReportAsJson() throws Exception {        
        Path runReport = DataBundles.getWorkflowRunReport(dataBundle);
        DataBundles.setStringValue(runReport, "{ \"valid\": \"not really\", \"number\": 1337 }");
        JsonNode json = DataBundles.getWorkflowRunReportAsJson(dataBundle);
        assertEquals("not really", json.path("valid").asText());
        assertEquals(1337, json.path("number").asInt());
    }
    
    @Test
    public void setWorkflowReport() throws Exception {
        ObjectNode report = JsonNodeFactory.instance.objectNode();
        report.put("number", 1337);
        DataBundles.setWorkflowRunReport(dataBundle, report);
        Path runReport = DataBundles.getWorkflowRunReport(dataBundle);
        String json = DataBundles.getStringValue(runReport);
        assertTrue(json.contains("number"));
        assertTrue(json.contains("1337"));
    }
    

}

