package com.example.dms.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	private StringUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static String snakeToCamel(String text) {
		if (!text.contains("_")) {
			return text;
		}
		
		String[] words = text.split("[\\W_]+");

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (i == 0) {
				word = word.isEmpty() ? word : word.toLowerCase();
			} else {
				word = word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
			}
			builder.append(word);
		}
		return builder.toString();
	}
	
	public static boolean isFalse(String val) {
		return val == null || val.isEmpty() || val.isBlank();
	}

	public static String dateTimeToString(LocalDateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		return dateTime.format(formatter);
	}

	public static boolean validateFolderName(String name) {
		Pattern p = Pattern.compile(Constants.FOLDER_NAME_REGEX);
		Matcher m = p.matcher(name);
		return m.matches();
	}

	public static String capitalize(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}
}
