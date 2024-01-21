package io.billie.orders.service

import com.fasterxml.jackson.annotation.JsonFormat
import io.billie.common.Constants
import io.billie.orders.model.Order
import java.util.*

data class OrderCreationRequest(

        /**
         * @see Order.totalItems
         */
        val totalItems: Int,
        /**
         * Time when the order was placed
         * @see Order.timePlaced
         */
        @JsonFormat(pattern = Constants.DATE_JSON_FORMAT) val timePlaced: Date
)