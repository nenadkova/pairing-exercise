package io.billie.orders.model

import java.util.*

/**
 * Order sold by a merchamt
 */
data class Order(
        val id: UUID,
        /**
         * Total items that were sold and need to be shipped in this order
         */
        val totalItems: Int,
        /**
         * Time when the order is "placed"
         */
        val timePlaced: Date,
        val organisationId: UUID
)
