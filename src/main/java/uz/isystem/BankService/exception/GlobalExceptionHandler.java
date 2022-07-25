package uz.isystem.BankService.exception;

import org.postgresql.util.PSQLException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<?> handler(BadRequest exception){
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

/*    @ExceptionHandler
    public ResponseEntity<?> handler(PSQLException exception){
        return ResponseEntity.badRequest().body(exception.getMessage());
    }*/
}
