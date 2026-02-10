package com.inuvro.saltyserver.security

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginController {

    /** Root path: redirect to recipes. If not logged in, security will redirect to /login first. */
    @GetMapping("/")
    String root() {
        return "redirect:/recipes"
    }

    @GetMapping("/login")
    String login() {
        return "auth/login"
    }
}
