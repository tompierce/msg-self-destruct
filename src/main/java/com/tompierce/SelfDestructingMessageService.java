package com.tompierce;

import static spark.Spark.get;
import static spark.Spark.post;

import java.time.Duration;

import com.google.gson.Gson;
import com.tompierce.model.APIVersion;
import com.tompierce.model.BadRequestResponse;
import com.tompierce.model.MessageResponse;
import com.tompierce.model.NewMessageResponse;

public class SelfDestructingMessageService {
	
	private static final String API_VERSION = "0.0.1";
	private static Gson gson = new Gson();
	private static ExpiringConcurrentHashMap<String, String> messageMap = new ExpiringConcurrentHashMap<String, String>();
	private static MessageIDGenerator idGenerator = new MessageIDGenerator();
	
    public static void main(String[] args) {
        
    	get("/", (req, res) -> {
        	res.type("text/html");
        	return "Self-destructing Message Service v" + API_VERSION 
        			+ "<br/>Tom Pierce (tom.pierce0@gmail.com)";
        });
    	
    	get("/version", (req, res) -> {
    		return new APIVersion(API_VERSION);
    	}, gson::toJson);
    	
    	post("/message", (req, res) -> {
    		
    		String expiresParam = req.queryParams("expires");
    		String messageParam = req.queryParams("message");

    		if (expiresParam == null || messageParam == null) {
    			res.status(400);
    			return new BadRequestResponse();
    		}
    		
    		String newMessageId = getUniqueMessageId();
    		
    		messageMap.put(newMessageId, messageParam, Duration.ofSeconds(Integer.parseInt(expiresParam)));
    	
    		return new NewMessageResponse(newMessageId);
    	}, gson::toJson);
    	
    	get("/message/:messageId", (req, res) -> {
    		String jsonParam = req.queryParams("json");

    		String messageId = req.params(":messageId");
    		if (messageMap.containsKey(messageId)) {
    			try {
    				String message = messageMap.get(messageId);
    				
    				if (jsonParam != null) {
    					if (!jsonParam.equals("false")) {
    						res.type("application/json");
    						return gson.toJson(new MessageResponse(message, false));
    					}
    				}
    				return message;
    			} catch (ExpiredValueException e) {
    				res.status(410);
    				String expiredMessage = "Expired.";
    				if (jsonParam != null) {
    					if (!jsonParam.equals("false")) {
    						res.type("application/json");
    						return gson.toJson(new MessageResponse(expiredMessage, true));
    					}
    				}
    				return expiredMessage;
    			}
    		} else {
    			return "";
    		}
    	});
    	
    }
    
    private static String getUniqueMessageId() {
		String newMessageId = idGenerator.nextID();
		while(messageMap.containsKey(newMessageId)) {
			newMessageId = idGenerator.nextID();
		}
		return newMessageId;

    }
}