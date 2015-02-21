/**
 * 
 */
package org.apache.taverna.scufl2.translator.scufl.processorelement;
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


import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * @author alanrw
 */
public class AbstractProcessorExtensionParser extends AbstractExtensionParser {
	@Override
	public boolean canHandle(Class<?> c) {
		return c.equals(org.apache.taverna.scufl2.xml.scufl.jaxb.AbstractprocessorType.class);
	}

	@Override
	public List<URI> getAdditionalSchemas() {
		return Collections.emptyList();
	}

	@Override
	public void parseScuflObject(Object o) {
		System.err.println(this.getClass() + " is not yet implemented");
	}
}
