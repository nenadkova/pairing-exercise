package io.billie.orders.model

import com.fasterxml.jackson.annotation.JsonFormat
import io.billie.common.Constants
import java.util.*

data class OrderCreationRequest(
        /**
         * @see Order.totalItems
         */
        val totalItems: Int,
        @JsonFormat(pattern = Constants.DATE_JSON_FORMAT) val timePlaced: Date
)