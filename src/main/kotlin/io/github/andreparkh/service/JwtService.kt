package io.github.andreparkh.service

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.Key
import java.util.Base64
import java.util.Date

@Service
class JwtService() {

//    @Value("\${security.jwt.secret-key}")
//    private lateinit var secretKey: String

    val key = Keys.secretKeyFor(SignatureAlgorithm.HS512)
    val secretKey = Base64.getEncoder().encodeToString(key.encoded)

    @Value("\${security.jwt.expiration-time}")
    private var expirationTime: Long = 86400000 // 24 часа /вынести константой

    private fun getKey(): Key {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateToken(email: String): String {
        return Jwts.builder()
            .setSubject(email)
            .setExpiration(Date(System.currentTimeMillis() + expirationTime))
            .signWith(getKey(), SignatureAlgorithm.HS512)
            .compact()
    }

    fun extractEmail(token: String): String =
        Jwts.parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token)
            .body
            .subject

    fun isTokenValid(token: String): Boolean =
        try {
            Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: JwtException) {
            false
        }
}