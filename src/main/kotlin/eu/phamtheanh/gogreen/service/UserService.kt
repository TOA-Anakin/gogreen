package eu.phamtheanh.gogreen.service

import eu.phamtheanh.gogreen.exceptions.UserNotFoundException
import eu.phamtheanh.gogreen.model.User
import eu.phamtheanh.gogreen.model.UserDto
import eu.phamtheanh.gogreen.repo.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import jakarta.transaction.Transactional

@Service
@Transactional
class UserService(private val userRepository: UserRepository) {

    @Autowired
    private lateinit var bcryptEncoder: PasswordEncoder

    fun getUsers(): MutableList<User> = userRepository.findAll()

    fun getUser(id: Long): User =
        userRepository.findById(id).orElseThrow {
            UserNotFoundException("User by id $id was not found.")
        }

    fun updateUser(id: Long, updatedUser: User): User {
        val user: User = getUser(id).apply {
            username = updatedUser.username
            email = updatedUser.email
            address = updatedUser.address
            name = updatedUser.name
            phone = updatedUser.phone
        }
        return userRepository.save(user)
    }

    fun deleteUser(id: Long) = userRepository.deleteById(id)
}