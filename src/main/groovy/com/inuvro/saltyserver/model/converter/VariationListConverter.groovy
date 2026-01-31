package com.inuvro.saltyserver.model.converter

import com.inuvro.saltyserver.model.Variation
import jakarta.persistence.Converter

@Converter(autoApply = true)
class VariationListConverter extends JsonListConverter<Variation> {
    VariationListConverter() {
        super(Variation.class)
    }
}
