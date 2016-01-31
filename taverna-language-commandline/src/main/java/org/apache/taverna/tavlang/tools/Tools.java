package org.apache.taverna.tavlang.tools;

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



public class Tools {
	
	public static enum ConvertionTools {
		wfbundle{
			public String mediaType = "application/vnd.taverna.scufl2.workflow-bundle";
			

			@Override
			public String getMediaType(ConvertionTools t) {
				return this.mediaType;
			}
		},
		json{
			public String mediaType = "application/ld+json";


			@Override
			public String getMediaType(ConvertionTools t) {
				return mediaType;
			}
		},
		wfdesc{
			public String mediaType = "text/vnd.wf4ever.wfdesc+turtle";


			@Override
			public String getMediaType(ConvertionTools t) {
				// TODO Auto-generated method stub
//				System.out.println(mediaType);
				return mediaType;
			}
		},
		robundle{

			@Override
			public String getMediaType(ConvertionTools t) {
				return null;
			}
		
		},
		structure{
			public String mediaType = "text/vnd.taverna.scufl2.structure";
			
			@Override
			public String getMediaType(ConvertionTools t) {
				return mediaType;
			}
		},
		
		iwir{
				public String mediaType = "application/vnd.shiwa.iwir+xml";
				
				@Override
				public String getMediaType(ConvertionTools t) {
					return mediaType;
				}
		};
		
		ConvertionTools(){}
		
		public abstract String getMediaType(ConvertionTools t);
		
	}
	
}
