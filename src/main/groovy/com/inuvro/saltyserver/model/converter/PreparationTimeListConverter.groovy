package com.inuvro.saltyserver.model.converter

import com.inuvro.saltyserver.model.PreparationTime
import jakarta.persistence.Converter

@Converter(autoApply = true)
class PreparationTimeListConverter extends JsonListConverter<PreparationTime> {
    PreparationTimeListConverter() {
        super(PreparationTime.class)
    }
}
