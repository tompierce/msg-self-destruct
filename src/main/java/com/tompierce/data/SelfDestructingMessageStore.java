package com.tompierce.data;

import java.time.Duration;

public interface SelfDestructingMessageStore <K, V> {
    K put(V value, Duration expiresIn);
    V get(K key);
}