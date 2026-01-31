package com.inuvro.saltyserver.model.converter

import com.inuvro.saltyserver.model.Ingredient
import jakarta.persistence.Converter

@Converter(autoApply = true)
class IngredientListConverter extends JsonListConverter<Ingredient> {
    IngredientListConverter() {
        super(Ingredient.class)
    }
}
