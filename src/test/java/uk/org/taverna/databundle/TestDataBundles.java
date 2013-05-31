package uk.org.taverna.databundle;

import static org.junit.Assert.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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

import org.junit.Test;
import org.purl.wf4ever.robundle.Bundle;

public class TestDataBundles {
	protected void checkSignature(Path zip) throws IOException {
		String MEDIATYPE = "application/vnd.wf4ever.robundle+zip";
		// Check position 30++ according to RO Bundle specification
		// http://purl.org/wf4ever/ro-bundle#ucf
		byte[] expected = ("mimetype" + MEDIATYPE + "PK").getBytes("ASCII");

		try (InputStream in = Files.newInputStream(zip)) {
			byte[] signature = new byte[expected.length];
			int MIME_OFFSET = 30;
			assertEquals(MIME_OFFSET, in.skip(MIME_OFFSET));
			assertEquals(expected.length, in.read(signature));
			assertArrayEquals(expected, signature);
		}
	}
	
	@Test
    public void clear() throws Exception {
        Bundle dataBundle = DataBundles.createBundle();
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
        Bundle dataBundle = DataBundles.createBundle();
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
		Bundle dataBundle = DataBundles.createBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		assertTrue(Files.isDirectory(list));
	}
	
	@Test
	public void getError() throws Exception {	
		Bundle dataBundle = DataBundles.createBundle();
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
		Bundle dataBundle = DataBundles.createBundle();
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
		Bundle dataBundle = DataBundles.createBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		assertTrue(Files.isDirectory(inputs));
		// Second time should not fail because it already exists
		inputs = DataBundles.getInputs(dataBundle);
		assertTrue(Files.isDirectory(inputs));
		assertEquals(dataBundle.getRoot(), inputs.getParent());
	}

