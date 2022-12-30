package com.swizima.reportengine.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;

import com.swizima.reportengine.enums.Status;

@MappedSuperclass
public class ParentEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Id
	private String id;
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;

	private Status recordStatus;

	public ParentEntity() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Status getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(Status recordStatus) {
		this.recordStatus = recordStatus;
	}

}
