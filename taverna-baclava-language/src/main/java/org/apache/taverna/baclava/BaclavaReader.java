package org.apache.taverna.baclava;

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


import java.io.Reader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


public class BaclavaReader {
	
	private static final JAXBContext jaxbContext = initContext();
	
	private static JAXBContext initContext() {
        try {
			return JAXBContext.newInstance("org.apache.taverna.baclava");
		} catch (JAXBException e) {
			return null;
		}
    }

	public static DataThingMapType readBaclava(Reader r) throws JAXBException {
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		JAXBElement<?> jb = (JAXBElement<?>) unmarshaller.unmarshal(r);
		return (DataThingMapType) jb.getValue();
	}
}
