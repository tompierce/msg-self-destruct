package com.tompierce;

public class ValueExpiredWrapper<V> {
	
	private V value;
	private boolean expired;
	
	public ValueExpiredWrapper(V value) {
		this.value = value;
		this.expired = false;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}
}