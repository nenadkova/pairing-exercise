package io.billie.common

/**
 * Exception thrown to indicate that a method/operation/service failed because a required entity could not be found
 */
class EntityNotFoundException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)