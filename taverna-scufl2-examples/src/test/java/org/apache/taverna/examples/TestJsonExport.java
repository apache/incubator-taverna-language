package org.apache.taverna.examples;

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


import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.taverna.examples.JsonExport;
import org.junit.Ignore;
import org.junit.Test;

public class TestJsonExport {
    @Test
    public void jsonExportHelloAnyone() throws Exception {
        File tmp = File.createTempFile("helloanyone", ".t2flow");
        tmp.deleteOnExit();
        InputStream ebi = getClass().getResourceAsStream("/workflows/t2flow/helloanyone.t2flow");
        FileOutputStream output = new FileOutputStream(tmp);
        IOUtils.copy(ebi, output);
        output.close();

        
        JsonExport jsonExport = new JsonExport();
        jsonExport.convert(new String[]{tmp.getAbsolutePath()});        
        File jsonFile = new File(tmp.getAbsolutePath().replace(".t2flow", ".json"));
        assertTrue(jsonFile.isFile());
        jsonFile.deleteOnExit();
//      System.out.println(scufl2File);
        //System.out.println(FileUtils.readFileToString(jsonFile, "UTF-8"));
    }
    
    @Ignore("Takes 24 seconds!")
    @Test
    public void jsonExportNested() throws Exception {
        File tmp = File.createTempFile("enm", ".t2flow");
        tmp.deleteOnExit();
        InputStream ebi = getClass().getResourceAsStream("/workflows/t2flow/generic_enm_workflow_with_interaction_615643.t2flow");
        FileOutputStream output = new FileOutputStream(tmp);
        IOUtils.copy(ebi, output);
        output.close();

        
        JsonExport jsonExport = new JsonExport();
        jsonExport.convert(new String[]{tmp.getAbsolutePath()});        
        File jsonFile = new File(tmp.getAbsolutePath().replace(".t2flow", ".json"));
        assertTrue(jsonFile.isFile());
//        jsonFile.deleteOnExit();
      //System.out.println(jsonFile);
//        System.out.println(FileUtils.readFileToString(jsonFile, "UTF-8"));
    }
    
    
}
