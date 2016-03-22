package com.tompierce.data.memory;

import java.time.Duration;

import com.tompierce.data.SelfDestructingMessageStore;

public class InMemorySelfDestructingMessageStore<V> implements SelfDestructingMessageStore<String, V> {

	private final ExpiringConcurrentHashMap<String,V> map;
	private static MessageIDGenerator idGenerator = new MessageIDGenerator();

	public InMemorySelfDestructingMessageStore(final V expired) {
		this.map = new ExpiringConcurrentHashMap<String,V>(expired);
	}

	@Override
	public String put(final V value, final Duration expiresIn) {
		String id = getUniqueMessageId();
		map.put(id, value, expiresIn);
		return id;
	}

	@Override
	public V get(final String key) {
		return map.get(key);
	}

	private String getUniqueMessageId() {
		String newMessageId = idGenerator.nextID();
		while (map.containsKey(newMessageId)) {
			newMessageId = idGenerator.nextID();
		}
		return newMessageId;

	}
}
