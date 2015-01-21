package org.apache.taverna.databundle;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ErrorDocument {
	private List<Path> causedBy = new ArrayList<>();
	private String message = "";
	private String trace = "";

	public List<Path> getCausedBy() {
		return causedBy;
	}

	public String getMessage() {
		return message;
	}

	public String getTrace() {
		return trace;
	}

	public void setCausedBy(List<Path> causedBy) {
		this.causedBy.clear();
		if (causedBy != null)
			this.causedBy.addAll(causedBy);
	}

	public void setMessage(String message) {
		if (message == null)
			message = "";
		this.message = message;
	}

	public void setTrace(String trace) {
		if (trace == null)
			trace = "";
		this.trace = trace;
	}
}
