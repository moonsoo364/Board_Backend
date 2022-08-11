package com.cos.board.config;


public class XssUtil {
	public static String cleanXSS(String value) {
		String returnVal =value;
		returnVal = returnVal.replaceAll("script", "");
//		returnVal = returnVal.replaceAll("<", "&lt;").replace(">", "&gt;");
//		returnVal =returnVal.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
//		returnVal = returnVal.replaceAll("'", "&#39;");
//	    returnVal = returnVal.replaceAll("eval\\((.*)\\)", "");
//	    returnVal = returnVal.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
	    
//	    returnVal = returnVal.replaceAll("iframe", "");
//	    returnVal = returnVal.replaceAll("embed", "");
		return returnVal;
	}

}
