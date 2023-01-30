package com.konkuk.eleveneleven.common.advice;

import com.konkuk.eleveneleven.common.exceptions.ValidationFail;
import com.konkuk.eleveneleven.common.exceptions.ValidationFailForField;
import com.konkuk.eleveneleven.common.exceptions.ValidationFailForObject;
import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.config.BaseResponse;
import com.konkuk.eleveneleven.config.BaseResponseStatus;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.BindException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalAdvice {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse exceptionHandler(BaseException e){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e.getStatus(), e.getInternalMessage());
        return new BaseResponse(e.getStatus());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse bindExceptionHandler(BindException e, BindingResult bindingResult){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e, e.getMessage());
        ValidationFail validationFail = makeValidationError(bindingResult);
        return BaseResponse.failBeanValidation(validationFail);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse bindExHandler(MethodArgumentNotValidException e, BindingResult bindingResult){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e, e.getMessage());
        ValidationFail validationFail = makeValidationError(bindingResult);
        return BaseResponse.failBeanValidation(validationFail);
    }

    private ValidationFail makeValidationError(BindingResult bindingResult){
        return  ValidationFail.builder()
                .fieldList(bindingResult.getFieldErrors().stream()
                        .map(f -> new ValidationFailForField(f))
                        .collect(Collectors.toList()))
                .objectList(bindingResult.getGlobalErrors().stream()
                        .map(o -> new ValidationFailForObject(o))
                        .collect(Collectors.toList()))
                .build();

    }

    /** ----------------------------------------------------------------------------------------------------- */

    /** jwt가 예상하는 형식과 다른 형식이거나 구성 */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse unsupportedJwtException(UnsupportedJwtException e){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e, e.getMessage());
        return new BaseResponse(BaseResponseStatus.INAVALID_JWT_TOKEN);
    }

    /** 잘못된 jwt 구조 */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse malformedJwtException(MalformedJwtException e){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e, e.getMessage());
        return new BaseResponse(BaseResponseStatus.INAVALID_JWT_TOKEN);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse stringIndexOutOfBoundsException(StringIndexOutOfBoundsException e){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e, e.getMessage());
        return new BaseResponse(BaseResponseStatus.INAVALID_JWT_TOKEN);
    }


    /** JWT의 유효기간이 초과 */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse expiredJwtException(ExpiredJwtException e){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e, e.getMessage());
        return new BaseResponse(BaseResponseStatus.EXPIRED_JWT_TOKEN);
    }

    /** JWT의 서명실패(변조 데이터) */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse signatureException(SignatureException e){
        log.error("EXCEPTION = {} , INTERNAL_MESSAGE = {}", e, e.getMessage());
        return new BaseResponse(BaseResponseStatus.EXPIRED_JWT_TOKEN);
    }


}
