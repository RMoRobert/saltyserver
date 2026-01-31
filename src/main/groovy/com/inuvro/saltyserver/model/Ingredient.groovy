package com.inuvro.saltyserver.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class Ingredient implements Serializable {
    String id
    Boolean isHeading = false
    Boolean isMain = false
    String text

    Ingredient() {}

    @JsonCreator
    Ingredient(@JsonProperty("id") String id,
               @JsonProperty("isHeading") Boolean isHeading,
               @JsonProperty("isMain") Boolean isMain,
               @JsonProperty("text") String text) {
        this.id = id
        this.isHeading = isHeading ?: false
        this.isMain = isMain ?: false
        this.text = text
    }
}
