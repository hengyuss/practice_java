package com.restkeeper.operator.exception.handler;

import com.restkeeper.operator.exception.AccountException;
import com.restkeeper.utils.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(AccountException.class)
  public Result accountException(AccountException e) {
    return new Result().error(e.getMessage()) ;
  }

}
