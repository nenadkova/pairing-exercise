package io.billie.orders.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

data class OrderCreationRequest(
        /**
         * @see Order.totalItems
         */
        val totalItems: Int,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ") val timePlaced: Date?,
)