package io.ockr.ecosystem.enums;

import lombok.Getter;

@Getter
public enum Algorithm {
    DEFAULT_PUZZLE("square-puzzle"),
    PUZZLE_PING_PONG("puzzle-ping-pong");

    private final String name;

    Algorithm(String name) {
        this.name = name;
    }
}
