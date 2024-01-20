package io.billie.common

/**
 * Response object returned when a REST method fails
 */
class ErrorResponse(exception: Throwable, includeMessage: Boolean = true) {
    val errorType: String = exception.javaClass.simpleName
    var message: String?

    init {
        if (includeMessage) message = exception.message
        else message = null
    }
}