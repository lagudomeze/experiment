package com.phi.common.config;

import com.phi.common.BizError;
import com.phi.common.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BizException.class})
    public ResponseEntity<BizError> bizException(BizException ex) {
        log.warn("业务异常", ex);
        return new ResponseEntity<>(ex.getError(), ex.getStatus());
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<BizError> common(Exception ex) {
        BizError message = BizError.error(ex.getMessage());
        log.warn("未知异常", ex);
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}