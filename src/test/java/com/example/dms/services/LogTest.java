package com.example.dms.services;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SpringBootTest
class LogTest {
	
	@Test
	void testLogging() {
		log.debug("debug level ON");
		log.info("info level ON");
		log.warn("warn level ON");
		log.error("error level ON");
	}
 }
