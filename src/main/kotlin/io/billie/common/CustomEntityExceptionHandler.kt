package io.billie.common

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.lang.Nullable
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.util.WebUtils


@ControllerAdvice(annotations = [RestController::class])
class CustomEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ValidationException::class)
    fun handle(ex: ValidationException, request: WebRequest): ResponseEntity<ErrorResponse> =
        ResponseEntity(ErrorResponse(ex), HttpStatus.BAD_REQUEST)

    @ExceptionHandler(EntityNotFoundException::class)
    fun handle(ex: EntityNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity(ErrorResponse(ex), HttpStatus.NOT_FOUND)

    /**
     * Hanlde internal spring exceptions to return ErrorResponse
     * @see ErrorResponse
     */
    override fun handleExceptionInternal(
        ex: Exception,
        @Nullable body: Any?,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        if (HttpStatus.INTERNAL_SERVER_ERROR == status) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST)
        }
        val errorBody : ErrorResponse = ErrorResponse(ex, false)
        return ResponseEntity(errorBody, headers, status)
    }
}