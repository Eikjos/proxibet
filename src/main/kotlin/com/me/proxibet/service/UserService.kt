package com.me.proxibet.service

import com.me.proxibet.entity.User
import com.me.proxibet.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    val userRepository: UserRepository
) {

    fun create(user : User) : UUID {
        val u = userRepository.save(user);
        return u.id;
    }
}