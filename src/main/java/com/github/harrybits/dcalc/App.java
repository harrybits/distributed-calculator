package com.github.harrybits.dcalc;

import nl.arothuis.antlr4calculator.core.parser.CalculatorLexer;
import nl.arothuis.antlr4calculator.core.parser.CalculatorParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = {"com.github.harrybits.dcalc"})
@RestController
public class App {

	private static Logger LOGGER = LoggerFactory.getLogger(App.class);

	private WebCalculatorVisitor webCalc;

	public static ConfigurableApplicationContext context;

	public App(RestTemplate rt, Environment env) {
		this.webCalc = new WebCalculatorVisitor(rt, env);
	}

	public static void main(String[] args) {
		context = new SpringApplicationBuilder()
				.sources(App.class)
				.properties(Properties.defaults)
				.bannerMode(Banner.Mode.OFF)
				.run(args);
	}

	@PostMapping("/calc")
	public String calc(@RequestBody String exp) {

		LOGGER.debug("Expression received {}", exp);

		CharStream chars = CharStreams.fromString(exp);
		Lexer lexer = new CalculatorLexer(chars);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CalculatorParser parser = new CalculatorParser(tokens);
		ParseTree tree = parser.start();


		Double answer = webCalc.visit(tree);
		LOGGER.debug("Returning {}", answer);
		return Double.toString(answer);
	}

	// Leaving this in for a simple sanity check.
	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}
}
