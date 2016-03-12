package com.tompierce;

import java.security.SecureRandom;
import java.math.BigInteger;

public final class MessageIDGenerator {
  private SecureRandom random = new SecureRandom();

  public String nextID() {
    return new BigInteger(130, random).toString(32);
  }
}