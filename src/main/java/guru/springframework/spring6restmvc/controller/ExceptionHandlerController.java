package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.dto.ErrorInfo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorInfo>> handleBindErrors(MethodArgumentNotValidException ex) {
        List<ErrorInfo> errors = ex.getFieldErrors().stream()
                .map(fieldError ->
                    ErrorInfo.builder()
                            .fieldName(fieldError.getField())
                            .errorDescription(fieldError.getDefaultMessage())
                            .build()
                ).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<List<ErrorInfo>> handleDatabaseViolations(TransactionSystemException ex) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.badRequest();
        List<ErrorInfo> errors = new ArrayList<>();
        if(ex.getCause().getCause() instanceof ConstraintViolationException cause) {
            Set<ConstraintViolation<?>> constraintViolations = cause.getConstraintViolations();
            errors = constraintViolations.stream()
                    .map(cv ->
                            ErrorInfo.builder()
                                    .fieldName(cv.getPropertyPath().toString())
                                    .errorDescription(cv.getMessage())
                                    .build()
                    ).toList();
        }
        return builder.body(errors);
    }
}
