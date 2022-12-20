package com.swizima.reportengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.swizima.reportengine.service.FileStorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({ FileStorageProperties.class })
@EnableAutoConfiguration
public class ReportEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportEngineApplication.class, args);
	}

}
