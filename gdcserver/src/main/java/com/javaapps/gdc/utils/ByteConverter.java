package com.javaapps.gdc.utils;

import java.nio.ByteBuffer;

public class ByteConverter {

	public static byte[] getByteChunk(int offset, int numberOfBytes,
			final byte bytes[]) {
		byte retBytes[] = new byte[numberOfBytes];
		for (int ii = 0; ii < numberOfBytes; ii++) {
			if ((ii+offset) < bytes.length) {
				retBytes[ii] = bytes[ii + offset];
			} else {
				retBytes[ii] = 0;
			}
		}
		return retBytes;
	}

	public static byte[] getBytes(Short value) {
		byte[] returnByteArray = new byte[2];
		returnByteArray[0] = (byte) (value & 0xff);
		returnByteArray[1] = (byte) ((value >>> 8) & 0xff);
		return returnByteArray;
	}

	public static byte[] getBytes(Integer value) {
		byte[] returnByteArray = new byte[4];
		returnByteArray[0] = (byte) (value & 0xff);
		returnByteArray[1] = (byte) ((value >>> 8) & 0xff);
		returnByteArray[2] = (byte) ((value >>> 16) & 0xff);
		returnByteArray[3] = (byte) ((value >>> 24) & 0xff);
		return returnByteArray;

	}

	public static Long getUnsignedInteger(int offset, int numberOfBytes,
			final byte inBytes[]) {
		if (numberOfBytes > 4) {
			throw new RuntimeException(
					"Cannot convert numbers of more than 4 bytes");
		}
		Long retValue = 0l;
		byte bytes[] = getByteChunk(offset, numberOfBytes, inBytes);
		// System.out.println(ByteConverter.convertByteArrayToBinaryString(bytes));
		for (int jj = (bytes.length - 1); jj >= 0; jj--) {
			retValue = retValue << 8;
			retValue += (byte) bytes[jj] & 0xFF;
		}
		return retValue;
	}

	public static Integer getSignedInteger(int offset, int numberOfBytes,
			final byte inBytes[]) {
		if (numberOfBytes > 4) {
			throw new RuntimeException(
					"Cannot convert numbers of more than 4 bytes");
		}
		Integer retValue = 0;
		byte bytes[] = getByteChunk(offset, numberOfBytes, inBytes);
		// System.out.println(ByteConverter.convertByteArrayToBinaryString(bytes));
		for (int jj = (bytes.length - 1); jj >= 0; jj--) {
			retValue = retValue << 8;
			retValue += (byte) bytes[jj] & 0xFF;
		}
		if (bytes.length == 2) {
			retValue = (int) retValue.shortValue();
		}
		return retValue;
	}

	public static String convertByteArrayToBinaryString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int ii = bytes.length - 1; ii >= 0; ii--) {
			int testValue = 128;
			for (int jj = 0; jj < 8; jj++) {
				if ((bytes[ii] & testValue) > 0) {
					sb.append("1");
				} else {
					sb.append("0");
				}
				testValue = testValue >> 1;
			}
		}
		return sb.toString();
	}

	public static void main(String args[]) {
		for (int ii = -50; ii < 50; ii++) {
			System.out.println(ii + "   "
					+ convertByteArrayToBinaryString(getBytes(ii)));
		}
	}
}