package io.billie.orders.service

import com.fasterxml.jackson.annotation.JsonFormat
import io.billie.common.Constants
import java.util.*

/**
 * Details of a shipment that is shipped by a merchant
 */
data class ShipmentCreationRequest (
    /**
     * Number of items in this shipment
     */
    val shippedItems: Int,

    /**
     * Date when the shipment was sent
     */
    @JsonFormat(pattern = Constants.DATE_JSON_FORMAT) val timeShipped: Date

)
