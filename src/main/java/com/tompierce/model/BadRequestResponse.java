package com.tompierce.model;

public class BadRequestResponse {
	private final String error;

	public BadRequestResponse() {
		error = "Bad Request";
	}
	
	public String getError() {
		return error;
	}
}
