package com.github.harrybits.dcalc;

import org.junit.jupiter.params.provider.Arguments;
import java.util.stream.Stream;

public class TestExpressions {
    static Stream<Arguments> getStream() {
        return Stream.of(
                Arguments.of("1+2", 3.0),
                Arguments.of("-1+-2", -3.0),
                Arguments.of("1/2", 0.5),
                Arguments.of("1/4", 0.25),
                Arguments.of("1/4 + 1/4", 0.5),
                Arguments.of("4/1*2", 8.0),
                Arguments.of("4/0", Double.POSITIVE_INFINITY),
                Arguments.of("-4/0", Double.NEGATIVE_INFINITY),
                Arguments.of("2*3", 6.0),
                Arguments.of("-2*3", -6.0),
                Arguments.of("2^3", 8.0),
                Arguments.of("(2+3)*2-1", 9.0),
                Arguments.of("1-2*3+4", -1.0),
                Arguments.of("1+2*3-4", 3.0)
        );
    }
}
