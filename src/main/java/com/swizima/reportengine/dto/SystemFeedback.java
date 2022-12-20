package com.swizima.reportengine.dto;

import java.io.Serializable;

public class SystemFeedback implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String message;
	private boolean response;

	public SystemFeedback() {
		this.message = "An error occured while initiating transaction. Please again";
		this.response = false;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isResponse() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

}
