package com.me.proxibet.service

import com.me.proxibet.dto.output.UserDto
import com.me.proxibet.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService{

    override fun loadUserByUsername(username: String?): UserDetails {
        if (username != null) {
            val user = userRepository.findByUsername(username);
            if (user != null) {
                return User(user.username, user.password, emptyList());
            }
        }
        throw UsernameNotFoundException("username.not.found");
    }

    fun findByUsername(username: String?) : UserDto? {
        if (username != null) {
            val user = userRepository.findByUsername(username);
            if (user != null) {
                return UserDto(user.username, user.firstName, user.lastName);
            }
        }
        return null;
    }
}