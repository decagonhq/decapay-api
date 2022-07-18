package com.decagon.decapay.config;

import com.decagon.decapay.config.apiresponse.ApiDataResponse;
import com.decagon.decapay.exception.*;
import com.decagon.decapay.util.ApiResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
@RestController
@Slf4j
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiDataResponse<?> apiResponse = new ApiDataResponse<>(HttpStatus.BAD_REQUEST);
        apiResponse.setErrorCode("Validation Failed");
        apiResponse.addValidationErrors(ex.getBindingResult().getFieldErrors());
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(DecapayException.EntityNotFoundException.class)
    public final ResponseEntity handleNotFoundExceptions(Exception ex, WebRequest request) {
        return ApiResponseUtils.errorResponse(HttpStatus.NOT_FOUND,ex.getMessage());
    }

    @ExceptionHandler(DecapayException.DuplicateEntityException.class)
    public final ResponseEntity handleDuplicateEntityExceptions(Exception ex, WebRequest request) {
        return ApiResponseUtils.errorResponse(HttpStatus.CONFLICT,ex.getMessage());
    }

    @ExceptionHandler(DecapayException.UnAuthorizedException.class)
    public final ResponseEntity handleUnAuthorizeExceptions(Exception ex, WebRequest request) {
        return ApiResponseUtils.errorResponse(HttpStatus.UNAUTHORIZED,ex.getMessage());
    }

    @ExceptionHandler(UnAuthorizedException.class)
    public final ResponseEntity handleUnAuthorizedExceptions(Exception ex, WebRequest request) {
        return ApiResponseUtils.errorResponse(HttpStatus.UNAUTHORIZED,ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity handleResourceNotFoundException(ResourceNotFoundException e) {
        return ApiResponseUtils.errorResponse(HttpStatus.NOT_FOUND,e.getMessage());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity handleInvalidRequestException(InvalidRequestException e) {
        if(e.getBindingResult()!=null||e.getValidationErrors()!=null){
            ApiDataResponse<?> ar = new ApiDataResponse<>(HttpStatus.BAD_REQUEST);
            if(e.getBindingResult()!=null){
                ar.addValidationError(e.getBindingResult().getAllErrors());
            }else{
                ar.addValidationErrors(e.getValidationErrors().getFieldErrors());
            }
            return new ResponseEntity<>(ar, ar.getStatus());
        }else{
            String errorMsg="";
            errorMsg+=e.getMessage();
            String errorCode=errorMsg;//TODO: use approrpriate code
            return ApiResponseUtils.errorResponse(HttpStatus.BAD_REQUEST,errorMsg,"",errorCode);
        }
    }

    @ExceptionHandler(DecapaySystemException.class)
    public ResponseEntity handleFellowshipSystemException(DecapaySystemException e) {
        //http status code,custom errCd,errMsg,dbgMsg
        log.error(e.getMessage(), e);
        return ApiResponseUtils.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,"An unknown error has occured",e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        //http status code,custom errCd,errMsg,dbgMsg
        log.error(e.getMessage(), e);
        return ApiResponseUtils.errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,"An unknown error has occured",e.getMessage());
    }

    @ExceptionHandler(UnAuthorizeEntityAccessException.class)
    public  ResponseEntity handleUnAuthorizedEntityExceptions(Exception ex, WebRequest request) {
        return ApiResponseUtils.errorResponse(HttpStatus.FORBIDDEN,ex.getMessage());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity handleServiceException(InvalidRefreshTokenException e, WebRequest request) {
        return ApiResponseUtils.errorResponse(HttpStatus.UNAUTHORIZED, e.getErrorMessage(), e.getLocalizedMessage(), e.getErrorCode());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity handleServiceException(InvalidCredentialsException e, WebRequest request) {
        return ApiResponseUtils.errorResponse(HttpStatus.BAD_REQUEST, e.getErrorMessage(), e.getLocalizedMessage(), e.getErrorCode());
    }

    @ExceptionHandler(AccountDisabledException.class)
    public ResponseEntity handleServiceException(AccountDisabledException e, WebRequest request) {
        return ApiResponseUtils.errorResponse(HttpStatus.FORBIDDEN, e.getErrorMessage(), e.getLocalizedMessage(), e.getErrorCode());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity handleAccessDeniedException(AccessDeniedException e, WebRequest request) {
        return ApiResponseUtils.errorResponse(HttpStatus.FORBIDDEN, "Attempting to access an unauthorized resource", e.getLocalizedMessage(), "");
    }
}
