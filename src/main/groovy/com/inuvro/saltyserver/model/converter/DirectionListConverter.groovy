package com.inuvro.saltyserver.model.converter

import com.inuvro.saltyserver.model.Direction
import jakarta.persistence.Converter

@Converter(autoApply = true)
class DirectionListConverter extends JsonListConverter<Direction> {
    DirectionListConverter() {
        super(Direction.class)
    }
}
