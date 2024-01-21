package io.billie.orders.data

import io.billie.orders.model.Order
import org.springframework.data.repository.CrudRepository
import java.util.*

interface OrderRepository: CrudRepository<Order, UUID> {
    fun findByOrganisationId(orgId: UUID): List<Order>
}