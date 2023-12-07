package io.ockr.ecosystem.enums;

import lombok.Getter;

@Getter
public enum Algorithm {
    SQUARE_PUZZLE("square-puzzle");

    private final String name;

    Algorithm(String name) {
        this.name = name;
    }
}
