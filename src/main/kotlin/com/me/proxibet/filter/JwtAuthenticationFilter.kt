package com.me.proxibet.filter

import com.me.proxibet.service.CustomUserDetailsService
import com.me.proxibet.service.JWTService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*


@Component
class JwtAuthenticationFilter(
    private val userDetailsService : CustomUserDetailsService,
    private val jwtService: JWTService,
    private val authenticationManager : AuthenticationManager
) : OncePerRequestFilter() {

    private val COOKIE_NAME = "jwtToken";

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val cookies = request.cookies
        if (cookies != null && "/login" != request.servletPath) {
            var cookie = cookies.filter { f -> f.name == COOKIE_NAME }.firstOrNull()
            if (cookie != null) {
                var token = cookie.value
                var username = jwtService.extractUsername(token)
                if (username != null && SecurityContextHolder.getContext().authentication == null) {
                    val userDetails: UserDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtService.isValid(token, userDetails)) {
                        val authToken = UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, userDetails.authorities
                        )
                        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authToken
                    }
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}