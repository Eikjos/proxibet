package com.me.proxibet.service

import com.me.proxibet.entity.User
import com.me.proxibet.repository.UserRepository
import main.kotlin.com.me.proxibet.dto.input.user.CreateUserDto
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    val userRepository: UserRepository
) {

    fun create(model : CreateUserDto) : UUID {

        return u.id;
    }
}