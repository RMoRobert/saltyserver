package com.inuvro.saltyserver.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class NutritionInformation implements Serializable {
    String id
    String servingSize
    Double calories
    Double protein          // grams
    Double carbohydrates   // grams
    Double fat             // grams
    Double saturatedFat    // grams
    Double transFat        // grams
    Double fiber           // grams
    Double sugar           // grams
    Double sodium          // milligrams
    Double cholesterol     // milligrams
    Double addedSugar      // grams
    Double vitaminD        // micrograms
    Double calcium         // milligrams
    Double iron            // milligrams
    Double potassium       // milligrams
    Double vitaminA        // micrograms
    Double vitaminC        // milligrams

    NutritionInformation() {
        this.id = UUID.randomUUID().toString()
    }

    @JsonCreator
    NutritionInformation(@JsonProperty("id") String id,
                        @JsonProperty("servingSize") String servingSize,
                        @JsonProperty("calories") Double calories,
                        @JsonProperty("protein") Double protein,
                        @JsonProperty("carbohydrates") Double carbohydrates,
                        @JsonProperty("fat") Double fat,
                        @JsonProperty("saturatedFat") Double saturatedFat,
                        @JsonProperty("transFat") Double transFat,
                        @JsonProperty("fiber") Double fiber,
                        @JsonProperty("sugar") Double sugar,
                        @JsonProperty("sodium") Double sodium,
                        @JsonProperty("cholesterol") Double cholesterol,
                        @JsonProperty("addedSugar") Double addedSugar,
                        @JsonProperty("vitaminD") Double vitaminD,
                        @JsonProperty("calcium") Double calcium,
                        @JsonProperty("iron") Double iron,
                        @JsonProperty("potassium") Double potassium,
                        @JsonProperty("vitaminA") Double vitaminA,
                        @JsonProperty("vitaminC") Double vitaminC) {
        this.id = id ?: UUID.randomUUID().toString()
        this.servingSize = servingSize
        this.calories = calories
        this.protein = protein
        this.carbohydrates = carbohydrates
        this.fat = fat
        this.saturatedFat = saturatedFat
        this.transFat = transFat
        this.fiber = fiber
        this.sugar = sugar
        this.sodium = sodium
        this.cholesterol = cholesterol
        this.addedSugar = addedSugar
        this.vitaminD = vitaminD
        this.calcium = calcium
        this.iron = iron
        this.potassium = potassium
        this.vitaminA = vitaminA
        this.vitaminC = vitaminC
    }
}
