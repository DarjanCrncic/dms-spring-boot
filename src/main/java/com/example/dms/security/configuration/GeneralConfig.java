package com.example.dms.security.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class GeneralConfig {

	@Bean
	public TaskExecutor taskExecutor(){
		return new SimpleAsyncTaskExecutor();
	}
}
