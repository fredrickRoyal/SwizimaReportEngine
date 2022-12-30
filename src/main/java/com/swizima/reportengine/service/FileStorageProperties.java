package com.swizima.reportengine.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {

	private String uploadDir;

	private String exportDir;

	private String reporttemplatesDir;

	private String reportoutputDir;

	private String dowloadUrl;

	public String getUploadDir() {
		return uploadDir;
	}

	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}

	public String getExportDir() {
		return exportDir;
	}

	public void setExportDir(String exportDir) {
		this.exportDir = exportDir;
	}

	public String getReporttemplatesDir() {
		return reporttemplatesDir;
	}

	public void setReporttemplatesDir(String reporttemplatesDir) {
		this.reporttemplatesDir = reporttemplatesDir;
	}

	public String getReportoutputDir() {
		return reportoutputDir;
	}

	public void setReportoutputDir(String reportoutputDir) {
		this.reportoutputDir = reportoutputDir;
	}

	public String getDowloadUrl() {
		return dowloadUrl;
	}

	public void setDowloadUrl(String dowloadUrl) {
		this.dowloadUrl = dowloadUrl;
	}
	
	
}
