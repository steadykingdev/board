package com.study.board.controller.domain.dto;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonResponseFormat<T> {

    private static final String SUCCESS_STATUS = "success";
    private static final String FAIL_STATUS = "fail";
    private static final String ERROR_STATUS = "error";

    private String status;
    private T data;
    private String message;

    public static <T> CommonResponseFormat<T> createSuccess(T data) {
        return new CommonResponseFormat<>(SUCCESS_STATUS, data, null);
    }

    public static CommonResponseFormat<?> createSuccessWithNoContent() {
        return new CommonResponseFormat<>(SUCCESS_STATUS, null, null);
    }

    public static CommonResponseFormat<?> createFail(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();

        List<ObjectError> allErrors = bindingResult.getAllErrors();
        for (ObjectError error : allErrors) {
            if(error instanceof FieldError) {
                errors.put(((FieldError) error).getField(), error.getDefaultMessage());
            } else {
                errors.put(error.getObjectName(), error.getDefaultMessage());
            }
        }
        return new CommonResponseFormat<>(FAIL_STATUS, errors, null);
    }

    public static CommonResponseFormat<?> createError(String message) {
        return new CommonResponseFormat<>(ERROR_STATUS, null, message);
    }

    private CommonResponseFormat(String status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
