package eu.phamtheanh.gogreen.repo

import eu.phamtheanh.gogreen.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ProductRepository : JpaRepository<Product, Long> {
    override fun deleteById(id: Long)
    override fun findById(id: Long): Optional<Product>
}