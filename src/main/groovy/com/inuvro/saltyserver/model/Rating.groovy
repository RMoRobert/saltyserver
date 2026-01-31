package com.inuvro.saltyserver.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum Rating {
    NOT_SET(0, "not set"),
    ONE(1, "1"),
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5")

    final int value
    final String displayName

    Rating(int value, String displayName) {
        this.value = value
        this.displayName = displayName
    }

    @JsonValue
    int getValue() {
        return value
    }

    @JsonCreator
    static Rating fromValue(int value) {
        return values().find { it.value == value } ?: NOT_SET
    }

    static Rating fromIndex(double index) {
        return fromValue((int) index)
    }

    double asIndex() {
        return (double) value
    }
}
