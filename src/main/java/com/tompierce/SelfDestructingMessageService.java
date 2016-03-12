package com.tompierce;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.tompierce.model.APIVersion;
import com.tompierce.model.NewMessageResponse;

public class SelfDestructingMessageService {
	
	private static final String API_VERSION = "0.0.1";
	private static Gson gson = new Gson();
	private static Map<String, String> messageMap = new ConcurrentHashMap<String, String>();
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
    		String newMessageId = idGenerator.nextID();
    		while(messageMap.containsKey(newMessageId)) {
    			newMessageId = idGenerator.nextID();
    		}
    		
    		messageMap.put(newMessageId, req.body());
    	
    		return new NewMessageResponse(newMessageId);
    	}, gson::toJson);
    	
    	get("/message/:messageId", (req, res) -> {
    		String messageId = req.params(":messageId");
    		if (messageMap.containsKey(messageId)) {
    			return messageMap.get(req.params(":name"));    			
    		} else {
    			return "";
    		}
    	});
    }
}