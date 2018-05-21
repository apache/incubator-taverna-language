package org.apache.taverna.scufl2.cwl;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



class InputField {

    public String key;
    public String type;
    public int position;
    public String prefix;

    public InputField(String _key) {
        key = _key;
        type = "";
        position = -1;
        prefix = "";
    }

    public InputField(String _key, String _type) {
        key = _key;
        type = _type;
        position = -1;
        prefix = "";
    }

    public InputField(String _key, String _type, int pos) {
        key = _key;
        type = _type;
        position = pos;
        prefix = "";
    }

    public InputField(String _key, String _type, int pos, String _prefix) {
        key = _key;
        type = _type;
        position = pos;
        prefix = _prefix;
    }
}


public class Parser {

    private String yamlLine;
    private int fileLength;

    private Map<Integer, String> yamlFile = null;

    public Parser(File file) {
        int counter = 0;

        yamlFile = new HashMap<>();

        FileReader yamlFileDescriptor = null;


        try {
            yamlFileDescriptor = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(yamlFileDescriptor);
            String parent = null;
            int parentDepth = 0;
            while((yamlLine = bufferedReader.readLine()) != null) {

                yamlFile.put(counter, yamlLine);
                counter = counter + 1;
            }

            bufferedReader.close();
            fileLength = counter;
        } catch (IOException e) {
            System.err.println("Parser init error: " + e );
        }
    }




    private int getNextLineIndex(int index) {
        index++;

        while(yamlFile.get(index).equals("")) {
            index++;
        }

        return index;
    }

    public static int getDepth(String line) {
        int count = 0;
        int idx = 0;
        while(idx < line.length()) {
            if(line.charAt(idx) != ' ') {
                break;
            }
            count++;
            idx++;
        }
        assert count % 2 == 0;
        return count / 2;
    }

    public static String getKeyFromLine(String line) {
        int commaIndex = line.indexOf(':');
        assert commaIndex != -1;

        return line.substring(0, commaIndex).trim();
    }

    public static String getValueFromLine(String line) {
        int commaIndex = line.indexOf(':');
        assert commaIndex != -1;

        return line.substring(commaIndex + 1).trim();
    }
}
