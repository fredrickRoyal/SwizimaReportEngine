package com.swizima.reportengine.dto;

import java.io.Serializable;
import java.util.List;

public class ReportMergeRequestDTO implements Serializable{

	private String template;
	private String jsonString;
	List<String> files;
	private String outputFile;
	private String refId;
	
	public ReportMergeRequestDTO() {
		
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}

	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}
	
	
}
