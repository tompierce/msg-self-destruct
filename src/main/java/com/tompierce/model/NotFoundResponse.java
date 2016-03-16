package com.tompierce.model;

public class NotFoundResponse extends AbstractErrorResponse {

	public NotFoundResponse() {
		super();
		setError("Not Found");
	}
}
