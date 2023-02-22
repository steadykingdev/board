package com.study.board.controller;

import com.study.board.domain.dto.CommonResponseFormat;
import com.study.board.exception.ForbiddenException;
import com.study.board.exception.PostNotFoundException;
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

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<CommonResponseFormat> handleForbiddenException(ForbiddenException ex) {
        return new ResponseEntity<>(CommonResponseFormat.createError(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<CommonResponseFormat> handleNotFoundException(PostNotFoundException ex) {
        return new ResponseEntity<>(CommonResponseFormat.createError(ex.getMessage()), HttpStatus.NOT_FOUND);
    }
}