	@Test
	public void getList() throws Exception {
		Bundle dataBundle = DataBundles.createBundle();
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
		Bundle dataBundle = DataBundles.createBundle();
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
    public void getListItemChecksExtension() throws Exception {
        Bundle dataBundle = DataBundles.createBundle();
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
		Bundle dataBundle = DataBundles.createBundle();
		Path outputs = DataBundles.getOutputs(dataBundle);
		assertTrue(Files.isDirectory(outputs));
		// Second time should not fail because it already exists
		outputs = DataBundles.getOutputs(dataBundle);
		assertTrue(Files.isDirectory(outputs));
		assertEquals(dataBundle.getRoot(), outputs.getParent());
	}
	
	@Test
	public void getPort() throws Exception {
		Bundle dataBundle = DataBundles.createBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path portIn1 = DataBundles.getPort(inputs, "in1");
		assertFalse(Files.exists(portIn1));
		assertEquals(inputs, portIn1.getParent());
	}

	@Test
    public void getPortChecksExtension() throws Exception {
        Bundle dataBundle = DataBundles.createBundle();
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
		Bundle dataBundle = DataBundles.createBundle();
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
		Bundle dataBundle = DataBundles.createBundle();
		assertFalse(DataBundles.hasInputs(dataBundle));
		DataBundles.getInputs(dataBundle); // create on demand
		assertTrue(DataBundles.hasInputs(dataBundle));
	}

	@Test
	public void hasOutputs() throws Exception {
		Bundle dataBundle = DataBundles.createBundle();
		assertFalse(DataBundles.hasOutputs(dataBundle));
		DataBundles.getInputs(dataBundle); // independent
		assertFalse(DataBundles.hasOutputs(dataBundle));
		DataBundles.getOutputs(dataBundle); // create on demand
		assertTrue(DataBundles.hasOutputs(dataBundle));
	}

	@Test
	public void isError() throws Exception {
		Bundle dataBundle = DataBundles.createBundle();
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
		Bundle dataBundle = DataBundles.createBundle();
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
		Bundle dataBundle = DataBundles.createBundle();
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
        Bundle bundle = DataBundles.createBundle();
        Path inputs = DataBundles.getInputs(bundle);
        DataBundles.setError(DataBundles.getPort(inputs, "test"),
                "error", "");
        assertFalse(DataBundles.isValue(DataBundles.getPorts(inputs).get("test")));
    }
	
	@Test
    public void isValueOnReference() throws Exception {
	    Bundle bundle = DataBundles.createBundle();
	    Path inputs = DataBundles.getInputs(bundle);
	    DataBundles.setReference(DataBundles.getPort(inputs, "test"), URI.create("http://www.example.com/"));
	    assertFalse(DataBundles.isValue(DataBundles.getPorts(inputs).get("test")));
    }
	
	@Test
	public void listOfLists() throws Exception {
		Bundle dataBundle = DataBundles.createBundle();
		Path inputs = DataBundles.getInputs(dataBundle);
		Path list = DataBundles.getPort(inputs, "in1");
		DataBundles.createList(list);
		Path sublist0 = DataBundles.newListItem(list);
		DataBundles.createList(sublist0);
		
		Path sublist1 = DataBundles.newListItem(list);
		DataBundles.createList(sublist1);
		
		assertEquals(Arrays.asList("0/", "1/"), ls(list));
		
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
        Bundle dataBundle = DataBundles.createBundle();
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
	    Bundle dataBundle = DataBundles.createBundle();
        Path inputs = DataBundles.getInputs(dataBundle);
        Path list = DataBundles.getPort(inputs, "in1");
        DataBundles.setStringValue(list, "A string");
        assertTrue(Files.isRegularFile(list));
        assertFalse(Files.isDirectory(list));
        DataBundles.createList(list);
    }
	

    @Test(expected=FileAlreadyExistsException.class)
    public void newListAlreadyExistsAsReference() throws Exception {
        Bundle dataBundle = DataBundles.createBundle();
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
		Bundle dataBundle = DataBundles.createBundle();
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
	public void setErrorArgs() throws Exception {
		Bundle dataBundle = DataBundles.createBundle();
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
		Bundle dataBundle = DataBundles.createBundle();
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
        Bundle dataBundle = DataBundles.createBundle();
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
        Bundle dataBundle = DataBundles.createBundle();
        Path inputs = DataBundles.getInputs(dataBundle);
        Path list = DataBundles.getPort(inputs, "in1");
        DataBundles.createList(list);
        assertFalse(Files.isRegularFile(list));
        assertTrue(Files.isDirectory(list));
        DataBundles.setError(list, "a", "b");
    }

	@Test(expected=FileAlreadyExistsException.class)
    public void setErrorExistsAsReference() throws Exception {
        Bundle dataBundle = DataBundles.createBundle();
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        Path ref = DataBundles.setReference(in1, URI.create("http://example.com/"));
        assertFalse(Files.exists(in1));
        assertTrue(Files.isRegularFile(ref));
        DataBundles.setError(in1, "a", "b");
    }
	
    @Test(expected=FileAlreadyExistsException.class)
    public void setErrorExistsAsValue() throws Exception {
        Bundle dataBundle = DataBundles.createBundle();
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        DataBundles.setStringValue(in1, "test");
        assertTrue(Files.isRegularFile(in1));
        DataBundles.setError(in1, "a", "b");
    }
	
	@Test
	public void setErrorObj() throws Exception {
		Bundle dataBundle = DataBundles.createBundle();
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
        Bundle dataBundle = DataBundles.createBundle();
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        Path err = DataBundles.setError(in1, "a", "b");
        assertFalse(Files.exists(in1));
        assertTrue(Files.isRegularFile(err));
        DataBundles.setReference(in1, URI.create("http://example.com/"));
    }
	
    @Test(expected = FileAlreadyExistsException.class)
    public void setReferenceExistsAsList() throws Exception {
        Bundle dataBundle = DataBundles.createBundle();
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        DataBundles.createList(in1);
        assertTrue(Files.isDirectory(in1));
        DataBundles.setReference(in1, URI.create("http://example.com/"));
    }

    @Test
    public void setReferenceExistsAsReference() throws Exception {
        Bundle dataBundle = DataBundles.createBundle();
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        Path ref = DataBundles.setReference(in1, URI.create("http://example.com/"));
        assertFalse(Files.exists(in1));
        assertTrue(Files.isRegularFile(ref));
        DataBundles.setReference(in1, URI.create("http://example.com/"));
    }
    

    @Test(expected = FileAlreadyExistsException.class)
    public void setReferenceExistsAsValue() throws Exception {
        Bundle dataBundle = DataBundles.createBundle();
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        DataBundles.setStringValue(in1, "Hello");
        assertTrue(Files.isRegularFile(in1));
        DataBundles.setReference(in1, URI.create("http://example.com/"));
    }
    
    @Test(expected=FileAlreadyExistsException.class)
    public void setStringExistsAsError() throws Exception {
        Bundle dataBundle = DataBundles.createBundle();
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        Path err = DataBundles.setError(in1, "x", "X");
        assertFalse(Files.exists(in1));
        assertTrue(Files.isRegularFile(err));
        DataBundles.setStringValue(in1, "Hello");
    }

    
    @Test(expected=FileAlreadyExistsException.class)
    public void setStringExistsAsList() throws Exception {
        Bundle dataBundle = DataBundles.createBundle();
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        DataBundles.createList(in1);
        assertTrue(Files.isDirectory(in1));
        DataBundles.setStringValue(in1, "Hello");
    }
    
    

    @Test(expected=FileAlreadyExistsException.class)
    public void setStringExistsAsReference() throws Exception {
        Bundle dataBundle = DataBundles.createBundle();
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        Path ref = DataBundles.setReference(in1, URI.create("http://example.com/"));
        assertFalse(Files.exists(in1));
        assertTrue(Files.isRegularFile(ref));
        DataBundles.setStringValue(in1, "Hello");
    }

    @Test
    public void setStringExistsAsString() throws Exception {
        Bundle dataBundle = DataBundles.createBundle();
        Path inputs = DataBundles.getInputs(dataBundle);
        Path in1 = DataBundles.getPort(inputs, "in1");
        DataBundles.setStringValue(in1, "A");
        assertTrue(Files.isRegularFile(in1));
        DataBundles.setStringValue(in1, "B");
        assertEquals("B", DataBundles.getStringValue(in1));
    }

    @Test
    public void getIntermediates() throws Exception {
        Bundle dataBundle = DataBundles.createBundle();
        Path intermediates = DataBundles.getIntermediates(dataBundle);
        assertEquals("/intermediates", intermediates.toString());
        assertTrue(Files.isDirectory(intermediates));
    }

    
    @Test(expected=FileAlreadyExistsException.class)
    public void getIntermediatesFails() throws Exception {
        Bundle dataBundle = DataBundles.createBundle();
        Path intermediates = DataBundles.getIntermediates(dataBundle);
        Files.delete(intermediates);
        Files.createFile(intermediates);
        DataBundles.getIntermediates(dataBundle);
    }
    
    @Test
    public void getIntermediate() throws Exception {
        Bundle dataBundle = DataBundles.createBundle();
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


}

