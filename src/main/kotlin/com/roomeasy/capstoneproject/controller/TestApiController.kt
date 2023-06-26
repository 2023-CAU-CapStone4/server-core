package com.roomeasy.capstoneproject.controller

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
class TestApiController {

    @GetMapping("/hello")
    fun hello(): String {
        return "hello"
    }
}