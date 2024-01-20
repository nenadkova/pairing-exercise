package io.billie.orders.data

import io.billie.orders.model.Shipment
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ShipmentRepository: CrudRepository<Shipment, UUID> {
    fun findByOrderId(orderID: UUID): List<Shipment>

}