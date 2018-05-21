package org.apache.taverna.scufl2.cwl;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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
}
