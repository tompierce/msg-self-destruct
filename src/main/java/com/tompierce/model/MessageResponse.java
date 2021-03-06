package com.tompierce.model;

public class MessageResponse {

	private final String message;
	private final boolean expired;

	public MessageResponse(final String message, final boolean expired) {
		this.message = message;
		this.expired = expired;
	}

	public String getMessage() {
		return message;
	}

	public boolean isExpired() {
		return expired;
	}

}
