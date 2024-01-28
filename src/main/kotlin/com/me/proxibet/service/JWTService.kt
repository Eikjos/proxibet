package com.me.proxibet.service

import com.me.proxibet.entity.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors
import kotlin.collections.HashMap


@Component
class JWTService {

    private val secret : String = "357638792F423F4428472B4B6250655368566D597133743677397A2443264629";
    private val SECRET_KEY  = Keys.hmacShaKeyFor(
        secret.toByteArray()
    )

    fun extractUsername(token: String?): String? {
        return extractClaim(token!!, { obj: Claims -> obj.subject })
    }

    fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T {
        val claims: Claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    fun GenerateToken(authentication : Authentication): String {
        val claims: HashMap<String, Any> = HashMap()
        val scope = authentication.authorities.stream()
            .map { obj: GrantedAuthority -> obj.authority }
            .collect(Collectors.joining(" "))
        claims.put("role", scope);
        return Jwts.builder().claims(claims).signWith(SECRET_KEY).compact();
    }

    fun isValid(token : String, userDetails: UserDetails) : Boolean {
        val username = extractUsername(token);
        return username == userDetails.username && !isExpired(token);
    }

    fun isExpired(token: String): Boolean =
        extractAllClaims(token)
            .expiration
            .before(Date(System.currentTimeMillis()))

    private fun extractAllClaims(token: String): Claims {
        val parser = Jwts.parser()
            .verifyWith(SECRET_KEY)
            .build()
        return parser
            .parseSignedClaims(token)
            .payload
    }

    private fun createToken(claims: Map<String, Any?>, user: UserDetails): String {
        return Jwts.builder().subject(user.username)
            .claims(claims)
            .signWith(SECRET_KEY)
            .compact();
    }
}