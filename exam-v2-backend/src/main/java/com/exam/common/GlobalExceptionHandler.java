package com.exam.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 参数校验 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleValidation(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            return ApiResponse.fail(fieldErrors.get(0).getDefaultMessage());
        }
        return ApiResponse.fail("参数校验失败");
    }

    /** 业务异常（服务层抛出的 RuntimeException） */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleRuntime(RuntimeException ex) {
        log.warn("业务异常: {}", ex.getMessage());
        return ApiResponse.fail(ex.getMessage());
    }

    /** 参数异常 */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return ApiResponse.fail(ex.getMessage());
    }

    /** 文件过大 */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleFileTooBig(MaxUploadSizeExceededException ex) {
        return ApiResponse.fail("文件过大，请上传小于 10MB 的文件");
    }

    /** 兜底 — 未预期的服务器错误 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Object> handleException(Exception ex) {
        log.error("未预期异常", ex);
        return ApiResponse.fail("服务器繁忙，请稍后重试");
    }
}
