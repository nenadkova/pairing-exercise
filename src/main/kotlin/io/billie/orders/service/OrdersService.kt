package io.billie.orders.service

import io.billie.orders.data.OrdersRepository
import io.billie.orders.model.Order
import io.billie.orders.model.OrderCreationRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrdersService(val db: OrdersRepository) {

    /**
     * List all orders for the given merchant (organisation)
     * @param orgId unique Billie's organisation identifier
     */
    fun findOrganisationOrders(orgId: UUID): List<Order> = db.findOrders(orgId)

    /**
     * @param orgId id of the merchant that sold the order
     * @param order details of the order
     * @return
     */
    fun createOrder(orgId: UUID, order: OrderCreationRequest): UUID {
        return db.createOrder(orgId, order)
    }
}