package com;

public class StrFormat {
	    public static String formatStateCode(long stateCode) throws Exception {
		if (stateCode > 99 || stateCode < 0) {
		    //throw new Exception("Invalid state code: " + stateCode + ". State Code only can be 0 - 99");
		    System.out.println("    Warn: Invalid state code: " + stateCode + ". State Code only can be 0 - 99");
		}
		String stateCodeStr = String.valueOf(stateCode);	
		if (stateCode < 10) {
		    stateCodeStr =  "0" + stateCodeStr;
		}
		return stateCodeStr;
	    }
	    
	    public static String formatCountyCode(long countyCode) throws Exception {
		if (countyCode > 999 || countyCode < 0) {
		    //throw new Exception("Invalid county code: " + countyCode + ". County Code only can be 0 - 999");
		    System.out.println("    Warn: Invalid county code: " + countyCode + ". County Code only can be 0 - 999");
		}
		String countyCodeStr = String.valueOf(countyCode);	
		if (countyCode < 10) {
		    countyCodeStr =  "00" + countyCodeStr;
		} else if (countyCode < 100) {
		    countyCodeStr =  "0" + countyCodeStr;
		}
		return countyCodeStr;
	    }
	    
	    public static String convertEmptyStringToNull(String str) {
		if (str != null && str.equals("")) {
		    return null;
		}
		return str;
	    }
	    
	    public static String convertEmptyStringToNullAndUppercase(String str) {
		if (str != null && str.equals("")) {
		    return null;
		}
		if (str != null) {
		    str = str.toUpperCase();
		}
		return str;
	    }
	    
	    public static String convertNullStringToEnptyAndUppercase(String str) {
		if (str == null) {
		    return "";
		}
		return str.toUpperCase();
	    }
}


