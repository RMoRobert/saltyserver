package com.inuvro.saltyserver.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class ShoppingListListContents implements Serializable {
    String id
    Boolean isCompleted = false
    Boolean isImportant = false
    String text

    ShoppingListListContents() {}

    @JsonCreator
    ShoppingListListContents(@JsonProperty("id") String id,
                            @JsonProperty("isCompleted") Boolean isCompleted,
                            @JsonProperty("isImportant") Boolean isImportant,
                            @JsonProperty("text") String text) {
        this.id = id
        this.isCompleted = isCompleted ?: false
        this.isImportant = isImportant ?: false
        this.text = text
    }
}
