package org.apache.taverna.scufl2.cwl;

import java.io.*;
import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.apache.commons.io.FileUtils;

import org.apache.taverna.scufl2.cwl.Parser;
import org.apache.taverna.scufl2.cwl.InputField;

public class TestParser {

    private static final String SIMPLE_STRING_INPUT = "/simple_string_input.cwl";
    private static final String INT_INPUT = "/int_input.cwl";

    @Test
    public void testGetDepth() throws Exception {

        assert Parser.getDepth("  test") == 1;
        assert Parser.getDepth("test") == 0;
        assert Parser.getDepth("    test") == 2;
    }

    @Test
    public void testGetKey() throws Exception {

        assert Parser.getKeyFromLine("  test: test_value").equals("test");
        assert Parser.getKeyFromLine("test: 1 ").equals("test");
        assert Parser.getKeyFromLine("    test:").equals("test");
    }

    @Test
    public void testGetValue() throws Exception {

        assert Parser.getValueFromLine("  test: test_value").equals("test_value");
        assert Parser.getValueFromLine("test: 1 ").equals("1");
        assert Parser.getValueFromLine("    test:").equals("");
    }

    @Test
    public void testSimpleInput() throws Exception {
        File yaml = FileUtils.getFile("src", "test", "resources", SIMPLE_STRING_INPUT);

        Parser parser = new Parser(yaml);

        ArrayList<InputField> inputs = parser.parseInputs();

        assertEquals(1, inputs.size());
        assertEquals("example_string", inputs.get(0).key);
    }

    @Test
    public void testIntInput() throws Exception {
        File yaml = FileUtils.getFile("src", "test", "resources", INT_INPUT);

        Parser parser = new Parser(yaml);

        ArrayList<InputField> inputs = parser.parseInputs();

        assertEquals(1, inputs.size());
        assertEquals("example_int", inputs.get(0).key);
        assertEquals("int", inputs.get(0).type);
        assertEquals(2, inputs.get(0).position);
        assertEquals("-i", inputs.get(0).prefix);
    }

    void printFile(File yaml) throws Exception {
        /**
         * Print file
         */
        FileReader fdesc = new FileReader(yaml);
        BufferedReader bufferedReader = new BufferedReader(fdesc);
        String yamlLine;
        while((yamlLine = bufferedReader.readLine()) != null) {
            System.out.println(yamlLine);
        }

        System.out.println("*************************");
        bufferedReader.close();
        /*****/
    }
}
