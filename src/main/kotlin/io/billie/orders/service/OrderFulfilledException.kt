package io.billie.orders.service

import io.billie.common.ValidationException

/**
 * Thrown to indicate that a service failed to post a shipment for an order that has been already fulfilled
 */
class OrderFulfilledException : ValidationException(message = "Order has been fulfilled") {}
