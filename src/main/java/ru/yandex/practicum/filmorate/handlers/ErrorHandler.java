package ru.yandex.practicum.filmorate.handlers;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private static final String NOT_FOUND_TITLE = "Ресурс не найден";
    private static final String VALIDATION_ERROR_TITLE = "Ошибка валидации";
    private static final String INTERNAL_ERROR_TITLE = "Внутренняя ошибка";

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleNotFoundException(final ResourceNotFoundException e) {
        log.error("{}: {}", NOT_FOUND_TITLE, e.getMessage());
        return new ErrorResponse(NOT_FOUND_TITLE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.error("{}: {}", VALIDATION_ERROR_TITLE, e.getMessage());
        return new ErrorResponse(VALIDATION_ERROR_TITLE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleValidationException(final HttpMessageNotReadableException e) {
        log.error("{}: {}", VALIDATION_ERROR_TITLE, e.getMessage());
        return new ErrorResponse(VALIDATION_ERROR_TITLE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleValidationException(final MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    String defaultMessage = fieldError.getDefaultMessage();
                    return fieldError.getField() + ": " + (defaultMessage != null ? defaultMessage : "Ошибка валидации");
                })
                .collect(Collectors.joining("; "));
        log.error("{}: {}", VALIDATION_ERROR_TITLE, errorMessage);
        return new ErrorResponse(VALIDATION_ERROR_TITLE, errorMessage);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleValidationException(final ConstraintViolationException e) {
        log.error("{}: {}", VALIDATION_ERROR_TITLE, e.getMessage());
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleOtherExceptions(final Exception e) {
        log.error("{}: {}", INTERNAL_ERROR_TITLE, e.getMessage());
        return new ErrorResponse(INTERNAL_ERROR_TITLE, e.getMessage());
    }

}
