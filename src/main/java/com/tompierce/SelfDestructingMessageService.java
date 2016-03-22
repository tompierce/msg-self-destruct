package com.tompierce;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.port;

import java.time.Duration;

import com.google.gson.Gson;
import com.tompierce.data.SelfDestructingMessageStore;
import com.tompierce.data.memory.InMemorySelfDestructingMessageStore;
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
	private static SelfDestructingMessageStore<String, String> store = 
			new InMemorySelfDestructingMessageStore<String>(EXPIRED_MESSAGE);

	public static void main(String[] args) {
		port(Integer.parseInt(System.getProperty("server.port", "8080")));

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

			String id = store.put(messageParam, Duration.ofSeconds(Integer.parseInt(expiresParam)));

			return new NewMessageResponse(id);
		}, gson::toJson);

		get("/message/:messageId", (req, res) -> {
			final String messageId = req.params(":messageId");
			final String message = store.get(messageId);

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

}