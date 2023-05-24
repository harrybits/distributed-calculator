package com.github.harrybits.dcalc;

import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.Duration;

@Configuration
public class AppConfig {

	static final int TIMEOUT = 2000;

    @Bean
	public RestTemplate restTemplate() {
		DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
		factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
		RestTemplateBuilder rtb = new RestTemplateBuilder();

		return rtb.uriTemplateHandler(factory)
				.setConnectTimeout(Duration.ofMillis(TIMEOUT))
				.setReadTimeout(Duration.ofMillis(TIMEOUT))
				.build();
	}

	@Bean
	public HttpTraceRepository httpTraceRepository() {
		return new InMemoryHttpTraceRepository();
	}

}
