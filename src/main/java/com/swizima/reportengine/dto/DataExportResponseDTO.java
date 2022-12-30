package com.swizima.reportengine.dto;

import java.io.Serializable;

public class DataExportResponseDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean response;
	private String message;
	private String fileName;
	private String fileDownloadUri;

	public DataExportResponseDTO() {

	}

	public boolean isResponse() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileDownloadUri() {
		return fileDownloadUri;
	}

	public void setFileDownloadUri(String fileDownloadUri) {
		this.fileDownloadUri = fileDownloadUri;
	}

}
