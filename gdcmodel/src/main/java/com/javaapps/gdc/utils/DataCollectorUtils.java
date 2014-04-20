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
	
	public static String getNormalizedString(String inputStr)
	{
		if ( inputStr == null){
			return null;
		}
		StringBuilder sb=new StringBuilder();
		for (int  ii=0;ii<inputStr.length();ii++){
			char ch=inputStr.charAt(ii);
			if ( Character.isLetterOrDigit(ch)){
				sb.append(ch);
			}
		}
		return sb.toString().trim();
	}

}
