package com.inuvro.saltyserver.recipe

import com.inuvro.saltyserver.model.Recipe
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/recipes")
class RecipeViewController {

    /** Default page size for recipe list */
    private static final int DEFAULT_PAGE_SIZE = 30
    private static final int MAX_PAGE_SIZE = 100

    private final RecipeService recipeService

    RecipeViewController(RecipeService recipeService) {
        this.recipeService = recipeService
    }

    @GetMapping
    String listRecipes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "pageSize", defaultValue = "30") int pageSize,
            @RequestParam(name = "q", required = false) String searchQuery,
            Model model) {
        // Clamp to avoid problems with unexpected values
        int safeSize = Math.min(Math.max(1, pageSize), MAX_PAGE_SIZE)
        int safePage = Math.max(0, page)

        def pageRequest = PageRequest.of(safePage, safeSize)
        def recipePage = searchQuery?.trim() ? recipeService.search(searchQuery.trim(), pageRequest) : recipeService.findAll(pageRequest)

        model.addAttribute("recipes", recipePage.content)
        model.addAttribute("recipePage", recipePage)
        model.addAttribute("pageSize", safeSize)
        model.addAttribute("searchQuery", searchQuery?.trim() ?: "")
        model.addAttribute("currentPage", "recipes")
        return "recipes/list"
    }

    @GetMapping("/{id}")
    String viewRecipe(@PathVariable("id") String id, Model model) {
        def recipe = recipeService.findById(id)
        if (!recipe.isPresent()) {
            return "redirect:/recipes"
        }
        model.addAttribute("recipe", recipe.get())
        model.addAttribute("currentPage", "recipes")
        return "recipes/view"
    }
}
