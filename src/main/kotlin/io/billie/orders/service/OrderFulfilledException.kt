package io.billie.orders.service

import io.billie.common.ValidationException

class OrderFulfilledException : ValidationException(message = "Order has been fulfilled") {}
