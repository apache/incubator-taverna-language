package org.apache.tavlang.commandline.tools.convert;

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
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.io.WriterException;


/*
 * Converts .t2flow workflows into .workflowbundle format.
 * two constructors.
 * ToWfbundle(List<String> list, String out) --> will save the converted files in 'out folder or a directory named /converted in the same folder.
 * ToWfbundle(String in, String out) --> Will convert all the files in the 'in' folder and save them in 'out' folder --> -r must be true.
 * 
 * */
public class ToWfbundle implements Runnable{
	
	private String MEDIA_TYPE = "application/vnd.taverna.scufl2.workflow-bundle";
	private String input;
	private String output;
	private List<String> filesList;
	
	public ToWfbundle(List<String> files, String out){
		this.filesList = files;
		this.output = out;
		this.convert();
	}
	
	//When recursive case is on....
	public ToWfbundle(String in, String out){
		this.input = in;
		this.output = out;
		
		if(output == null){
			File outFile = new File(this.input, "converted");
			try {
				FileUtils.forceMkdir(outFile);
				this.output = outFile.getAbsolutePath();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.err.println("Error creating the directory...!!!!");
				e1.printStackTrace();
			}
		}else{
			File outFile = new File(this.input);
			try {
				FileUtils.forceMkdir(outFile);
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.err.println("Error creating the directory...!!!!");
				e1.printStackTrace();
			}
		}
		this.run();
	}
	
	//Convert the given file. Return in case of an exception.
	public void convert(){
		WorkflowBundleIO wfbio = new WorkflowBundleIO();
		
		// If the output folder is given, save the converted files in to that folder.
		 
		if(this.filesList.size()>0 && this.output != null){
			File outFile = new File(this.output);
			try {
				FileUtils.forceMkdir(outFile);
			} catch (IOException e1) {
				System.err.println("Error creating the directory...!!!");
			}
			for(String file : this.filesList){
				File t2File = new File(file);
				
				String filename = t2File.getName();
				filename = filename.replaceFirst("\\..*", ".wfbundle");			
				File scufl2File = new File(outFile.getAbsolutePath(), filename);
				
				WorkflowBundle wfBundle;
				try {
					wfBundle = wfbio.readBundle(t2File, "application/vnd.taverna.t2flow+xml");
					wfbio.writeBundle(wfBundle, scufl2File, this.MEDIA_TYPE);
					System.out.println(scufl2File.getPath() + " is created.");
					
					//Exceptions
				
				} catch (ReaderException e){
					System.err.println("Error reading the file");
				}catch(IOException e){
					System.err.println("Error reading the file");
				}catch(WriterException e) {
					System.err.println("Error writing the file");
				}	
			}
		}
		
		 /* If the output file is not given, save the converted files in 
		  *  '/converted' folder.
		  */
		 
		if(this.filesList.size()>0 && this.output == null){
			for(String file : this.filesList){
				File t2File = new File(file);
				
				File out = new File(t2File.getParentFile(), "converted");
				try {
					FileUtils.forceMkdir(out);
				} catch (IOException e1) {
					System.err.println("Error creating the directory...!!!");
				}
				
				String filename = t2File.getName();
				filename = filename.replaceFirst("\\..*", ".wfbundle");			
				File scufl2File = new File(out.getAbsolutePath(), filename);
				
				WorkflowBundle wfBundle;
				try {
					wfBundle = wfbio.readBundle(t2File,
							"application/vnd.taverna.t2flow+xml");
					wfbio.writeBundle(wfBundle, scufl2File, this.MEDIA_TYPE);
					System.out.println(scufl2File.getPath() + " is created.");
				}catch (ReaderException e){
					System.err.println("Error reading the file");
				}catch(IOException e){
					System.err.println("Error reading the file");
				}catch(WriterException e) {
					System.err.println("Error writing the file");
				}
				
			}
		}
	}
	
	//Convert the files in a given directory and save the converted files in to specified dir or /converted folder.
	//Recursive conversion
	public void rec_convert(String dir){
					
			File parent = new File(this.input);
			if(!parent.exists()){
				System.err.println("Input directory not found");
			}else{
				for(File file : parent.listFiles()){
					if(file.isDirectory())
						rec_convert(file.getAbsolutePath());
					else{
						recConvert(file);
					}
				}
			}
			
			
	}
	public void recConvert(File t2File){

//		File t2File = new File(file);
		
		String filename = t2File.getName();
		System.out.println(t2File.getAbsolutePath());
		filename = filename.replaceFirst("\\..*", ".wfbundle");			
		File scufl2File = new File(this.output, filename);
		
		WorkflowBundleIO wfbio = new WorkflowBundleIO();
		
		WorkflowBundle wfBundle;
		try {
			wfBundle = wfbio.readBundle(t2File, "application/vnd.taverna.t2flow+xml");
			wfbio.writeBundle(wfBundle, scufl2File, this.MEDIA_TYPE);
			System.out.println(scufl2File.getPath() + " is created.");
			
			//Exceptions
		
		} catch (ReaderException e){
			System.err.println("Error reading the file");
		}catch(IOException e){
			System.err.println("Error reading the file");
		}catch(WriterException e) {
			System.err.println("Error writing the file");
		}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		rec_convert(this.input);
	}
}
