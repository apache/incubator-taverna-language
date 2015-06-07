package org.apache.commandline.test;

import org.apache.taverna.commandline.CommandLineTool;
import org.junit.Assert;
import org.junit.Test;

public class CommandLineTest {
	CommandLineTool commandLineTool = new CommandLineTool();
	
	@Test
	public void test(){
//		Assert
		commandLineTool.parse();
		commandLineTool.parse("version");
		commandLineTool.parse("help");
		commandLineTool.parse("help", "convert");
		commandLineTool.parse("help", "inspect");
		commandLineTool.parse("help", "validate");
		commandLineTool.parse("help", "help");
		
		commandLineTool.parse("convert","-m", "-r", "-wfdesc", "-o", "/files/dir", "-i", "/files0/dir");
//		commandLineTool.parse();
//		commandLineTool.parse();
//		commandLineTool.parse();
//		commandLineTool.parse();
		
	}
	

}
