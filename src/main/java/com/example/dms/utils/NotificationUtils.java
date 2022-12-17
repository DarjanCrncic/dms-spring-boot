package com.example.dms.utils;

public class NotificationUtils {

	public static String buildMessage(String objectName, String parentFolderName, TypeEnum type, ActionEnum action) {
		String objectType = StringUtils.capitalize(getType(type));

		switch (action) {
			case CREATE:
				return objectType + " " + objectName + " was created in " + parentFolderName + " folder.";
			case UPDATE:
				return objectType + " " + objectName + " has been updated.";
			case DELETE:
				return objectType + " " + objectName + " has been deleted.";
			case ADMINISTRATE:
				return objectType + " " + objectName + " was administrated.";
			default:
				return null;
		}
	}

	public static String getType(TypeEnum type) {
		switch (type) {
			case DOCUMENT:
				return "document";
			case FOLDER:
				return "folder";
			default:
				return "object";
		}
	}
}
