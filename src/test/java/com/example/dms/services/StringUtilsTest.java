package com.example.dms.services;

import com.example.dms.utils.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilsTest {

	@Test
	void testSnakeToCammelConversion() {
		assertEquals("objectName", StringUtils.snakeToCammel("object_name"));
		assertEquals("objectNameName", StringUtils.snakeToCammel("object_name_name"));
		assertEquals("objectName", StringUtils.snakeToCammel("Object_Name"));
		assertEquals("objectName", StringUtils.snakeToCammel("OBJECT_NAME"));
		assertEquals("objectName", StringUtils.snakeToCammel("objectName"));
	}
}
