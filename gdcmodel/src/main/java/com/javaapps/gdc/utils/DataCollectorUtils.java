package com.javaapps.gdc.utils;

public class DataCollectorUtils {
	public static String getStackTrackElement(Throwable ex) {
		StringBuilder sb=new StringBuilder();
		sb.append(ex.getMessage()).append("\n");
		sb.append( ex.getStackTrace()[0].toString()).append("\n");
		for (StackTraceElement element : ex.getStackTrace()) {
			if (element.getClassName().contains("javaapps")) {
				sb.append( element.toString());
			}
		}
		return sb.toString();
	}
}
