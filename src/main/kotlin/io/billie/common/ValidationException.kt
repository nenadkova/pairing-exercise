package io.billie.common

/**
 * Exception used to indicate that a failure occurred due to an invalid object/data supplied to the service
 */
open class ValidationException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)
