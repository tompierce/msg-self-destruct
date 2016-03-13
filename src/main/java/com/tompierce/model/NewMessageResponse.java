package com.tompierce.model;

public class NewMessageResponse {
	private final String messageId;

	public NewMessageResponse(final String messageId) {
		this.messageId = messageId;
	}

	public String getMessageId() {
		return messageId;
	}

}
