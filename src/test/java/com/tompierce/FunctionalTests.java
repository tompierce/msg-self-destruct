package com.tompierce;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.tompierce.model.MessageResponse;
import com.tompierce.model.NewMessageResponse;

import spark.utils.IOUtils;

public class FunctionalTests {

	Gson gson = new Gson();
	
	@Test
	public void test() throws UnirestException, IOException, InterruptedException {
		SelfDestructingMessageService.main(new String[0]);
		
		String testMessage = "This is a test message.";
		
		HttpResponse<JsonNode> postResponse = Unirest.post("http://localhost:4567/message")
		  .queryString("expires", "3")
		  .queryString("message", testMessage)
		  .asJson();
		
		NewMessageResponse resp = gson.fromJson(IOUtils.toString(postResponse.getRawBody()), NewMessageResponse.class);
		
		String messageId = resp.getMessageId();
		
		assertNotNull(messageId);
		
		HttpResponse<JsonNode> getResponse = Unirest.get("http://localhost:4567/message/" + messageId + "?json=true")
				  .asJson();
		
		MessageResponse returnedMessage = gson.fromJson(IOUtils.toString(getResponse.getRawBody()), MessageResponse.class);
		
		assertEquals(returnedMessage.getMessage(), testMessage);
		
		Thread.sleep(3000);
		
		HttpResponse<JsonNode> expiredResponse = Unirest.get("http://localhost:4567/message/" + messageId + "?json=true")
				  .asJson();
		
		assertEquals(410, expiredResponse.getStatus());
		
		MessageResponse expiredMessage = gson.fromJson(IOUtils.toString(expiredResponse.getRawBody()), MessageResponse.class);
		
		assertEquals("Expired.", expiredMessage.getMessage());
		
		
	}

}
