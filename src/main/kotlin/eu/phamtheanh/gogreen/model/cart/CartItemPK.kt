package eu.phamtheanh.gogreen.model.cart

import com.fasterxml.jackson.annotation.JsonBackReference
import eu.phamtheanh.gogreen.model.Product
import eu.phamtheanh.gogreen.model.User
import jakarta.persistence.*
import java.io.Serializable

@Embeddable
class CartItemPK(
    @JsonBackReference
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: Product? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CartItemPK

        if (user != other.user) return false
        if (product != other.product) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user?.hashCode() ?: 0
        result = 31 * result + (product?.hashCode() ?: 0)
        return result
    }
}