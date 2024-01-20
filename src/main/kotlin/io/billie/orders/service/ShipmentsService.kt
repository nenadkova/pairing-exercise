package io.billie.orders.service

import io.billie.common.EntityNotFoundException
import io.billie.common.ValidationException
import io.billie.orders.data.OrdersRepository
import io.billie.orders.data.ShipmentRepository
import io.billie.orders.model.Order
import io.billie.orders.model.Shipment
import io.billie.orders.resource.ShipmentCreateRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
class ShipmentsService(val db: ShipmentRepository, val ordersDb: OrdersRepository) {
    fun findShipments(): List<Shipment> = db.findAll().toList()

    fun findShipmentById(id: UUID): List<Shipment> = db.findById(id).toList()

    fun save(shipment: Shipment) {
        db.save(shipment)
    }

    fun <T : Any> Optional<out T>.toList(): List<T> = if (isPresent) listOf(get()) else emptyList()

    /**
     * Retrieves all shipments for an order
     * @param orgId id of the org that order belongs to
     * @param orderId id of the order for which shipments are retrieved
     */
    fun findOrderShipments(orgId: UUID, orderId: UUID): List<Shipment> {
        assertOrderExists(orgId, orderId)
        return db.findAll().filter { shipment -> shipment.orderId == orderId }
    }

    /**
     * @throws EntityNotFoundException
     */
    private fun assertOrderExists(orgId: UUID, orderId: UUID): Order {
        val order: Order = ordersDb.findOrder(orderId) ?: throw EntityNotFoundException(
            "Order with specified orderId does not exist"
        )
        if (order.organisationId != orgId) throw EntityNotFoundException(
            "Order with specified orderId does not exist in this organisation"
        )
        return order
    }

    /**
     * Notify Billie that a shipment for an order has been sent to the customer
     * @param orgId Billie's id of the order
     * @param orderId Billie's id of the merchant
     * @throws EntityNotFoundException if order cannot be found
     * @throws ValidationException  if shipment request is not valid
     * @throws ItemsShippedExceedOrderException
     * @throws OrerFulfilledException
     */
    fun onShipmentSent(orgId: UUID, orderId: UUID, shipmentRequest: ShipmentCreateRequest): UUID {
        val order = assertOrderExists(orgId, orderId)
        assertValid(order, shipmentRequest)
        val shipment = Shipment(null, shipmentRequest.shippedItems, shipmentRequest.timeShipped, orderId)
        db.save(shipment)
        sendOrderFulfilledEvent(order)
        return shipment.id!!
    }

    private fun sendOrderFulfilledEvent(order: Order) {
        // implement to initiate funds transfer to the merchant
    }

    /**
     * @throws ValidateExcption in case validation fails
     */
    private fun assertValid(order: Order, shipmentRequest: ShipmentCreateRequest) {
        if (shipmentRequest.shippedItems < 1) throw ValidationException("Shipment with no items")
        var shippedSoFar = 0;
        for (shipment in db.findByOrderId(order.id)) {
            shippedSoFar += shipment.shippedItems
        }
        if (shippedSoFar == order.totalItems) throw OrderFulfilledException()
        if (shipmentRequest.shippedItems + shippedSoFar > order.totalItems) throw ItemsShippedExceedOrderException("""
           Number of shipped items (${shippedSoFar + shipmentRequest.shippedItems}) 
           exceeds order total (${order.totalItems}) 
        """.trimIndent())
    }

}