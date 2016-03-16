package com.tompierce;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.tompierce.model.MessageResponse;
import com.tompierce.model.NewMessageResponse;

import spark.utils.IOUtils;

public class FunctionalTests {

	Gson gson = new Gson();

	@BeforeClass
	public static void setup() {
		SelfDestructingMessageService.main(new String[0]);
	}

	@Test
	public void testMessageExpiration() throws UnirestException, IOException, InterruptedException {

		final String testMessage = "This is a test message";

		TestResponse postResponse = postMessage(testMessage, "3");
		assertEquals(200, postResponse.getStatusCode());

		String messageId = postResponse.getMessage();

		TestResponse getJSONResponse = getMessageJSON(messageId);
		assertEquals(200, getJSONResponse.getStatusCode());
		assertEquals(getJSONResponse.getMessage(), testMessage);

		TestResponse getHTMLResponse = getMessageHTML(messageId);
		assertEquals(200, getHTMLResponse.getStatusCode());
		assertEquals(getHTMLResponse.getMessage(), testMessage);

		Thread.sleep(3000);

		TestResponse getExpiredJSON = getMessageJSON(messageId);
		assertEquals(410, getExpiredJSON.getStatusCode());
		assertEquals(getExpiredJSON.getMessage(), "Expired.");

		TestResponse getExpiredHTML = getMessageHTML(messageId);
		assertEquals(410, getExpiredHTML.getStatusCode());
		assertEquals(getExpiredHTML.getMessage(), "Expired.");
	}

	@Test
	public void testUnknownMessageIDShouldReturnHTTPNotFound()
			throws JsonSyntaxException, UnirestException, IOException {
		TestResponse getUnknownMessageJSONResponse = getMessageJSON("Not-an-id");
		assertEquals(404, getUnknownMessageJSONResponse.getStatusCode());

		TestResponse getUnknownMessageHTMLResponse = getMessageJSON("Not-an-id");
		assertEquals(404, getUnknownMessageHTMLResponse.getStatusCode());
	}

	private TestResponse postMessage(final String message, final String expires)
			throws UnirestException, JsonSyntaxException, IOException {
		HttpResponse<JsonNode> postResponse = Unirest.post("http://localhost:8080/message")
				.queryString("expires", expires).queryString("message", message).asJson();
		NewMessageResponse resp = gson.fromJson(IOUtils.toString(postResponse.getRawBody()), NewMessageResponse.class);

		String messageId = resp.getMessageId();

		return new TestResponse(postResponse.getStatus(), messageId);
	}

	private TestResponse getMessageJSON(final String messageId)
			throws UnirestException, JsonSyntaxException, IOException {
		HttpResponse<JsonNode> getResponse = Unirest.get("http://localhost:8080/message/" + messageId + "?json=true")
				.asJson();
		String response = IOUtils.toString(getResponse.getRawBody());
		MessageResponse returnedMessage = gson.fromJson(response, MessageResponse.class);

		return new TestResponse(getResponse.getStatus(),
				returnedMessage != null ? returnedMessage.getMessage() : response);
	}

	private TestResponse getMessageHTML(final String messageId) throws UnirestException {
		HttpResponse<String> getResponse = Unirest.get("http://localhost:8080/message/" + messageId).asString();

		return new TestResponse(getResponse.getStatus(), getResponse.getBody());
	}

	private class TestResponse {
		private final int statusCode;
		private final String message;

		public TestResponse(final int statusCode, final String message) {
			this.statusCode = statusCode;
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public int getStatusCode() {
			return statusCode;
		}
	}
}
