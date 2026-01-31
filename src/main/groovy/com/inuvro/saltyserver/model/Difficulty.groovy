package com.inuvro.saltyserver.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum Difficulty {
    NOT_SET(0, "(not set)"),
    EASY(1, "easy"),
    SOMEWHAT_EASY(2, "somewhat easy"),
    MEDIUM(3, "medium"),
    SLIGHTLY_DIFFICULT(4, "slightly difficult"),
    DIFFICULT(5, "difficult")

    final int value
    final String displayName

    Difficulty(int value, String displayName) {
        this.value = value
        this.displayName = displayName
    }

    @JsonValue
    int getValue() {
        return value
    }

    @JsonCreator
    static Difficulty fromValue(int value) {
        return values().find { it.value == value } ?: NOT_SET
    }

    static Difficulty fromIndex(double index) {
        return fromValue((int) index)
    }

    double asIndex() {
        return (double) value
    }
}
