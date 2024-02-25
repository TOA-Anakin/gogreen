package eu.phamtheanh.gogreen.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.util.Date

@Entity
@Table(name = "products")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, length = 128)
    var name: String = "",

    @Column(nullable = false, length = 4000)
    var description: String = "",

    @Column(nullable = false, precision = 10, scale = 2)
    var price: Double = 0.0,

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    var addedOn: Date = Date(),

    @Lob
    @Column(nullable = true, length = Integer.MAX_VALUE)
    @JsonIgnoreProperties("hibernateLazyInitializer", "handler")
    var image: ByteArray? = null
) {
    constructor(name: String, description: String, price: Double) : this() {
        this.name = name
        this.description = description
        this.price = price
    }

    constructor(name: String, description: String, price: Double, image: ByteArray) : this(name, description, price) {
        this.image = image
    }
}