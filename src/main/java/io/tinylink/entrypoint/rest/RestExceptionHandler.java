package io.tinylink.entrypoint.rest;

import io.tinylink.core.error.InvalidUrlException;
import io.tinylink.core.error.LinkNotFoundException;
import io.tinylink.core.error.UserAlreadyExistsException;
import io.tinylink.entrypoint.rest.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(LinkNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiError notFound(LinkNotFoundException ex, HttpServletRequest request) {
        return ApiError.of(404, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ApiError conflict(UserAlreadyExistsException ex, HttpServletRequest request) {
        return ApiError.of(409, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ApiError unauthorized(BadCredentialsException ex, HttpServletRequest request) {
        return ApiError.of(401, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidUrlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError invalidUrl(InvalidUrlException ex, HttpServletRequest request) {
        return ApiError.of(400, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError validation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(RestExceptionHandler::describe)
                .toList();
        return new ApiError(java.time.Instant.now(), 400, "Dados inválidos", request.getRequestURI(), violations);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError badRequest(Exception ex, HttpServletRequest request) {
        return ApiError.of(400, ex.getMessage(), request.getRequestURI());
    }

    private static String describe(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
