package com.tompierce;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.port;

import java.time.Duration;

import com.google.gson.Gson;
import com.tompierce.model.APIVersion;
import com.tompierce.model.BadRequestResponse;
import com.tompierce.model.MessageResponse;
import com.tompierce.model.NewMessageResponse;
import com.tompierce.model.NotFoundResponse;

import spark.Request;

public class SelfDestructingMessageService {

	private static final String API_VERSION = "0.0.1";
	private static final String EXPIRED_MESSAGE = "Expired.";
	private static Gson gson = new Gson();
	private static ExpiringConcurrentHashMap<String, String> messageMap = new ExpiringConcurrentHashMap<String, String>(
			EXPIRED_MESSAGE);
	private static MessageIDGenerator idGenerator = new MessageIDGenerator();

	public static void main(String[] args) {

		String port = System.getProperty("port");
		if (port == null) {
			port = "8080";
		}

		port(Integer.parseInt(port));

		get("/", (req, res) -> {
			res.type("text/html");
			return "Self-destructing Message Service v" + API_VERSION + "<br/>Tom Pierce (tom.pierce0@gmail.com)";
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
			String messageId = req.params(":messageId");
			String message = messageMap.get(messageId);

			if (message == null) {
				res.status(404);
				if (shouldReturnJson(req)) {
					return gson.toJson(new NotFoundResponse());
				}
				return "Not Found";
			}

			res.status(message.equals(EXPIRED_MESSAGE) ? 410 : 200);
			if (shouldReturnJson(req)) {
				res.type("application/json");
				return gson.toJson(new MessageResponse(message, false));
			} else {
				return message;
			}

		});

	}

	private static boolean shouldReturnJson(Request req) {
		String jsonParam = req.queryParams("json");

		if (jsonParam != null) {
			if (!jsonParam.equals("false")) {
				return true;
			}
		}
		return false;
	}

	private static String getUniqueMessageId() {
		String newMessageId = idGenerator.nextID();
		while (messageMap.containsKey(newMessageId)) {
			newMessageId = idGenerator.nextID();
		}
		return newMessageId;

	}
}