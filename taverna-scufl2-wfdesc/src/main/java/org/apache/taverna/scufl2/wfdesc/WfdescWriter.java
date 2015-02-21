package org.apache.taverna.scufl2.wfdesc;

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


import static org.apache.taverna.scufl2.wfdesc.WfdescReader.TEXT_VND_WF4EVER_WFDESC_TURTLE;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.WorkflowBundleWriter;
import org.apache.taverna.scufl2.api.io.WriterException;


public class WfdescWriter implements WorkflowBundleWriter {

	@Override
	public Set<String> getMediaTypes() {
		return new HashSet<String>(
				Arrays.asList(TEXT_VND_WF4EVER_WFDESC_TURTLE));
	}

	@Override
	public void writeBundle(WorkflowBundle wfBundle, File destination,
			String mediaType) throws WriterException, IOException {
		if (!mediaType.equals(TEXT_VND_WF4EVER_WFDESC_TURTLE)) {
			throw new WriterException("Unsupported media type: " + mediaType);
		}
		WfdescSerialiser serializer = new WfdescSerialiser();
		BufferedOutputStream outStream = new BufferedOutputStream(
				new FileOutputStream(destination));
		try {
			serializer.save(wfBundle, outStream);
		} finally {
			outStream.close();
		}

	}

	@Override
	public void writeBundle(WorkflowBundle wfBundle, OutputStream output,
			String mediaType) throws WriterException, IOException {
		if (!mediaType.equals(TEXT_VND_WF4EVER_WFDESC_TURTLE)) {
			throw new WriterException("Unsupported media type: " + mediaType);
		}

		WfdescSerialiser serializer = new WfdescSerialiser();
		serializer.save(wfBundle, output);

	}

}
