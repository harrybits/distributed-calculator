package com.github.harrybits.dcalc;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;


class AppTests {

	private static ConfigurableApplicationContext context;

	private static RestTemplate rt;

	@BeforeAll
	static void setup() {
		String[] args = {
				"--spring.config.location=src/test/resources/application-test.properties",
				"--ops.can.do.all=true",
				"--logging.level.root=ERROR"
						};

		// Roughly the same as App.main()
		context = new SpringApplicationBuilder()
				.sources(App.class)
				.properties(Properties.defaults)
				.bannerMode(Banner.Mode.OFF)
				.run(args);
		App.context = context;

		rt = new RestTemplate();
	}

	@AfterAll
	static void teardown() {
		context.stop();
	}

	@Test
	void contextLoads() {
		assertTrue(context.getEnvironment().containsProperty(Properties.CAN_ADD));
		assertTrue(context.getEnvironment().containsProperty(Properties.CAN_DIVIDE));
	}

	@ParameterizedTest
	@MethodSource("com.github.harrybits.dcalc.TestExpressions#getStream")
	void webCalculatorWithRestTemplate(String expression, Double expectation) {
		ResponseEntity<String> response = rt.postForEntity("http://localhost/calc", expression, String.class);
		assertEquals(expectation.toString(), response.getBody());
	}

}
