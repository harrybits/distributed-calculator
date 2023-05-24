package com.github.harrybits.dcalc;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class Properties {
    /**
     * Configures the app to handle all operators. Consequently, no network calls will be made because they are not needed. Used for debugging.
     */
    public static final String CAN_DO_ALL = "ops.can.do.all";

    public static final String CAN_ADD = "ops.can.add";
    public static final String CAN_SUBTRACT = "ops.can.subtract";
    public static final String CAN_MULTIPLY = "ops.can.multiply";
    public static final String CAN_DIVIDE = "ops.can.divide";
    public static final String CAN_POWER = "ops.can.power";

    /**
     * The default values work Docker contexts.
     * Override these if you want to direct calls some place else. For example, maybe you want to direct addition calls to localhost instead of the Docker network. Set the addition service name to host.docker.internal:PORT
     */
    public static final String SERVICE_ADD = "ops.service.add.name";
    public static final String SERVICE_SUBTRACT = "ops.service.subtract.name";
    public static final String SERVICE_MULTIPLY = "ops.service.multiply.name";
    public static final String SERVICE_DIVIDE = "ops.service.divide.name";
    public static final String SERVICE_POWER = "ops.service.power.name";

    public static Map<String, Object> defaults = ImmutableMap.<String, Object>builder()
            .put(CAN_ADD, "false")
            .put(CAN_SUBTRACT, "false")
            .put(CAN_MULTIPLY, "false")
            .put(CAN_DIVIDE, "false")
            .put(CAN_POWER, "false")
            .put(SERVICE_ADD, "addition")
            .put(SERVICE_SUBTRACT, "subtraction")
            .put(SERVICE_MULTIPLY, "multiplication")
            .put(SERVICE_DIVIDE, "division")
            .put(SERVICE_POWER, "power")
            .put("management.endpoints.web.exposure.include", "*")
            .put("server.port", "80")
            .build();
}
