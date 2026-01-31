package com.inuvro.saltyserver.model

import com.inuvro.saltyserver.model.converter.ShoppingListContentsConverter
import jakarta.persistence.*

@Entity
@Table(name = "shopping_list")
class ShoppingList {
    @Id
    String id
    
    String name
    
    @Column(name = "is_freeform")
    Boolean isFreeform
    
    @Lob
    @Column(name = "contents_for_freeform", columnDefinition = "CLOB")
    String contentsForFreeform
    
    @Lob
    @Convert(converter = ShoppingListContentsConverter.class)
    @Column(name = "contents_for_list", columnDefinition = "CLOB")
    List<ShoppingListListContents> contentsForList = []

    ShoppingList() {}

    ShoppingList(String id, String name, Boolean isFreeform, String contentsForFreeform, List<ShoppingListListContents> contentsForList) {
        this.id = id
        this.name = name
        this.isFreeform = isFreeform
        this.contentsForFreeform = contentsForFreeform
        this.contentsForList = contentsForList ?: []
    }
}
