package com.inuvro.saltyserver.model.converter

import com.inuvro.saltyserver.model.ShoppingListListContents
import jakarta.persistence.Converter

@Converter(autoApply = true)
class ShoppingListContentsConverter extends JsonListConverter<ShoppingListListContents> {
    ShoppingListContentsConverter() {
        super(ShoppingListListContents.class)
    }
}
