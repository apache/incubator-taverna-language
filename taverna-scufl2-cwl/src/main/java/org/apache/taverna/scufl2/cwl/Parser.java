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

    public ArrayList<InputField> parseInputs() {
        int startIndex = 0;
        int endIndex = -1;
        int depth = -1;

        /**
         * Search for start and end of inputs section
         */
        for(Map.Entry<Integer, String> entry: yamlFile.entrySet()) {
            int index = entry.getKey();
            String line = entry.getValue();
            String key = getKeyFromLine(line);
            if(key.equals("inputs")) {
                startIndex = index;
                endIndex = index;
                depth = getDepth(line);
            } else if(!line.equals("") && getDepth(line) <= depth) {
                break;
            } else {
                endIndex++;
            }
        }
        /**
         * Parse each input
         */
        ArrayList<InputField> result = new ArrayList<>();
        for(int i = startIndex+1; i <= endIndex; i++) {
            int curDepth = getDepth(yamlFile.get(i));
            // If current element is a child of inputs key
            if(curDepth == depth + 1) {
                result.add(parseInputField(i));
            }
        }

        return result;
    }

    public InputField parseInputField(int startIndex) {
        String line = yamlFile.get(startIndex);
        int depth = getDepth(line);
        String id = getKeyFromLine(line);
        String value = getValueFromLine(line);

        if(!value.equals("")) {
            return new InputField(id, value);
        }

        InputField field = new InputField(id);
        for(int i = startIndex+1; i < length; i++) {
            String curLine = yamlFile.get(i);
            if(curLine.equals("")) {
                // Ignore empty lines
                continue;
            }
            if(getDepth(curLine) <= depth) {
                // Out of input section
                break;
            }
            String key = getKeyFromLine(curLine);
            value = getValueFromLine(curLine);

            if(key.trim().equals("type")) {
                field.type = value;
            } else if(key.trim().equals("inputBinding")) {
                
                int curDepth = getDepth(curLine);
                int nextIndex = getNextLineIndex(i);
                String nextLine = yamlFile.get(nextIndex);
                String nextKey = getKeyFromLine(nextLine);
                String nextValue = getValueFromLine(nextLine);

                if(nextKey.equals("position")){
                    field.position = Integer.parseInt(nextValue);
                } else if(nextKey.equals("prefix")){
                    field.prefix = nextValue;
                }

                // Check if we have another inputBinding property
                nextIndex = getNextLineIndex(nextIndex);
                nextLine = yamlFile.get(nextIndex);
                if(getDepth(nextLine) == curDepth + 1) {
                    nextKey = getKeyFromLine(nextLine);
                    nextValue = getValueFromLine(nextLine);
                    if(nextKey.equals("position")){
                        field.position = Integer.parseInt(nextValue);
                    } else if(nextKey.equals("prefix")){
                        field.prefix = nextValue.trim();
                    }
                }
            }
        }

        return field;
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
