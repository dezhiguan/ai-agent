package com.threekingdoms.quiz.common.exception;

import com.threekingdoms.quiz.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  @ResponseStatus(HttpStatus.OK)
  public Result handleBusiness(BusinessException ex) {
    return new Result(ex.getCode(), ex.getMessage(), null);
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
  @ResponseStatus(HttpStatus.OK)
  public Result handleValidation(Exception ex) {
    String message = "请求参数不合法";
    if (ex instanceof MethodArgumentNotValidException m) {
      var fieldError = m.getBindingResult().getFieldError();
      if (fieldError != null && fieldError.getDefaultMessage() != null) {
        message = fieldError.getDefaultMessage();
      }
    } else if (ex instanceof BindException b) {
      var fieldError = b.getBindingResult().getFieldError();
      if (fieldError != null && fieldError.getDefaultMessage() != null) {
        message = fieldError.getDefaultMessage();
      }
    }
    return new Result(400, message, null);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.OK)
  public Result handleOther(Exception ex) {
    log.error("Unhandled exception", ex);
    return Result.error("服务器内部错误");
  }
}
