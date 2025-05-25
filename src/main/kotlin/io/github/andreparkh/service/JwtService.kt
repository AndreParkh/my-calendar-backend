package io.github.andreparkh.service

import io.github.andreparkh.enums.GroupMemberRole
import io.github.andreparkh.model.GroupMember
import io.github.andreparkh.model.User
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.Key
import java.util.Date

@Service
class JwtService() {

    @Value("\${security.jwt.secret-key}")
    private lateinit var secretKey: String

    @Value("\${security.jwt.expiration-time}")
    private var expirationTime: Long = 86_400_000 // 24 часа /вынести константой

    private fun getKey(): Key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    fun generateTokenWithRoles(user: User, groupMembers: List<GroupMember>): String {
        val rolesByGroup = groupMembers.map {
            "${it.group.id}:${it.role}"
        }

        return Jwts.builder()
            .setSubject(user.email)
            .claim("roles", rolesByGroup)
            .setExpiration(Date(System.currentTimeMillis() + expirationTime))
            .signWith(getKey(), SignatureAlgorithm.HS512)
            .compact()
    }

    fun extractUsername(token: String): String =
        Jwts.parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token)
            .body
            .subject

    fun extractRoles(token: String): Map<Long, GroupMemberRole> {
        val claims = Jwts.parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token)
            .body

        val rolesList = claims["roles"] as List<String>
        return rolesList.associate {
            val parts = it.split(":")
            parts[0].toLong() to GroupMemberRole.valueOf(parts[1])
        }
    }

    fun validateToken(token: String): Boolean =
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