package io.billie.orders.service

import io.billie.common.ValidationException
import io.billie.orders.data.OrderRepository
import io.billie.orders.model.Order
import org.springframework.stereotype.Service
import java.util.*

/**
 * Manages Orders
 */
@Service
class OrderService(val db: OrderRepository) {

    /**
     * List all orders for the given merchant (organisation)
     * @param orgId unique Billie's organisation identifier
     */
    fun findOrganisationOrders(orgId: UUID): List<Order> = db.findByOrganisationId(orgId)

    /**
     * @param orgId id of the merchant that sold the order
     * @param orderRequest details of the order
     * @throws ValidationException in case order request is invalid
     */
    fun createOrder(orgId: UUID, orderRequest: OrderCreationRequest): UUID {
        assertValid(orderRequest)
        val order = Order(null, orderRequest.totalItems, orderRequest.timePlaced, orgId)
        db.save(order)
        return order.id!!
    }

    private fun assertValid(orderCreationRequest: OrderCreationRequest) {
        if (orderCreationRequest.totalItems < 1) throw ValidationException("totalItems must be greater than 1")
    }

}