package com.example.dms.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FolderUtils {
	
	public static boolean validateFolderPath(String path) {
		Pattern p = Pattern.compile(Constants.FOLDER_PATH_REGEX);
		Matcher m = p.matcher(path);
		return m.matches();
	}

	public static String getParentFolderPath(String path) {
		int i = path.lastIndexOf("/");
		return i == 0 ? "/" : path.substring(0, i);
	}
	
	public static boolean compareLevels(String oldPath, String newPath) {
		return StringUtils.extractNumOfChars(newPath, '/') == StringUtils.extractNumOfChars(oldPath, '/');
	}
	
	public static boolean isSameParentFolder(String oldPath, String newPath) {
		return FolderUtils.getParentFolderPath(oldPath).equals(FolderUtils.getParentFolderPath(newPath));
	}

}
