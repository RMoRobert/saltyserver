package com.inuvro.saltyserver.category

import com.inuvro.saltyserver.model.Category
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/categories")
class CategoryController {
    private final CategoryService categoryService

    CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService
    }

    @GetMapping
    List<Category> getAllCategories() {
        return categoryService.findAll()
    }

    @GetMapping("/{id}")
    ResponseEntity<Category> getCategoryById(@PathVariable("id") String id) {
        def category = categoryService.findById(id)
        return category.map { new ResponseEntity<>(it, HttpStatus.OK) }
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND))
    }

    @PostMapping
    ResponseEntity<Category> createCategory(@RequestBody Category category) {
        def saved = categoryService.save(category)
        return new ResponseEntity<>(saved, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    ResponseEntity<Category> updateCategory(@PathVariable("id") String id, @RequestBody Category category) {
        if (!categoryService.findById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND)
        }
        category.id = id
        def updated = categoryService.save(category)
        return new ResponseEntity<>(updated, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteCategory(@PathVariable("id") String id) {
        if (!categoryService.findById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND)
        }
        categoryService.deleteById(id)
        return new ResponseEntity<>(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/search")
    List<Category> searchCategories(@RequestParam("name") String name) {
        return categoryService.searchByName(name)
    }
}
