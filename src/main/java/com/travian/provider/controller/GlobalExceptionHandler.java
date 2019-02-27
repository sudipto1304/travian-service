package com.travian.provider.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travian.provider.exception.TaskExecutionException;
import com.travian.provider.response.Status;

@SuppressWarnings("Duplicates")
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{
	
	private static final Logger Log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(TaskExecutionException.class)
    protected ResponseEntity<Object> handleGlobalException(TaskExecutionException ex, WebRequest request) {
        Status status  = new Status("TASK.EXECUTION.EXCEPTION", 400);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return handleExceptionInternal(ex, constructJsonResponse(status),
                headers, HttpStatus.EXPECTATION_FAILED, request);
    }
	
	
	public static String constructJsonResponse(Object object){
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            return mapper.writeValueAsString(object);
        }catch (Exception e){
            if(Log.isErrorEnabled())
                Log.error("Exception::", e);
        }
        return null;

    }


}
