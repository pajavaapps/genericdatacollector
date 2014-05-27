package com.javaapps.gdc.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class ByteConverterTest {

	@Test
	public void test() {

		for (Short ii = -5; ii < 5; ii++) {
			byte bytes[] = ByteConverter.getBytes(ii);
			Integer intValue = new Integer(ii);
			assertEquals(intValue, ByteConverter.getSignedInteger(0, 2, bytes));
			long unsignedValue = ByteConverter.getUnsignedInteger(0, 2, bytes);
			if (intValue < 0) {
				assertFalse(intValue + " should not equal " + unsignedValue,
						ii == unsignedValue);
			} else {
				assertEquals(intValue.intValue(), unsignedValue);
			}
		}

		for (int ii = -5; ii < 5; ii++) {
			byte bytes[] = ByteConverter.getBytes(ii);
			assertEquals(ii, ByteConverter.getSignedInteger(0, 4, bytes)
					.intValue());
			long unsignedValue = ByteConverter.getUnsignedInteger(0, 4, bytes);
			if (ii < 0) {
				assertFalse(ii + " should not equal " + unsignedValue,
						ii == unsignedValue);
			} else {
				assertEquals(ii, unsignedValue);
			}
		}

		System.out.println("success");

	}
}
