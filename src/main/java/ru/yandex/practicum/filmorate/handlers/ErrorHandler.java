package ru.yandex.practicum.filmorate.handlers;

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

@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleNotFoundException(final ResourceNotFoundException e) {
        return new ErrorResponse("Ресурс не найден", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse("Ошибка валидации", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleValidationException(final HttpMessageNotReadableException e) {
        return new ErrorResponse("Ошибка валидации", e.getMessage());
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
        return new ErrorResponse("Ошибка валидации", errorMessage);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleOtherExceptions(final Exception e) {
        return new ErrorResponse("Внутренняя ошибка", e.getMessage());
    }

}
