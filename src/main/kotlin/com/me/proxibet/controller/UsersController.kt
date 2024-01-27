package com.me.proxibet.controller

import com.me.proxibet.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid;

@RestController()
@RequestMapping("/api/user")
class UsersController(private val userService : UserService) {

    @PostMapping()
    fun create(@RequestBody @Valid createUserDto : CreateUserDto)

}