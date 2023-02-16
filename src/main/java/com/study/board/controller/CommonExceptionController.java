package com.study.board.exception;

import com.study.board.dto.CommonResponseFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommonExceptionController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponseFormat> handleValidationException(BindingResult bindingResult) {
        return new ResponseEntity<>(CommonResponseFormat.createFail(bindingResult), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResponseFormat> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(CommonResponseFormat.createError(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
