package com.tompierce.data.memory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Duration;

import org.junit.Test;

import com.tompierce.data.SelfDestructingMessageStore;

public class InMemorySelfDestructingMessageStoreTest {
	private static final String EXPIRED = "EXPIRED";

	@Test
	public void testCreatingAStore() {
		SelfDestructingMessageStore<String, String> store = new InMemorySelfDestructingMessageStore<String>(EXPIRED);
		assertNotNull(store);
	}
	
	@Test
	public void testPuttingAndGettingShouldReturnCorrectMessage() {
		SelfDestructingMessageStore<String, String> store = new InMemorySelfDestructingMessageStore<String>(EXPIRED);
		for (int i = 0; i < 10; ++i) {
			String id = store.put("Test message " + i, Duration.ofSeconds(2));
			assertNotNull(id);
			assertEquals("Test message " + i, store.get(id));
		}
	}
	

}
