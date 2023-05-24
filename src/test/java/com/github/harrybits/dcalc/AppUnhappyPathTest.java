package com.github.harrybits.dcalc;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppUnhappyPathTest {

	private static ConfigurableApplicationContext context;
	private static RestTemplate rt;

	@BeforeAll
	static void setup() {
		// Within this file, we are testing unhappy paths (e.g. missing downstream dependency) with the default
		// 	config, so we don't load settings for a local dev environment.
		String[] args = {
				"--logging.level.root=ERROR",		// cleaner test output
				"--logging.level.com.github.harrybits.dcalc.WebCalculatorVisitor=ERROR" // override default TRACE behavior
						};

		// Roughly the same as App.main()
		context = new SpringApplicationBuilder()
				.sources(App.class)
				.properties(Properties.defaults)
				.bannerMode(Banner.Mode.OFF)
				.run(args);
		App.context = context;

		rt = (new RestTemplateBuilder())
				.errorHandler(new RestTemplateResponseErrorHandler()) // Turn off ALL exceptions
				.build();
	}

	@AfterAll
	static void teardown() {
		context.stop();
	}

	// Error handler for RestTemplate that suppresses all 4xx and 5xx responses to simplify testing.
	public static class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
		@Override
		public boolean hasError(ClientHttpResponse response) throws IOException {
			return false;
		}
		@Override
		public void handleError(ClientHttpResponse response) throws IOException {

		}
	}

	/*
	  These calls will be picked up by the app, but will fail since we are configured to send more downstream requests.
	*/
	@Test
	void serviceDoesNotExist() {
		ResponseEntity<String> response;

		response = rt.postForEntity("http://localhost/calc", "1+2", String.class);
		assertEquals(response.getStatusCode(), HttpStatus.FAILED_DEPENDENCY);

		response = rt.postForEntity("http://localhost/calc", "1-2", String.class);
		assertEquals(response.getStatusCode(), HttpStatus.FAILED_DEPENDENCY);

		response = rt.postForEntity("http://localhost/calc", "1*2", String.class);
		assertEquals(response.getStatusCode(), HttpStatus.FAILED_DEPENDENCY);

		response = rt.postForEntity("http://localhost/calc", "1/2", String.class);
		assertEquals(response.getStatusCode(), HttpStatus.FAILED_DEPENDENCY);

		response = rt.postForEntity("http://localhost/calc", "2^3", String.class);
		assertEquals(response.getStatusCode(), HttpStatus.FAILED_DEPENDENCY);
	}
}
