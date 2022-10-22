package com.example.dms.services;

import com.example.dms.utils.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilsTest {

	@Test
	void testSnakeToCammelConversion() {
		assertEquals("objectName", StringUtils.snakeToCamel("object_name"));
		assertEquals("objectNameName", StringUtils.snakeToCamel("object_name_name"));
		assertEquals("objectName", StringUtils.snakeToCamel("Object_Name"));
		assertEquals("objectName", StringUtils.snakeToCamel("OBJECT_NAME"));
		assertEquals("objectName", StringUtils.snakeToCamel("objectName"));
	}
}
