package org.apache.taverna.scufl2.translator.t2flow;
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


import static org.junit.Assert.assertNotNull;

import java.net.URL;

import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;
import org.junit.Test;


public class TestFastaWorkflow {
    
    private static final String WF_FASTA_AND_PSCAN = "/fasta_and_pscan.t2flow";
    private static final String WF_FASTA_PSCAN_AND_DBFETCH = "/fasta_pscan_and_dbfetch.t2flow";
    private static final String WF_SIMPLE_FASTA = "/simple_fasta.t2flow";

    
    @Test
    public void fastaPscan() throws Exception {
        URL wfResource = getClass().getResource(WF_FASTA_AND_PSCAN);
        assertNotNull("Could not find workflow " + WF_FASTA_AND_PSCAN,
                wfResource);
        T2FlowParser parser = new T2FlowParser();
        parser.setValidating(true);
        // parser.setStrict(true);
        WorkflowBundle wfBundle = parser.parseT2Flow(wfResource.openStream());

        Profile p = wfBundle.getMainProfile();
        for (Configuration c : p.getConfigurations()) {
            System.out.println(c.getConfigures());
            System.out.println(c.getJson());
        }
    }

    @Test
    public void fastaPscanDbfetch() throws Exception {
        URL wfResource = getClass().getResource(WF_FASTA_PSCAN_AND_DBFETCH);
        assertNotNull("Could not find workflow " + WF_FASTA_PSCAN_AND_DBFETCH,
                wfResource);
        T2FlowParser parser = new T2FlowParser();
        parser.setValidating(true);
        // parser.setStrict(true);
        WorkflowBundle wfBundle = parser.parseT2Flow(wfResource.openStream());

        Profile p = wfBundle.getMainProfile();
        for (Configuration c : p.getConfigurations()) {
            System.out.println(c.getConfigures());
            System.out.println(c.getJson());
        }
    }

    @Test
    public void simpleFasta() throws Exception {
        URL wfResource = getClass().getResource(WF_SIMPLE_FASTA);
        assertNotNull("Could not find workflow " + WF_SIMPLE_FASTA,
                wfResource);
        T2FlowParser parser = new T2FlowParser();
        parser.setValidating(true);
        // parser.setStrict(true);
        WorkflowBundle wfBundle = parser.parseT2Flow(wfResource.openStream());

        Profile p = wfBundle.getMainProfile();
        for (Configuration c : p.getConfigurations()) {
            System.out.println(c.getConfigures());
            System.out.println(c.getJson());
        }
    }

}
