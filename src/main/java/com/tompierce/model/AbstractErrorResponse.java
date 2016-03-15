package com.tompierce.model;

public abstract class AbstractErrorResponse {
	private String error;

	public AbstractErrorResponse() {
		error = "Error";
	}
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
