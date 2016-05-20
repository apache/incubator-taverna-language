/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ************************************************************************/
/*  This file is derived from ODFDOM 0.8.6, and
 *  has been modified for Apache Taverna.
 *  (c) 2010-2014 University of Manchester
 *  (c) 2015 The Apache Software Foundation
 */
package org.apache.taverna.scufl2.ucfpackage.impl.odfdom.pkg.manifest;

import javax.activation.MimetypesFileTypeMap;

public class OdfFileEntry {
	private String mPath;
	private String mMediaType = "";
	private int mSize = -1;
	private String version = null;
	private EncryptionData _encryptionData; // The following static attributes
	public OdfFileEntry() {
	}

	public OdfFileEntry(String path, String mediaType) {
		mPath = path;
		mMediaType = (mediaType == null ? "" : mediaType);
		mSize = 0;
	}

	public OdfFileEntry(String path, String mediaType, int size) {
		mPath = path;
		mMediaType = mediaType;
		mSize = size;
	}

	public void setPath(String path) {
		mPath = path;
	}

	public String getPath() {
		return mPath;
	}

	public void setMediaType(String mediaType) {
		mMediaType = (mediaType == null ? "" : mediaType);
	}

	public String getMediaType() {
		return mMediaType;
	}

	/**
	 * Get the media type from the given file reference
	 *
	 * @param fileRef
	 *            the reference to the file the media type is questioned
	 *
	 * @return the mediaType string of the given file reference
	 */
	public static String getMediaType(String fileRef) {
		return MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(fileRef);
	}

	public void setSize(int size) {
		mSize = size;
	}

	/**
	 * get the size or -1 if not set
	 */
	public int getSize() {
		return mSize;
	}

	public void setEncryptionData(EncryptionData encryptionData) {
		_encryptionData = encryptionData;
	}

	public EncryptionData getEncryptionData() {
		return _encryptionData;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
