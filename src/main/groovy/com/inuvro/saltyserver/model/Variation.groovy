package com.inuvro.saltyserver.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class Variation implements Serializable {
    String id
    String variationName
    String text

    Variation() {}

    @JsonCreator
    Variation(@JsonProperty("id") String id,
              @JsonProperty("variationName") String variationName,
              @JsonProperty("text") String text) {
        this.id = id
        this.variationName = variationName
        this.text = text
    }
}
