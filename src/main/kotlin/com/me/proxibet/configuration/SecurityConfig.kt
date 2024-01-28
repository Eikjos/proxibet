package com.me.proxibet.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.me.proxibet.filter.JwtAuthenticationFilter
import com.me.proxibet.service.CustomUserDetailsService
import com.me.proxibet.service.JWTService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class SecurityConfig(private val customUserDetailsService: CustomUserDetailsService,
                     private val jwtService: JWTService,
                     private val objectMapper : ObjectMapper) {

    @Bean
    fun securityChainFilter(http : HttpSecurity, jwtAuthenticationFilter: JwtAuthenticationFilter) : SecurityFilterChain {
        http.invoke {
            formLogin {
                loginPage = "/login"
                authenticationSuccessHandler = authenticationSuccessHandler()
                authenticationFailureHandler = authenticationFailureHandler()
            }
            logout {
                logoutUrl = "/logout"
                deleteCookies("jwtToken")
            }
            httpBasic {  }
        }
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build();
    }

    @Bean
    fun authenticationManager(
        userDetailsService: UserDetailsService?, passwordEncoder: PasswordEncoder?
    ): AuthenticationManager? {
        val authenticationProvider = DaoAuthenticationProvider()
        authenticationProvider.setUserDetailsService(userDetailsService)
        authenticationProvider.setPasswordEncoder(passwordEncoder)
        return ProviderManager(authenticationProvider)
    }

    @Bean
    fun userDetailsService() : UserDetailsService {
        return customUserDetailsService;
    }

    @Bean
    fun passwordEncoder() : PasswordEncoder {
        return BCryptPasswordEncoder();
    }

    fun authenticationFailureHandler(): AuthenticationFailureHandler? {
        return AuthenticationFailureHandler { request: HttpServletRequest?, response: HttpServletResponse, exception: AuthenticationException? ->
            val problemDetail =
                ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "ACCESS_DENIED")
            problemDetail.title = "INVALID_CREDENTIALS"
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.status = HttpStatus.FORBIDDEN.value()
            objectMapper.writeValue(response.writer, problemDetail)
        }
    }

    @Bean
    fun authenticationSuccessHandler(): AuthenticationSuccessHandler? {
        return AuthenticationSuccessHandler { request: HttpServletRequest?, response: HttpServletResponse, authentication: Authentication ->
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            var cookie = Cookie("jwtToken", jwtService.GenerateToken(authentication));
            cookie.isHttpOnly = true
            cookie.path = "/"
            response.addCookie(cookie)
            response.status = HttpStatus.OK.value()
            objectMapper.writeValue(
                response.writer, customUserDetailsService.findByUsername(authentication.name)
            )
        }
    }

    @Bean
    fun authenticationEntryPoint(): AuthenticationEntryPoint? {
        return AuthenticationEntryPoint { request: HttpServletRequest?, response: HttpServletResponse, authException: AuthenticationException? ->
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.status = HttpStatus.UNAUTHORIZED.value()
            val problemDetail =
                ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "UNAUTHORIZE_ACCESS")
            problemDetail.title = "UNAUTHORIZED"
            objectMapper.writeValue(response.writer, problemDetail)
        }
    }
}