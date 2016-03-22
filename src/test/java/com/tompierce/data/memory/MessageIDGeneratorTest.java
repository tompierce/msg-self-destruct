package com.tompierce.data.memory;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MessageIDGeneratorTest {

	@Test
	public void testThatGeneratedIDsAreAtLeastTenDigits() {
		MessageIDGenerator generator = new MessageIDGenerator();
		assertTrue(10 < generator.nextID().length());
	}

}
