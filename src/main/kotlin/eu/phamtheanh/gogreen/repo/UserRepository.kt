package eu.phamtheanh.gogreen.repo

import eu.phamtheanh.gogreen.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {
    override fun deleteById(id: Long)
    fun findByUsername(username: String): User
    override fun findById(id: Long): Optional<User>
}