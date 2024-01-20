package io.billie.orders.model

import com.fasterxml.jackson.annotation.JsonFormat
import io.billie.common.Constants
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

/**
 * Shipment that the merchant sends to a buyer to fulfill an order
 */
@Table(schema = "organisations_schema", name = "shipments")
data class Shipment(
        @Id var id: UUID?,
        /**
         * Number of items in this shipment.
         */
        val shippedItems: Int,
        /**
         * Time when the shipment was sent
         */
        @JsonFormat(pattern = Constants.DATE_JSON_FORMAT)val timeShipped: Date,
        val orderId: UUID
)
