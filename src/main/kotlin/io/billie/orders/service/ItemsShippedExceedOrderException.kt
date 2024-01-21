package io.billie.orders.service

import io.billie.common.ValidationException

/**
 * Thrown to indicate that an attempt to post a shipment would exceed the total items sold in the related order
 */
class ItemsShippedExceedOrderException(message: String) : ValidationException(message) {}
