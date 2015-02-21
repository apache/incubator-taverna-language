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
import java.io.InputStream;
import java.util.Set;

import org.apache.taverna.scufl2.api.container.WorkflowBundle;


/**
 * A reader for {@link WorkflowBundle}s.
 * 
 * Implementations specify workflow bundle formats (media types/mime types) that
 * they can read.
 */
public interface WorkflowBundleReader {
	/**
	 * Return the media types that this reader can read.
	 * <p>
	 * Returned media types are must be valid <code>mediaType</code> arguments
	 * to {@link #readBundle(File, String)} and/or
	 * {@link #readBundle(InputStream, String)} for this reader.
	 * <p>
	 * It is recommended, but not required, that the reader can also recognise
	 * those media types from {@link #guessMediaTypeForSignature(byte[])}.
	 * <p>
	 * If the returned set is empty, the reader should should be able to
	 * Recognise at least one media type from
	 * {@link #guessMediaTypeForSignature(byte[])}.
	 * 
	 * @return the media types that this reader can read, or an empty set if
	 *         this reader can't read any bundle formats.
	 */
	Set<String> getMediaTypes();

	/**
	 * Read a file containing a workflow bundle in the specified media type and
	 * return a <code>WorkflowBundle</code>.
	 * 
	 * @param bundleFile
	 *            the file containing the workflow bundle
	 * @param mediaType
	 *            the media type of the workflow bundle
	 * @return the <code>WorkflowBundle</code> read from the file
	 * @throws ReaderException
	 *             if there is an error parsing the workflow bundle
	 * @throws IOException
	 *             if there is an error reading the file
	 */
	WorkflowBundle readBundle(File bundleFile, String mediaType)
			throws ReaderException, IOException;

	/**
	 * Read a stream containing a workflow bundle in the specified media type
	 * and return a <code>WorkflowBundle</code>.
	 * 
	 * @param inputStream
	 *            the stream containing the workflow bundle
	 * @param mediaType
	 *            the media type of the workflow bundle
	 * @return the <code>WorkflowBundle</code> read from the stream
	 * @throws ReaderException
	 *             if there is an error parsing the workflow bundle
	 * @throws IOException
	 *             if there is an error reading from the stream
	 */
	WorkflowBundle readBundle(InputStream inputStream, String mediaType)
			throws ReaderException, IOException;

	/**
	 * Attempt to guess the media type for a stream or file that starts with
	 * these bytes.
	 * <p>
	 * Return <code>null</code> if ambiguous (more than one possibility) or
	 * unknown.
	 * <p>
	 * Typically a WorkflowBundleReader should be able to recognise the same
	 * types as those listed in {@link #getMediaTypes()}, but this is no
	 * requirement. A WorkflowBundleReader could also recognise types not listed
	 * in its {@link #getMediaTypes()}.
	 * 
	 * @param firstBytes
	 *            The initial bytes, at least 512 bytes long unless the resource
	 *            is smaller.
	 * @return The recognised media type, or <code>null</code> if the bytes were
	 *         ambiguous or unknown.
	 */
	String guessMediaTypeForSignature(byte[] firstBytes);
}
