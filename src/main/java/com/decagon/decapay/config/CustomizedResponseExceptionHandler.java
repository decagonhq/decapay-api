package com.decagon.decapay.config;

import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.decagon.decapay.exception.InvalidCredentialException;
import com.decagon.decapay.exception.ResourceConflictException;
import com.decagon.decapay.exception.InvalidRequestException;
import com.decagon.decapay.exception.ResourceNotFoundException;
import com.decagon.decapay.utils.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
@Slf4j
public class CustomizedResponseExceptionHandler extends ResponseEntityExceptionHandler {


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiDataResponse<?> apiResponse = new ApiDataResponse<>(HttpStatus.BAD_REQUEST);
        apiResponse.setErrorCode("Validation Failed");
        apiResponse.addValidationErrors(ex.getBindingResult().getFieldErrors());
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ResourceConflictException.class)
    public final ResponseEntity handleDuplicateEntityExceptions(Exception ex, WebRequest request) {
        return ApiResponseUtil.errorResponse(HttpStatus.CONFLICT,ex.getMessage());
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity handleResourceNotFoundException(ResourceNotFoundException e) {
        return ApiResponseUtil.errorResponse(HttpStatus.NOT_FOUND,e.getMessage());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity handleResourceNotFoundException(InvalidRequestException e) {
        return ApiResponseUtil.errorResponse(HttpStatus.BAD_REQUEST,e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ApiResponseUtil.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,"An unknown error has occurred", e.getMessage());
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity handleBadCredentialsException(InvalidCredentialException e) {
        System.out.println("***************" + e.getErrorMessage());
        return ApiResponseUtil.errorResponse(HttpStatus.BAD_REQUEST,e.getErrorMessage());
    }
}
