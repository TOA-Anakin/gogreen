package eu.phamtheanh.gogreen.controller

import eu.phamtheanh.gogreen.config.JwtUtil
import eu.phamtheanh.gogreen.model.User
import eu.phamtheanh.gogreen.repo.UserRepository
import eu.phamtheanh.gogreen.service.JwtUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

import jakarta.servlet.http.HttpServletRequest

@RestController
@CrossOrigin
class JwtAuthenticationController {

    @Autowired
    lateinit var repo: UserRepository

    @Autowired
    lateinit var jwtUserDetailsService: JwtUserDetailsService

    @Autowired
    lateinit var jwtUtil: JwtUtil

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @CrossOrigin
    @GetMapping("/user")
    fun getCurrentUser(request: HttpServletRequest): ResponseEntity<User> {
        val principal = SecurityContextHolder.getContext().authentication.principal as UserDetails
        val user = repo.findByUsername(principal.username)
        return ResponseEntity.ok(user)
    }

    @PostMapping("/register")
    fun registerUser(@RequestBody user: Map<String, Any>): ResponseEntity<*> {
        val newUser = User(
            username = user["username"] as String,
            password = user["password"] as String,
            email = user["email"] as String,
            name = user["name"] as String,
            address = user["address"] as String,
            phone = user["phone"] as String
        )

        val validations = validateUserFields(newUser)
        if (validations.isNotEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validations.joinToString(separator = "\n"))
        }

        return try {
            val savedUser = jwtUserDetailsService.save(newUser)
            val tokenResponse = hashMapOf<String, Any>()
            val userDetails: UserDetails = jwtUserDetailsService.loadUserByUsername(savedUser.username!!)
            val token: String = jwtUtil.generateToken(userDetails)
            tokenResponse["token"] = token
            ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse)
        } catch (e: DataIntegrityViolationException) {
            handleDataIntegrityViolation(newUser, e)
        }
    }

    @PostMapping("/login")
    fun authenticateUser(@RequestBody user: Map<String, String>): ResponseEntity<*> {
        authenticate(user["username"]!!, user["password"]!!)

        val tokenResponse = hashMapOf<String, Any>()
        val userDetails: UserDetails = jwtUserDetailsService.loadUserByUsername(user["username"]!!)
        val token: String = jwtUtil.generateToken(userDetails)

        tokenResponse["token"] = token
        return ResponseEntity.ok(tokenResponse)
    }

    private fun authenticate(username: String, password: String) {
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
        } catch (e: DisabledException) {
            throw Exception("User disabled", e)
        } catch (e: BadCredentialsException) {
            throw Exception("Invalid credentials", e)
        }
    }

    private fun validateUserFields(user: User): List<String> {
        return mutableListOf<String>().apply {
            if (user.username == null) add("Username is missing.")
            if (user.email == null) add("Email is missing.")
            if (user.password == null) add("Password is missing.") else if (user.password.length < 8) add("Password length must be 8+.")
            if (user.name == null) add("Name is missing.")
            if (user.address == null) add("Address is missing.")
            if (user.phone == null) add("Phone is missing.")
        }
    }

    private fun handleDataIntegrityViolation(user: User, e: DataIntegrityViolationException): ResponseEntity<*> {
        val rootCauseMessage = e.rootCause?.message ?: ""
        when {
            rootCauseMessage.contains(user.username!!) -> return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is not available.")
            rootCauseMessage.contains(user.email!!) -> return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is not available.")
            else -> return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data integrity violation.")
        }
    }
}