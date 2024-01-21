package io.billie.orders.model

import com.fasterxml.jackson.annotation.JsonFormat
import io.billie.common.Constants
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

/**
 * Order sold by a merchamt
 */
@Table(schema = "organisations_schema", name = "orders")
data class Order(
        @Id var id: UUID?,
        /**
         * Total items that were sold and need to be shipped in this order
         */
        val totalItems: Int,
        /**
         * Time when the order is "placed"
         */
        @JsonFormat(pattern = Constants.DATE_JSON_FORMAT)val timePlaced: Date,
        val organisationId: UUID
)
