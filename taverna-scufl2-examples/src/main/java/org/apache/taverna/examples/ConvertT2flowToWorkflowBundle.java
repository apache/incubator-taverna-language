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


import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WriterException;

public class ConvertT2flowToWorkflowBundle {
	public static void main(String[] args) throws Exception, ReaderException,
			WriterException {
		new ConvertT2flowToWorkflowBundle().convert(args);
	}

	public void convert(String[] filepaths) throws ReaderException, IOException, WriterException {

		WorkflowBundleIO io = new WorkflowBundleIO();
		for (String filepath : filepaths) {
			File t2File = new File(filepath);
			
			String filename = t2File.getName();			
			filename = filename.replaceFirst("\\..*", ".wfbundle");			
			File scufl2File = new File(t2File.getParentFile(), filename);
			
			WorkflowBundle wfBundle = io.readBundle(t2File,
					"application/vnd.taverna.t2flow+xml");
			io.writeBundle(wfBundle, scufl2File,
					"application/vnd.taverna.scufl2.workflow-bundle");
		}

	}

}
