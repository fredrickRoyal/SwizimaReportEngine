package com.swizima.reportengine.models;

import java.time.LocalDateTime;

import com.swizima.reportengine.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity(name = "ReportTemplates")
public class ReportTemplate extends ParentEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
 
	private String fileName; 
	private String fileSize;
	private LocalDateTime publishedDate;
	private String publishedBy;

	 
	private String description;

	private Status publishStatus;
	private String folder;
	private String refId;
	private String filePath;
	private String fileType;

	public ReportTemplate() {

	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public LocalDateTime getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(LocalDateTime publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getPublishedBy() {
		return publishedBy;
	}

	public void setPublishedBy(String publishedBy) {
		this.publishedBy = publishedBy;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Status getPublishStatus() {
		return publishStatus;
	}

	public void setPublishStatus(Status publishStatus) {
		this.publishStatus = publishStatus;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

}
