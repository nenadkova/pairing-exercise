package io.billie.orders.service

import io.billie.common.ValidationException

class ItemsShippedExceedOrderException(message: String) : ValidationException(message) {

}
