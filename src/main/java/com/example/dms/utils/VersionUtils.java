package com.example.dms.utils;

public class VersionUtils {

	public static String getNextVersion(String currentVersion) {
		String[] components = currentVersion.split("\\.");
		int lastDigit = Integer.parseInt(components[components.length-1]);
		components[components.length-1] = Integer.toString(lastDigit + 1);
		return String.join(".", components);
	}
}
