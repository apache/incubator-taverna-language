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

public class KeyDerivation {
	private String name;
	private String salt;
	private int iterationCount = 0;

	public KeyDerivation() {
	}

	public KeyDerivation(String name, String salt, int iterationCount) {
		this.name = name;
		this.salt = salt;
		this.iterationCount = iterationCount;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getSalt() {
		return salt;
	}

	public void setIterationCount(int iterationCount) {
		this.iterationCount = iterationCount;
	}

	public int getIterationCount() {
		return iterationCount;
	}
}
