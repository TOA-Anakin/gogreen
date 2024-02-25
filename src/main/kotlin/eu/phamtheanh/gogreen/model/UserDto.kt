package eu.phamtheanh.gogreen.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import eu.phamtheanh.gogreen.model.cart.CartItem
import jakarta.persistence.OneToMany

data class UserDto(
    var id: Long = 0,
    var username: String = "",
    var password: String = "",
    var email: String = "",
    var name: String = "",
    var address: String = "",
    var phone: String = "",
    @JsonManagedReference
    @OneToMany(mappedBy = "pk.user")
    var cartItems: MutableList<CartItem> = mutableListOf()
)
