package com.jop.address.utils;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Util class for CEP.
 * 
 * @author julianopontes
 *
 */
public final class CEPUtil {

	private static final Pattern PATTERN_STRING = Pattern.compile("[0-9]{5}-[0-9]{3}");
	private static final Pattern PATTERN_NUMBER = Pattern.compile("[0-9]{8}");
	
	private CEPUtil() {
	}

	/**
	 * Verify correct format of CEP.
	 * 
	 * @param cep
	 * @return true for valid and false for invalid
	 */
	public static boolean isCEP(String cep) {
		// Left padding used for CEP in number format beginning with 0 that was discarded.
		cep = StringUtils.leftPad(cep, 8, "0");
		
		return PATTERN_STRING.matcher(cep).matches() || PATTERN_NUMBER.matcher(cep).matches();
	}
}
