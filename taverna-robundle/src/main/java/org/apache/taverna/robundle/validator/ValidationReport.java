package org.apache.taverna.robundle.validator;

import java.util.ArrayList;

public class ValidationReport {
	
	private ArrayList<String> errorList;
	
	private ArrayList<String> infoWarnings;
	
	private ArrayList<String> warnings;
	
	public ValidationReport(){
		
	}

	public ArrayList<String> getErrorList() {
		return errorList;
	}

	public void setErrorList(ArrayList<String> errorList) {
		this.errorList = errorList;
	}

	public ArrayList<String> getInfoWarnings() {
		return infoWarnings;
	}

	public void setInfoWarnings(ArrayList<String> infoWarnings) {
		this.infoWarnings = infoWarnings;
	}

	public ArrayList<String> getWarnings() {
		return warnings;
	}

	public void setWarnings(ArrayList<String> warnings) {
		this.warnings = warnings;
	}
	

}
