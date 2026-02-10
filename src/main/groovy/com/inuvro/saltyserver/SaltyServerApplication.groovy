package com.inuvro.saltyserver

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.bind.annotation.Mapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@EnableScheduling
class SaltyServerApplication {

    static void main(String[] args) {
        SpringApplication.run(SaltyServerApplication, args)
    }
}
