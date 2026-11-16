package dev.reliefgrid.api;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.dao.DataIntegrityViolationException;
@RestControllerAdvice public class ApiExceptionHandler {
 @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<Dto.ApiError> bad(IllegalArgumentException e){return ResponseEntity.badRequest().body(new Dto.ApiError("INVALID_REQUEST",e.getMessage()));}
 @ExceptionHandler(IllegalStateException.class) ResponseEntity<Dto.ApiError> conflict(IllegalStateException e){return ResponseEntity.status(409).body(new Dto.ApiError("CONFLICT",e.getMessage()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<Dto.ApiError> invalid(MethodArgumentNotValidException e){return ResponseEntity.badRequest().body(new Dto.ApiError("VALIDATION",e.getFieldErrors().isEmpty()?"Invalid input":e.getFieldErrors().get(0).getField()+" "+e.getFieldErrors().get(0).getDefaultMessage()));}
 @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<Dto.ApiError> unique(DataIntegrityViolationException e){return ResponseEntity.status(409).body(new Dto.ApiError("CONSTRAINT", "Database constraint violated"));}
}
