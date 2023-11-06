package com.saqaya.exception

import com.saqaya.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.function.Consumer


@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleResourceNotFound(ex: ResourceNotFoundException): ResponseEntity<Any> {
        val error = ErrorResponse(ex.javaClass.getSimpleName(), ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }

    @ExceptionHandler(ResourceAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleResourceAlreadyRegistered(ex: ResourceAlreadyExistsException): ResponseEntity<Any> {
        val error = ErrorResponse(ex.javaClass.getSimpleName(), ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(AuthenticationFailedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAuthenticationFailedException(ex: AuthenticationFailedException): ResponseEntity<Any> {
        val error = ErrorResponse(ex.javaClass.getSimpleName(), ex.message)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: org.springframework.http.HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val fieldErrors = mutableMapOf<String, String>()
        ex.bindingResult.allErrors.forEach(Consumer { error: ObjectError ->
            val fieldName = (error as FieldError).field
            val errorMessage: String = error.getDefaultMessage() ?: "r"
            fieldErrors[fieldName] = errorMessage
        })
        val error = ErrorResponse("ValidationException", "Invalid fields", fieldErrors)
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    override fun handleExceptionInternal(
        ex: java.lang.Exception,
        body: Any?,
        headers: org.springframework.http.HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val error = ErrorResponse("JsonParseException", "Could not parse Json")
        return ResponseEntity(error, HttpStatus.BAD_REQUEST)
    }

    // catch any other exception for standard error message handling
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: org.springframework.http.HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val error = ErrorResponse("InternalErrorException", ex.message)
        return ResponseEntity<Any>(error, headers, status)
    }
}