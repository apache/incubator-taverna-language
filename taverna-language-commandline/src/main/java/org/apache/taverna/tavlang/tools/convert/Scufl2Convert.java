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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WriterException;
import org.apache.taverna.tavlang.tools.Tools.ConvertionTools;


/*
 * Converts 
 * 	.t2flow --> .wfbundle
 * 	.t2flow --> .structure
 * 	.wfbundle --> .structure
 *  .wfbundle/ .t2flow -> .iwir
 * two constructors.
 * Scufl2Convert(List<String> list, String out) --> will save the converted files in 'out folder or a directory named /converted in the same folder.
 * Scufl2Convert(String in, String out) --> Will convert all the files in the 'in' folder and save them in 'out' folder --> -r must be true.
 * 
 * */
public class Scufl2Convert{
	
	private ConvertionTools t;
	private String MEDIA_TYPE;
	private String input;
	private String output;
	private String type;
	private List<String> filesList;
	
	public Scufl2Convert(String type, List<String> files, String out){
		this.filesList = files;
		this.output = out;
		this.type = type.equals("wfdesc")?".wfdesc.ttl":"."+type;
		this.MEDIA_TYPE = ConvertionTools.valueOf(type).getMediaType(t);
		this.convert();
	}
	
	//When recursive case is on....
	public Scufl2Convert(String type, String in, String out){
		this.input = in;
		this.output = out;
		this.type = type.equals("wfdesc")?".wfdesc.ttl":"."+type;
		this.MEDIA_TYPE = ConvertionTools.valueOf(type).getMediaType(t);	//Determine the writer media type
		
		this.createdir();
	}
	
	//Create the dir if not exists
	public void createdir(){
		if(output == null){
			File outFile = new File(this.input, "converted");
			try {
				FileUtils.forceMkdir(outFile);
				this.output = outFile.getAbsolutePath();
			} catch (IOException e1) {
				System.err.println("Error: The directory cannot be created...!!!!");
				e1.printStackTrace();
			}
		}else{
			File outFile = new File(this.output);
			try {
				FileUtils.forceMkdir(outFile);
				
			} catch (IOException e1) {
				System.err.println("Error: The directory cannot be created...!!!!");
				e1.printStackTrace();
			}
		}
		this.rec_convert(this.input);
	}
	
	//Convert the given file. Return in case of an exception.
	public boolean convert(){
		
		boolean check = false;
		// If the output folder is given, save the converted files in to that folder.
		 
		if(this.filesList.size()>0 && this.output != null){
			File outFile = new File(this.output);
			try {
				FileUtils.forceMkdir(outFile);
			} catch (IOException e1) {
				System.err.println("Error: The directory cannot be created...!!!");
			}
			for(String file : this.filesList){
				File t2File = new File(file);
				
				convertFile(t2File, outFile);
				
			}
			
		}
		
		 /* If the output file is not given, save the converted files in 
		  *  '/converted' folder.
		  */
		 
		else if(this.filesList.size()>0 && this.output == null){
			for(String file : this.filesList){
				File t2File = new File(file);
				
				File outFile = new File(t2File.getParentFile(), "converted");
				try {
					FileUtils.forceMkdir(outFile);
				} catch (IOException e1) {
					System.err.println("Error: The directory cannot be created...!!!");
				}
				
				convertFile(t2File, outFile);
				
			}
		}else{
			System.err.println("Error: Argument mismatch");
			check = false;
		}
		
		return check;
	}
	
	//Convert the files in a given directory and save the converted files in to specified dir or /converted folder.
	//Recursive conversion
	public void rec_convert(String dir){
			
			File parent = new File(this.input);
			if(!parent.exists()){
				System.err.println("Error: Input directory not found");
			}else{
				for(File file : parent.listFiles()){
					if(!file.isDirectory())
					{
						File outFile = new File(this.output);
						convertFile(file, outFile);
					}
				}
			}
	}
	
	//Convert the file
	public void convertFile(File t2File, File outFile){
		
		//Check weather the input files are in valid format...!!!
		String ext = FilenameUtils.getExtension(t2File.getName());
		if(!ext.equalsIgnoreCase("t2flow")&&!ext.equalsIgnoreCase("wfbundle")){
			System.err.println("Error: Invalid input file format...!!!");
			return;
		}
		
		WorkflowBundleIO wfbio = new WorkflowBundleIO();
		String filename = t2File.getName();
		filename = filename.replaceFirst("\\..*", this.type);
		File scufl2File = new File(outFile.getAbsolutePath(), filename);
		
		WorkflowBundle wfBundle;
		try {
			wfBundle = wfbio.readBundle(t2File, null);// null --> will guess the media type for reading.
			
//			if(this.type.equals(".iwir")){
//				IwirWriter iww = new IwirWriter();
//				iww.writeBundle(wfBundle, scufl2File, this.MEDIA_TYPE);
//			}else
			
			if(this.type.equals(".json")){
				ToJson toJ = new ToJson();
				toJ.convert(t2File, outFile);
			}
			else{
				wfbio.writeBundle(wfBundle, scufl2File, this.MEDIA_TYPE);
			}
			System.out.println(scufl2File.getPath() + " is created.");
		}catch (ReaderException e){
			System.err.println("Error: Connot read the file");
		}catch(IOException e){
			System.err.println("Error: File not found");
		}catch(WriterException e) {
			System.err.println("Error: Cannot write to the file");
//			e.printStackTrace();
		}
	}

	
}
