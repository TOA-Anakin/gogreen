package eu.phamtheanh.gogreen.model.cart

import com.fasterxml.jackson.annotation.JsonIgnore
import eu.phamtheanh.gogreen.model.Product
import eu.phamtheanh.gogreen.model.User
import jakarta.persistence.*
import java.beans.Transient
import java.util.Date
import java.util.Objects

@Entity
@Table(name = "cart_item")
class CartItem(
    @EmbeddedId
    @JsonIgnore
    var pk: CartItemPK? = null,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    var addedOn: Date = Date(),

    @Column(nullable = false)
    var quantity: Int = 1
) {

    @Transient
    fun getProduct(): Product? = pk?.product

    @Transient
    fun getTotalPrice(): Double = quantity * (getProduct()?.price ?: 0.0)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as CartItem
        return Objects.equals(pk?.user?.id, other.pk?.user?.id) &&
                Objects.equals(getProduct()?.id, other.getProduct()?.id)
    }

    override fun hashCode(): Int {
        return Objects.hash(pk?.user?.id, getProduct()?.id)
    }
}