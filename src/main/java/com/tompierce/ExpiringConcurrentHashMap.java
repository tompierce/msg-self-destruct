package com.tompierce;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExpiringConcurrentHashMap<K, V> implements Map<K, V> {

	private ScheduledExecutorService scheduler;

	private Map<K, ValueExpiredWrapper<V>> map;

	private final V expiredValue;

	public ExpiringConcurrentHashMap(final V expiredValue) {
		this.expiredValue = expiredValue;
		map = new ConcurrentHashMap<K, ValueExpiredWrapper<V>>();
		scheduler = Executors.newScheduledThreadPool(1);
	}

	public void put(K key, V value, Duration expiresIn) {

		map.put(key, new ValueExpiredWrapper<V>(value));

		scheduler.schedule(() -> {
			ValueExpiredWrapper<V> wrappedValue = map.get(key);
			if (wrappedValue != null) {
				wrappedValue.setExpired(true);
				wrappedValue.setValue(null);
			}
		}, expiresIn.abs().getSeconds(), TimeUnit.SECONDS);

	}

	@Override
	public V get(Object key) {
		ValueExpiredWrapper<V> wrappedValue = map.get(key);
		if (wrappedValue != null) {
			if (wrappedValue.isExpired()) {
				return expiredValue;
			}
			return wrappedValue.getValue();
		}
		return null;
	}

	@Override
	public V remove(Object key) {
		ValueExpiredWrapper<V> wrappedValue = map.remove(key);
		if (wrappedValue.isExpired()) {
			throw new UnsupportedOperationException("cannot remove expired value");
		}
		return wrappedValue.getValue();
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V put(K key, V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

}
