package com.github.harrybits.dcalc;

import nl.arothuis.antlr4calculator.core.parser.CalculatorBaseVisitor;
import nl.arothuis.antlr4calculator.core.parser.CalculatorParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class WebCalculatorVisitor extends CalculatorBaseVisitor<Double> {

    private static Logger LOGGER = LoggerFactory.getLogger(WebCalculatorVisitor.class);

    private final RestTemplate rt;
    private final static String path = "/calc";
    private final String uriAdd;
    private final String uriSub;
    private final String uriMult;
    private final String uriDiv;
    private final String uriPow;
    private final Environment env;

    private final boolean CAN_ADD;
    private final boolean CAN_SUBTRACT;
    private final boolean CAN_MULTIPLY;
    private final boolean CAN_DIVIDE;
    private final boolean CAN_POWER;

    public static enum OPERATOR {
        ADD, SUB, MULT, DIV, POW;
    }

    public WebCalculatorVisitor(RestTemplate rt, Environment env) {
        this.rt = rt;
        this.env = env;

        this.uriAdd = "http://" + env.getProperty(Properties.SERVICE_ADD) + path;
        this.uriSub = "http://" + env.getProperty(Properties.SERVICE_SUBTRACT) + path;
        this.uriMult = "http://" + env.getProperty(Properties.SERVICE_MULTIPLY) + path;
        this.uriDiv = "http://" + env.getProperty(Properties.SERVICE_DIVIDE) + path;
        this.uriPow = "http://" + env.getProperty(Properties.SERVICE_POWER) + path;

        if (Boolean.TRUE.equals(env.getProperty(Properties.CAN_DO_ALL, Boolean.class))) {
            this.CAN_ADD = true;
            this.CAN_SUBTRACT = true;
            this.CAN_MULTIPLY = true;
            this.CAN_DIVIDE = true;
            this.CAN_POWER = true;
        }
        else {
            this.CAN_ADD = Boolean.TRUE.equals(env.getProperty(Properties.CAN_ADD, Boolean.class));
            this.CAN_SUBTRACT = Boolean.TRUE.equals(env.getProperty(Properties.CAN_SUBTRACT, Boolean.class));
            this.CAN_MULTIPLY = Boolean.TRUE.equals(env.getProperty(Properties.CAN_MULTIPLY, Boolean.class));
            this.CAN_DIVIDE = Boolean.TRUE.equals(env.getProperty(Properties.CAN_DIVIDE, Boolean.class));
            this.CAN_POWER = Boolean.TRUE.equals(env.getProperty(Properties.CAN_POWER, Boolean.class));
        }
    }

    @Override
    public Double visitNumber(CalculatorParser.NumberContext ctx) {
        return Double.parseDouble(ctx.NUMBER().getText());
    }

    @Override
    public Double visitParentheses(CalculatorParser.ParenthesesContext ctx) {
        LOGGER.trace("Node Parenthesis: {}", ctx.getText());
        return this.visit(ctx.inner);
    }

    @Override
    public Double visitPower(CalculatorParser.PowerContext ctx) {
        LOGGER.trace("Node power ({})", ctx.getText());

        String opUri = uriPow;
        OPERATOR op = OPERATOR.POW;

        if (CAN_POWER) {
            Double answer = Math.pow(this.visit(ctx.left), this.visit(ctx.right));
            LOGGER.trace("Node power ({}) --> {}", ctx.getText(), answer);
            return answer;
        }
        else {
            return passBinaryOperation(op, opUri, ctx.getText());
        }
    }

    @Override
    public Double visitMultiplicationOrDivision(CalculatorParser.MultiplicationOrDivisionContext ctx) {
        String opUri;
        OPERATOR op;

        if (ctx.operator.getText().equals("*")) {
            LOGGER.trace("Node multiply ({}): left ({}), right ({})", ctx.getText(), ctx.left.getText(), ctx.right.getText());

            if (CAN_MULTIPLY) {
                Double answer = this.visit(ctx.left) * this.visit(ctx.right);
                LOGGER.trace("Node multiply ({}) --> {}", ctx.getText(), answer);
                return answer;
            }
            else {
                op = OPERATOR.MULT;
                opUri = uriMult;
            }
        }
        else { // must be division
            LOGGER.trace("Node divide ({}): left ({}), right ({})", ctx.getText(), ctx.left.getText(), ctx.right.getText());

            if (CAN_DIVIDE) {
                Double answer = this.visit(ctx.left) / this.visit(ctx.right);
                LOGGER.trace("Node divide ({}) --> {}", ctx.getText(), answer);
                return answer;
            }
            else {
                op = OPERATOR.DIV;
                opUri = uriDiv;
            }
        }

        return passBinaryOperation(op, opUri, ctx.getText());
    }

    @Override
    public Double visitNegation(CalculatorParser.NegationContext ctx) {
        return -1 * this.visit(ctx.right);
    }

    @Override
    public Double visitAdditionOrSubtraction(CalculatorParser.AdditionOrSubtractionContext ctx) {
        String opUri;
        OPERATOR op;

        if (ctx.operator.getText().equals("+")) {
            LOGGER.trace("Node add ({}): left ({}), right ({})", ctx.getText(), ctx.left.getText(), ctx.right.getText());
            if (CAN_ADD) {
                Double answer = this.visit(ctx.left) + this.visit(ctx.right);
                LOGGER.trace("Node add ({}) --> {}", ctx.getText(), answer);
                return answer;
            }
            else {
                op = OPERATOR.ADD;
                opUri = uriAdd;
            }
        }
        else { // must be subtraction
            LOGGER.trace("Node subtract ({}): left ({}), right ({})", ctx.getText(), ctx.left.getText(), ctx.right.getText());
            if (CAN_SUBTRACT) {
                Double answer = this.visit(ctx.left) - this.visit(ctx.right);
                LOGGER.trace("Node subtract ({}) --> {}", ctx.getText(), answer);
                return answer;
            }
            else {
                op = OPERATOR.SUB;
                opUri = uriSub;
            }
        }

        return passBinaryOperation(op, opUri, ctx.getText());
    }

    private Double passBinaryOperation(OPERATOR op, String opUri, String expText) {
        LOGGER.trace("Passing binary op to {} service: {}, {}", op, opUri, expText);

        try {
            ResponseEntity<String> response = rt.postForEntity(opUri, expText, String.class);
            return Double.parseDouble(response.getBody());
        }
        catch (Exception e) {
            LOGGER.error("Cannot handle {} op. Maybe DNS? Tried {}", op, opUri);
            throw e;
        }
    }
}
