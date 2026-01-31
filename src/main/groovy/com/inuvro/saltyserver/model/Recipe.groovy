package com.inuvro.saltyserver.model

import com.inuvro.saltyserver.model.converter.*
import jakarta.persistence.*

import java.time.LocalDateTime

@Entity
@Table(name = "recipe")
class Recipe {
    @Id
    String id
    
    @Column(nullable = false)
    String name = ""
    
    @Column(name = "created_date")
    LocalDateTime createdDate = LocalDateTime.now()
    
    @Column(name = "last_modified_date")
    LocalDateTime lastModifiedDate = LocalDateTime.now()
    
    @Column(name = "last_prepared")
    LocalDateTime lastPrepared
    
    String source = ""
    
    @Column(name = "source_details")
    String sourceDetails = ""
    
    @Lob
    @Column(columnDefinition = "CLOB")
    String introduction = ""
    
    @Enumerated(EnumType.ORDINAL)
    Difficulty difficulty = Difficulty.NOT_SET
    
    @Enumerated(EnumType.ORDINAL)
    Rating rating = Rating.NOT_SET
    
    @Column(name = "image_filename")
    String imageFilename
    
    @Lob
    @Column(name = "image_thumbnail_data")
    byte[] imageThumbnailData
    
    @Column(name = "is_favorite")
    Boolean isFavorite = false
    
    @Column(name = "want_to_make")
    Boolean wantToMake = false
    
    String yield = ""
    
    Integer servings
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    Course course
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    User user
    
    // JSON columns using JPA converters
    @Lob
    @Convert(converter = DirectionListConverter.class)
    @Column(columnDefinition = "CLOB", name = "directions")
    List<Direction> directions = []
    
    @Lob
    @Convert(converter = IngredientListConverter.class)
    @Column(columnDefinition = "CLOB", name = "ingredients")
    List<Ingredient> ingredients = []
    
    @Lob
    @Convert(converter = NoteListConverter.class)
    @Column(columnDefinition = "CLOB", name = "notes")
    List<Note> notes = []
    
    @Lob
    @Convert(converter = VariationListConverter.class)
    @Column(columnDefinition = "CLOB", name = "variations")
    List<Variation> variations = []
    
    @Lob
    @Convert(converter = PreparationTimeListConverter.class)
    @Column(columnDefinition = "CLOB", name = "preparation_times")
    List<PreparationTime> preparationTimes = []
    
    @Lob
    @Convert(converter = NutritionConverter.class)
    @Column(columnDefinition = "CLOB", name = "nutrition")
    NutritionInformation nutrition

    // Many-to-many relationships
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "recipe_category",
        joinColumns = @JoinColumn(name = "recipe_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @com.fasterxml.jackson.annotation.JsonIgnore
    Set<Category> categories = [] as Set
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "recipe_tag",
        joinColumns = @JoinColumn(name = "recipe_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @com.fasterxml.jackson.annotation.JsonIgnore
    Set<Tag> tags = [] as Set
    
    // Virtual properties for JSON serialization (category/tag IDs only)
    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("categoryIds")
    List<String> getCategoryIds() {
        return categories?.collect { it.id } ?: []
    }
    
    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("categoryIds")
    void setCategoryIds(List<String> ids) {
        // This is handled by the service layer - just store temporarily
        this._categoryIds = ids
    }
    
    @Transient
    private List<String> _categoryIds
    
    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("tagIds")
    List<String> getTagIds() {
        return tags?.collect { it.id } ?: []
    }
    
    @Transient
    @com.fasterxml.jackson.annotation.JsonProperty("tagIds")
    void setTagIds(List<String> ids) {
        // This is handled by the service layer - just store temporarily
        this._tagIds = ids
    }
    
    @Transient
    private List<String> _tagIds
    
    // Getters for the temporary ID lists (used by service)
    List<String> getInputCategoryIds() { return _categoryIds }
    List<String> getInputTagIds() { return _tagIds }

    Recipe() {}

    String getSummary() {
        if (introduction && !introduction.isEmpty()) {
            return introduction
        } else if (source && !source.isEmpty()) {
            return source
        } else if (sourceDetails && !sourceDetails.isEmpty()) {
            return sourceDetails
        }
        return ""
    }
}
