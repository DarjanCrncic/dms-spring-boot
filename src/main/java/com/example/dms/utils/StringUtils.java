package com.example.dms.utils;

public class StringUtils {

	private StringUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static String snakeToCammel(String text) {
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
	
	public static long extractNumOfChars(String word, char c) {
		return word.chars().filter(a -> a == c).count();
	}
}
