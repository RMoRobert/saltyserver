package com.inuvro.saltyserver.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class Note implements Serializable {
    String id
    String title
    String content

    Note() {}

    @JsonCreator
    Note(@JsonProperty("id") String id,
         @JsonProperty("title") String title,
         @JsonProperty("content") String content) {
        this.id = id
        this.title = title
        this.content = content
    }
}
