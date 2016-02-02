package org.apache.taverna.tavlang.tools.convert;

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


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.taverna.robundle.Bundle;
import org.apache.taverna.robundle.Bundles;

public class ToRobundle{

	public ToRobundle(List<String> files, String out) throws Exception{
		
		Logger logger = Logger.getLogger("");
		//logger.setLevel(Level.FINER);
		ConsoleHandler console = new ConsoleHandler();
		console.setLevel(Level.FINEST);
		logger.addHandler(console);
		Logger.getLogger("org.researchobject").setLevel(Level.FINEST);
		
		for(String f : files){
			Path file = Paths.get(f);
			convert(file);
		}
	}
	
	//Recursive conversion
	public ToRobundle(String type, String in, String out) {
		
	}
	
	public void convert(Path file) throws IOException{
		try (Bundle bundle = Bundles.openBundle(file)) {
			
//			System.out.println(bundle.getManifest().toString());
			bundle.getManifest().writeAsJsonLD();
			bundle.getManifest().writeAsCombineManifest();
		}
	}
	
	
}
