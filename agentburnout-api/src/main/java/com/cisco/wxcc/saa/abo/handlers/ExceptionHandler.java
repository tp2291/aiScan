package com.cisco.wxcc.saa.abo.handlers;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@ResponseStatus
public class ExceptionHandler
        extends ResponseEntityExceptionHandler {


}
