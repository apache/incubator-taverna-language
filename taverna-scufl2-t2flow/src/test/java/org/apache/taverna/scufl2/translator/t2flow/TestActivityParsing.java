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

import org.apache.taverna.scufl2.api.common.NamedSet;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;
import org.junit.Test;


@SuppressWarnings("unused")
public class TestActivityParsing {

    private static final String WF_ALL_ACTIVITIES = "/defaultActivitiesTaverna2.2.t2flow";
	private static final String WF_AS = "/as.t2flow";
	private static Scufl2Tools scufl2Tools = new Scufl2Tools();

    @Test
    public void readSimpleWorkflow() throws Exception {
        URL wfResource = getClass().getResource(WF_ALL_ACTIVITIES);
        assertNotNull("Could not find workflow " + WF_ALL_ACTIVITIES,
                wfResource);
        T2FlowParser parser = new T2FlowParser();
        parser.setValidating(true);
        // parser.setStrict(true);
        WorkflowBundle wfBundle = parser
                .parseT2Flow(wfResource.openStream());
		NamedSet<Configuration> cfgs = wfBundle.getProfiles().iterator().next()
				.getConfigurations();
        // System.out.println(cfgs);

    }
    
}
