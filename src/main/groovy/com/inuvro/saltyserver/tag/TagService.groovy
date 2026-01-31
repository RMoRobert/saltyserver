package com.inuvro.saltyserver.tag

import com.inuvro.saltyserver.model.Tag
import com.inuvro.saltyserver.security.CurrentUserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TagService {
    private final TagRepository tagRepository
    private final CurrentUserService currentUserService

    TagService(TagRepository tagRepository, CurrentUserService currentUserService) {
        this.tagRepository = tagRepository
        this.currentUserService = currentUserService
    }

    List<Tag> findAll() {
        def user = currentUserService.getCurrentUser()
        return user ? tagRepository.findByUser(user) : []
    }

    Optional<Tag> findById(String id) {
        def user = currentUserService.getCurrentUser()
        return user ? tagRepository.findByIdAndUser(id, user) : Optional.empty()
    }

    @Transactional
    Tag save(Tag tag) {
        def user = currentUserService.requireCurrentUser()
        if (tag.user == null) {
            tag.user = user
        }
        return tagRepository.save(tag)
    }

    @Transactional
    void deleteById(String id) {
        def user = currentUserService.getCurrentUser()
        if (user) {
            tagRepository.deleteByIdAndUser(id, user)
        }
    }

    List<Tag> findByName(String name) {
        def user = currentUserService.getCurrentUser()
        return user ? tagRepository.findByUserAndName(user, name) : []
    }

    List<Tag> searchByName(String name) {
        def user = currentUserService.getCurrentUser()
        return user ? tagRepository.findByUserAndNameContainingIgnoreCase(user, name) : []
    }
}
