package com.decagon.decapay.utils;

import com.decagon.decapay.apiresponse.ApiDataResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
@Slf4j
public class ApiResponseUtil {

    public static  <T> ResponseEntity<ApiDataResponse<T>> response(HttpStatus status, T data, String message ){
        return ApiResponseUtil.getResponse(status,data,message);
    }

    public static  <T> ResponseEntity<ApiDataResponse<T>> response(HttpStatus status, T data){
        String message=null;
        if(status.equals(HttpStatus.OK)){
            message="Success";
        }
        return ApiResponseUtil.getResponse(status,data,message);
    }

    private static  <T> ResponseEntity<ApiDataResponse<T>> getResponse(HttpStatus status, T data, String message ){
        ApiDataResponse<T> ar = new ApiDataResponse<>(HttpStatus.OK);
        ar.setData(data);
        ar.setMessage(message);
        return new ResponseEntity<>(ar,status);
    }

    public static  <T> ResponseEntity<ApiDataResponse<T>> response(HttpStatus status, String message ){
        return ApiResponseUtil.getResponse(status,null,message);
    }

    public static  ResponseEntity<ApiDataResponse> errorResponse(HttpStatus status, String errMsg, String debugMsg, String customErrCd ){
        return ApiResponseUtil.getErrResponse(status,errMsg,debugMsg,customErrCd);
    }

    private static ResponseEntity<ApiDataResponse> getErrResponse(HttpStatus status, String errMsg, String debugMsg, String customErrCd  ){
        ApiDataResponse ar = new ApiDataResponse<>(status);
        ar.setMessage(errMsg);
        ar.setErrorCode(customErrCd);
        ar.setDebugMessage(debugMsg);
        return new ResponseEntity<>(ar,status);
    }

    public static ResponseEntity<ApiDataResponse> errorResponse(HttpStatus status, String errMsg, String debugMsg ){
        return ApiResponseUtil.getErrResponse(status,errMsg,debugMsg,null);
    }

    public static ResponseEntity<ApiDataResponse> errorResponse(HttpStatus status, String errMsg){
        return ApiResponseUtil.getErrResponse(status,errMsg,null,null);
    }

    public static void writeErrorResponse(String errMsg, HttpServletResponse response, HttpStatus httpStatus) {
        try {
            ApiDataResponse ar = new ApiDataResponse<>(httpStatus);
            ar.setMessage(errMsg);
            response.setStatus(httpStatus.value());
            response.setContentType("application/json");
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            PrintWriter out = response.getWriter();
            out.write(mapper.writeValueAsString(ar));
        } catch (Exception e) {
            log.error("Unknown error", e);
        }
    }

}
