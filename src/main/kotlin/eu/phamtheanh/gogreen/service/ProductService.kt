package eu.phamtheanh.gogreen.service

import eu.phamtheanh.gogreen.exceptions.ProductNotFoundException
import eu.phamtheanh.gogreen.model.Product
import eu.phamtheanh.gogreen.repo.ProductRepository
import org.springframework.stereotype.Service
import jakarta.transaction.Transactional

@Service
@Transactional
class ProductService(private val productRepository: ProductRepository) {

    fun getProducts(): MutableList<Product> = productRepository.findAll()

    fun getProduct(id: Long): Product =
        productRepository.findById(id).orElseThrow {
            ProductNotFoundException("Product by id $id was not found.")
        }

    fun addProduct(product: Product): Product = productRepository.save(product)

    fun updateProduct(id: Long, updatedProduct: Product): Product {
        val product: Product = getProduct(id).apply {
            name = updatedProduct.name
            description = updatedProduct.description
            price = updatedProduct.price
            image = updatedProduct.image
        }
        return productRepository.save(product)
    }

    fun deleteProduct(id: Long) = productRepository.deleteById(id)
}