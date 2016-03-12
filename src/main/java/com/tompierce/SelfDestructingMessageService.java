package com.tompierce;

import static spark.Spark.*;

public class SelfDestructingMessageService {
	
	private static final String API_VERSION = "0.0.1";
	
    public static void main(String[] args) {
        
    	get("/", (req, res) -> {
        	res.type("text/html");
        	return "Self-destructing Message Service v" + API_VERSION 
        			+ "<br/>Tom Pierce (tom.pierce0@gmail.com)";
        });
    	
    }
}