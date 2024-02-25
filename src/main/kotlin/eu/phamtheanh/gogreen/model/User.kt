package eu.phamtheanh.gogreen.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import eu.phamtheanh.gogreen.model.cart.CartItem
import jakarta.persistence.*
import java.beans.Transient

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(unique = true, nullable = false, length = 35)
    var username: String = "",

    @Column(nullable = false, length = 128)
    var password: String = "",

    @Column(unique = true, nullable = false, length = 100)
    var email: String = "",

    @Column(nullable = false, length = 100)
    var name: String = "",

    @Column(nullable = false, length = 128)
    var address: String = "",

    @Column(nullable = false, length = 15)
    var phone: String = "",

    @JsonManagedReference
    @OneToMany(mappedBy = "pk.user", cascade = [CascadeType.ALL])
    var cartItems: List<CartItem> = mutableListOf()
) {
    constructor(username: String, password: String, email: String, name: String, address: String, phone: String) : this() {
        this.username = username
        this.password = password
        this.email = email
        this.name = name
        this.address = address
        this.phone = phone
    }

    @Transient
    fun getCartTotal(): Double = cartItems.sumOf { it.getTotalPrice() }

    override fun toString(): String {
        return "User(id=$id, username='$username', password='$password', email='$email', name='$name', address='$address', phone='$phone', cartItems=$cartItems)"
    }
}