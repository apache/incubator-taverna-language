package org.apache.taverna.scufl2.api.io;

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
import java.io.OutputStream;
import java.util.Set;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;


/**
 * A writer for {@link WorkflowBundle}s.
 * 
 * Implementations specify workflow bundle formats (media types) that they can
 * write.
 */
public interface WorkflowBundleWriter {
	/**
	 * Returns the media types that this writer can handle.
	 * 
	 * @return the media types that this writer can handle
	 */
	Set<String> getMediaTypes();

	/**
	 * Writes a <code>WorkflowBundle</code> to a file with specified media type.
	 * 
	 * @param wfBundle
	 *            the workflow bundle to write
	 * @param destination
	 *            the file to write the workflow bundle to
	 * @param mediaType
	 *            the media type to write workflow bundle in
	 * @throws WriterException
	 *             if there is an error writing the workflow bundle
	 * @throws IOException
	 *             if there is an error writing the file
	 */
	void writeBundle(WorkflowBundle wfBundle, File destination, String mediaType)
			throws WriterException, IOException;

	/**
	 * Writes a <code>WorkflowBundle</code> to a stream with specified media
	 * type.
	 * 
	 * @param wfBundle
	 *            the workflow bundle to write
	 * @param output
	 *            the stream to write the workflow bundle to
	 * @param mediaType
	 *            the media type to write workflow bundle in
	 * @throws WriterException
	 *             if there is an error writing the workflow bundle
	 * @throws IOException
	 *             if there is an error writing to the stream
	 */
	void writeBundle(WorkflowBundle wfBundle, OutputStream output,
			String mediaType) throws WriterException, IOException;
}
