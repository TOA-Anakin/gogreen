package eu.phamtheanh.gogreen.service

import eu.phamtheanh.gogreen.exceptions.CartItemAlreadyExistsException
import eu.phamtheanh.gogreen.exceptions.CartItemDoesNotExistsException
import eu.phamtheanh.gogreen.model.cart.CartItem
import eu.phamtheanh.gogreen.repo.CartItemRepository
import org.springframework.stereotype.Service
import jakarta.transaction.Transactional

@Service
@Transactional
class CartItemService(private val repo: CartItemRepository) {

    fun getCartItems(): MutableList<CartItem> = repo.findAll()

    fun getCartItem(userId: Long, productId: Long): CartItem =
        getCartItems().find { it.pk?.user?.id == userId && it.pk?.product?.id == productId }
            ?: throw CartItemDoesNotExistsException("Cart item w/ user id $userId and product id $productId does not exist.")

    fun addCartItem(cartItem: CartItem): CartItem {
        if (getCartItems().any { it == cartItem }) {
            throw CartItemAlreadyExistsException(
                "Cart item w/ user id ${cartItem.pk?.user?.id} and product id ${cartItem.pk?.product?.id} already exists."
            )
        }
        return repo.save(cartItem)
    }

    fun updateCartItem(cartItem: CartItem): CartItem {
        return getCartItems().find { it == cartItem }?.let {
            it.quantity = cartItem.quantity
            repo.save(it)
        } ?: throw CartItemDoesNotExistsException(
            "Cart item w/ user id ${cartItem.pk?.user?.id} and product id ${cartItem.pk?.product?.id} does not exist."
        )
    }

    fun deleteCartItem(userId: Long, productId: Long) {
        getCartItems().find { it.pk?.user?.id == userId && it.pk?.product?.id == productId }?.let {
            repo.delete(it)
        } ?: throw CartItemDoesNotExistsException("Cart item w/ user id $userId and product id $productId does not exist.")
    }
}