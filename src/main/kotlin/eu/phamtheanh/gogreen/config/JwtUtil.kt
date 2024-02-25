package eu.phamtheanh.gogreen.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.Date
import java.util.function.Function

@Component
class JwtUtil : Serializable {
    companion object {
        private const val serialVersionUID = -5608301936876257688L
        const val JWT_TOKEN_VALIDITY = 5 * 60 * 60
    }

    @Value("\${jwt.secret}")
    private lateinit var secret: String

    fun getUsernameFromToken(token: String): String = getClaimFromToken(token, Claims::getSubject)

    fun getIssuedAtDateFromToken(token: String): Date = getClaimFromToken(token, Claims::getIssuedAt)

    fun getExpirationDateFromToken(token: String): Date = getClaimFromToken(token, Claims::getExpiration)

    fun <T> getClaimFromToken(token: String, claimsResolver: Function<Claims, T>): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver.apply(claims)
    }

    private fun getAllClaimsFromToken(token: String): Claims =
        Jwts.parser().setSigningKey(secret).build().parseSignedClaims(token).getPayload()

    private fun isTokenExpired(token: String): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(Date())
    }

    private fun ignoreTokenExpiration(token: String): Boolean = false

    fun generateToken(userDetails: UserDetails): String {
        val claims = HashMap<String, Any>()
        return doGenerateToken(claims, userDetails.username)
    }

    private fun doGenerateToken(claims: Map<String, Any>, subject: String): String {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact()
    }

    fun canTokenBeRefreshed(token: String): Boolean =
        !isTokenExpired(token) || ignoreTokenExpiration(token)

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = getUsernameFromToken(token)
        return username == userDetails.username && !isTokenExpired(token)
    }
}