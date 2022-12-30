package com.swizima.reportengine.dto;

import java.io.Serializable;
import java.util.List;

public class DataExportRequestDTO implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String json;
	private List<String> columns;
	private String fileName;
	private String refId;
	
	public DataExportRequestDTO() {
		
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}
	
	

}
