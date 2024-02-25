package eu.phamtheanh.gogreen.repo

import eu.phamtheanh.gogreen.model.cart.CartItem
import eu.phamtheanh.gogreen.model.cart.CartItemPK
import org.springframework.data.jpa.repository.JpaRepository

interface CartItemRepository : JpaRepository<CartItem, CartItemPK>