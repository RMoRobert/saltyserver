package com.inuvro.saltyserver.model.converter

import com.inuvro.saltyserver.model.Note
import jakarta.persistence.Converter

@Converter(autoApply = true)
class NoteListConverter extends JsonListConverter<Note> {
    NoteListConverter() {
        super(Note.class)
    }
}
