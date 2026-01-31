package com.inuvro.saltyserver.category

import com.inuvro.saltyserver.model.Category
import com.inuvro.saltyserver.security.CurrentUserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryService {
    private final CategoryRepository categoryRepository
    private final CurrentUserService currentUserService

    CategoryService(CategoryRepository categoryRepository, CurrentUserService currentUserService) {
        this.categoryRepository = categoryRepository
        this.currentUserService = currentUserService
    }

    List<Category> findAll() {
        def user = currentUserService.getCurrentUser()
        return user ? categoryRepository.findByUser(user) : []
    }

    Optional<Category> findById(String id) {
        def user = currentUserService.getCurrentUser()
        return user ? categoryRepository.findByIdAndUser(id, user) : Optional.empty()
    }

    @Transactional
    Category save(Category category) {
        def user = currentUserService.requireCurrentUser()
        if (category.user == null) {
            category.user = user
        }
        return categoryRepository.save(category)
    }

    @Transactional
    void deleteById(String id) {
        def user = currentUserService.getCurrentUser()
        if (user) {
            categoryRepository.deleteByIdAndUser(id, user)
        }
    }

    List<Category> findByName(String name) {
        def user = currentUserService.getCurrentUser()
        return user ? categoryRepository.findByUserAndName(user, name) : []
    }

    List<Category> searchByName(String name) {
        def user = currentUserService.getCurrentUser()
        return user ? categoryRepository.findByUserAndNameContainingIgnoreCase(user, name) : []
    }
}
