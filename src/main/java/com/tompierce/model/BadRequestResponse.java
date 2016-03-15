package com.tompierce.model;

public class BadRequestResponse extends AbstractErrorResponse {

	public BadRequestResponse() {
		super();
		setError("Bad Request");
	}
	
}
