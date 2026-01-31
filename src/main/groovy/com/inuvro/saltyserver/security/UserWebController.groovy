package com.inuvro.saltyserver.security

import com.inuvro.saltyserver.model.User
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
class UserWebController {
    
    private final UserService userService
    
    UserWebController(UserService userService) {
        this.userService = userService
    }
    
    @GetMapping
    String listUsers(Model model) {
        model.addAttribute("users", userService.findAll())
        model.addAttribute("currentPage", "users")
        return "users/list"
    }
    
    @GetMapping("/new")
    String showCreateForm(Model model) {
        model.addAttribute("user", new UserForm())
        model.addAttribute("availableRoles", ['ROLE_USER', 'ROLE_ADMIN'])
        model.addAttribute("editing", false)
        model.addAttribute("currentPage", "users")
        return "users/form"
    }
    
    @PostMapping
    String createUser(@ModelAttribute UserForm form, RedirectAttributes redirectAttributes) {
        try {
            Set<String> roles = form.roles?.toSet() ?: ['ROLE_USER'] as Set
            userService.createUser(form.username, form.password, form.email, roles)
            redirectAttributes.addFlashAttribute("success", "User '${form.username}' created successfully")
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.message)
            return "redirect:/users/new"
        }
        return "redirect:/users"
    }
    
    @GetMapping("/{id}/edit")
    String showEditForm(@PathVariable("id") String id, Model model) {
        User user = userService.findById(id)
            .orElseThrow { new IllegalArgumentException("User not found") }
        
        UserForm form = new UserForm(
            id: user.id,
            username: user.username,
            email: user.email,
            enabled: user.enabled,
            roles: user.roles?.toList() ?: []
        )
        
        model.addAttribute("user", form)
        model.addAttribute("availableRoles", ['ROLE_USER', 'ROLE_ADMIN'])
        model.addAttribute("editing", true)
        model.addAttribute("currentPage", "users")
        return "users/form"
    }
    
    @PostMapping("/{id}")
    String updateUser(@PathVariable("id") String id, @ModelAttribute UserForm form, RedirectAttributes redirectAttributes) {
        try {
            Set<String> roles = form.roles?.toSet() ?: ['ROLE_USER'] as Set
            userService.updateUser(id, form.email, roles, form.enabled)
            
            // Update password if provided
            if (form.password && !form.password.isEmpty()) {
                userService.changePassword(id, form.password)
            }
            
            redirectAttributes.addFlashAttribute("success", "User updated successfully")
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update user: ${e.message}")
        }
        return "redirect:/users"
    }
    
    @PostMapping("/{id}/delete")
    String deleteUser(@PathVariable("id") String id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id).orElse(null)
            if (user) {
                userService.deleteUser(id)
                redirectAttributes.addFlashAttribute("success", "User '${user.username}' deleted")
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user: ${e.message}")
        }
        return "redirect:/users"
    }
}

/**
 * Form backing object for user create/edit
 */
class UserForm {
    String id
    String username
    String password
    String email
    Boolean enabled = true
    List<String> roles = []
}
