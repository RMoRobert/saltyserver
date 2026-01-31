package com.inuvro.saltyserver.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class Direction implements Serializable {
    String id
    Boolean isHeading
    String text

    Direction() {}

    @JsonCreator
    Direction(@JsonProperty("id") String id,
             @JsonProperty("isHeading") Boolean isHeading,
             @JsonProperty("text") String text) {
        this.id = id
        this.isHeading = isHeading
        this.text = text
    }
}
