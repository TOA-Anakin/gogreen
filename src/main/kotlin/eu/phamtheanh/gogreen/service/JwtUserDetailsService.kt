package eu.phamtheanh.gogreen.service

import eu.phamtheanh.gogreen.model.User
import eu.phamtheanh.gogreen.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.ArrayList

@Service
class JwtUserDetailsService @Autowired constructor(
    private val userRepository: UserRepository,
    private val bcryptEncoder: PasswordEncoder
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user: User? = userRepository.findByUsername(username)
        user ?: throw UsernameNotFoundException("User not found with the username: $username")

        return org.springframework.security.core.userdetails.User(
            user.username,
            user.password,
            mutableListOf()
        )
    }

    fun save(user: User): User {
        val newUser = User(
            username = user.username,
            password = bcryptEncoder.encode(user.password),
            email = user.email,
            name = user.name,
            address = user.address,
            phone = user.phone
        )

        return userRepository.save(newUser)
    }
}