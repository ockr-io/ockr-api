package io.ockr.ecosystem.algorithm;

import io.ockr.ecosystem.entity.HashResult;
import io.ockr.ecosystem.entity.TextPosition;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class Algorithm {
    private final String name;
    protected final List<Parameter> parameters;

    public Algorithm(String name) {
        this.name = name;
        this.parameters = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public List<Parameter> getParameters() {
        return this.parameters;
    }

    protected Integer getIntegerParameter(String name) {
        for (Parameter parameter : this.parameters) {
            if (parameter.getName().equals(name) && parameter.getType() == ParameterType.INTEGER) {
                if (parameter.getValue() == null) {
                    return Integer.parseInt(parameter.getDefaultValue());
                }
                return Integer.parseInt(parameter.getValue());
            }
        }
        throw new IllegalArgumentException("Parameter " + name + " does not exist or is not an integer");
    }

    public void setParameter(String name, String value) {
        for (Parameter parameter : this.parameters) {
            if (parameter.getName().equals(name)) {
                parameter.setValue(value);
                return;
            }
        }
        throw new IllegalArgumentException("Parameter " + name + " does not exist");
    }

    public abstract HashResult compute(List<TextPosition> textPositions, String base64Image);
    public String hash(String text) {
        return DigestUtils.sha256Hex(text);
    }
}
