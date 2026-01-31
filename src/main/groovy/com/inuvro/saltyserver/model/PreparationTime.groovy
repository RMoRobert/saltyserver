package com.inuvro.saltyserver.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class PreparationTime implements Serializable {
    String id
    String type
    String timeString

    PreparationTime() {}

    @JsonCreator
    PreparationTime(@JsonProperty("id") String id,
                   @JsonProperty("type") String type,
                   @JsonProperty("timeString") String timeString) {
        this.id = id
        this.type = type
        this.timeString = timeString
    }
}
