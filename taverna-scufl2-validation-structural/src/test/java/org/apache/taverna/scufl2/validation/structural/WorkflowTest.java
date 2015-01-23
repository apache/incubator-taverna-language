package org.apache.taverna.scufl2.validation.structural;
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


import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.validation.structural.ReportStructuralValidationListener;
import org.apache.taverna.scufl2.validation.structural.StructuralValidator;
import org.apache.taverna.scufl2.validation.structural.ValidatorState;
import org.junit.Test;



public class WorkflowTest {
	
	@Test
	public void testIncompleteWorkflow() throws ReaderException, IOException {
    	ReportStructuralValidationListener l = new ReportStructuralValidationListener();
		WorkflowBundle wb = new WorkflowBundle();
		Workflow w = new Workflow();
		wb.setMainWorkflow(w);
	    StructuralValidator sv = new StructuralValidator();
			sv.checkStructure(wb, l);
			@SuppressWarnings("unused")
			ValidatorState vs = sv.getValidatorState();
			assertEquals(1, l.getIncompleteWorkflows().size());
			assert(l.getIncompleteWorkflows().contains(w));	    
	}
	

}
