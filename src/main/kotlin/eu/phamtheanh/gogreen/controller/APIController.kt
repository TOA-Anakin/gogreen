package eu.phamtheanh.gogreen.controller

import eu.phamtheanh.gogreen.config.JwtUtil
import eu.phamtheanh.gogreen.exceptions.ProductNotFoundException
import eu.phamtheanh.gogreen.exceptions.UserNotFoundException
import eu.phamtheanh.gogreen.model.cart.CartItem
import eu.phamtheanh.gogreen.model.cart.CartItemPK
import eu.phamtheanh.gogreen.model.Product
import eu.phamtheanh.gogreen.model.User
import eu.phamtheanh.gogreen.model.UserDto
import eu.phamtheanh.gogreen.service.CartItemService
import eu.phamtheanh.gogreen.service.JwtUserDetailsService
import eu.phamtheanh.gogreen.service.ProductService
import eu.phamtheanh.gogreen.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import jakarta.transaction.Transactional

@RestController
@CrossOrigin
@RequestMapping("/api")
class APIController(
    val userService: UserService,
    val productService: ProductService,
    val cartItemService: CartItemService
) {

    @Autowired
    lateinit var jwtUserDetailsService: JwtUserDetailsService

    @Autowired
    lateinit var jwtUtil: JwtUtil

    @PostMapping("/create-token")
    fun createToken(@RequestBody user: Map<String, String>): ResponseEntity<*> {
        val username = user["username"]
        if (username.isNullOrEmpty()) {
            return ResponseEntity.badRequest().body("Username is required")
        }

        val userDetails: UserDetails = jwtUserDetailsService.loadUserByUsername(username)
        val token: String = jwtUtil.generateToken(userDetails)

        val tokenResponse = hashMapOf<String, Any>()
        tokenResponse["token"] = token
        return ResponseEntity.ok(tokenResponse)
    }

    @GetMapping("/users")
    fun getUsers(): ResponseEntity<List<User>> {
        val users: List<User> = userService.getUsers().toList()
        return ResponseEntity(users, HttpStatus.OK)
    }

    @GetMapping("/users/{id}")
    fun getUser(@PathVariable("id") id: Long): ResponseEntity<User> = ResponseEntity(userService.getUser(id), HttpStatus.OK)

    @PutMapping("/users/{id}")
    fun updateUser(@PathVariable("id") id: Long, @RequestBody user: Map<String, Any>): ResponseEntity<User> {
        val newUser = User(
            username = user["username"] as String,
            password = user["password"] as String,
            email = user["email"] as String,
            name = user["name"] as String,
            address = user["address"] as String,
            phone = user["phone"] as String
        )

        return ResponseEntity(userService.updateUser(id, newUser), HttpStatus.OK)
    }

    @GetMapping("/users/{id}/cart")
    fun getUserCart(@PathVariable("id") id: Long): ResponseEntity<List<CartItem>> {
        println(userService.getUser(id).cartItems.size)
        return ResponseEntity(userService.getUser(id).cartItems, HttpStatus.OK)
    }

    @PostMapping("/users/{id}/cart/add/{productId}")
    fun addToUserCart(@PathVariable("id") id: Long, @PathVariable("productId") productId: Long): ResponseEntity<User> {
        val user = userService.getUser(id) ?: throw UserNotFoundException("User with id $id not found")
        val product = productService.getProduct(productId) ?: throw ProductNotFoundException("Product with id $productId not found")

        val cartItemPK = CartItemPK(user, product)
        val cartItem = CartItem(pk = cartItemPK)

        cartItemService.addCartItem(cartItem)

        return ResponseEntity(userService.getUser(id), HttpStatus.CREATED)
    }
}