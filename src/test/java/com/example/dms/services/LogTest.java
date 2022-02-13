package com.example.dms.services;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class LogTest {
	
	@Test
	void testLogging() {
		log.debug("debug level ON");
		log.info("info level ON");
		log.warn("warn level ON");
		log.error("error level ON");
	}
 }
