package org.apache.taverna.scufl2.api;

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


import static org.junit.Assert.*;

import java.util.UUID;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;
import org.junit.Test;


public class TestAbstractRevisioned {
    @Test
    public void profileName() throws Exception {
        Profile p = new Profile();
        UUID uuid = UUID.fromString(p.getName());
        assertEquals(4, uuid.version());
        
    }
    
    @Test
    public void workflow() throws Exception {
        Workflow wf = new Workflow();
        UUID uuid = UUID.fromString(wf.getName());
        assertEquals(4, uuid.version());
    }
    
    @Test
    public void workflowBundle() throws Exception {
        WorkflowBundle wfBundle = new WorkflowBundle();
        UUID uuid = UUID.fromString(wfBundle.getName());
        assertEquals(4, uuid.version());
    }
}
