package io.billie.common

/**
 * Exception used to indicate that a failure occurred due to an invalid object/data supplied to the service
 */
class ValidationException(message: String) : Exception(message) {}