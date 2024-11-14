package code.frontend.exception;

import code.modules.googleApi.InternalApiException;
import jakarta.validation.ConstraintViolationException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<UUID> handle(IllegalArgumentException ex) {
    UUID uuid = UUID.randomUUID();
    log.error("IllegalArgument: {} | Exception UUID: {}", ex, uuid);
    return  new ResponseEntity<>(uuid, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(PropertyReferenceException.class)
  public ResponseEntity<UUID> handle(PropertyReferenceException ex) {
    UUID uuid = UUID.randomUUID();
    log.error("PropertyReference: {} | Exception UUID: {}", ex, uuid);
    return new ResponseEntity<>(uuid, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<UUID> handle(MethodArgumentNotValidException ex) {
    UUID uuid = UUID.randomUUID();
    log.error("ArgumentNotValid: {} | Exception UUID: {}", ex, uuid);
    return new ResponseEntity<>(uuid, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<UUID> handle(ConstraintViolationException ex) {
    UUID uuid = UUID.randomUUID();
    log.error("ConstraintViolation: {} | Exception UUID: {}", ex, uuid);
    return new ResponseEntity<>(uuid, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InternalApiException.class)
  public ResponseEntity<UUID> handle(InternalApiException ex) {
    UUID uuid = UUID.randomUUID();
    log.error("InternalApiException: {} | Exception UUID: {}", ex, uuid);
    return new ResponseEntity<>(uuid, HttpStatus.FAILED_DEPENDENCY);
  }
}