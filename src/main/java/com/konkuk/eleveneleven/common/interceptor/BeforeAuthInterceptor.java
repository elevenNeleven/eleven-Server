package com.konkuk.eleveneleven.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konkuk.eleveneleven.common.encryption.AES128;
import com.konkuk.eleveneleven.common.encryption.AES256;
import com.konkuk.eleveneleven.config.BaseResponse;
import com.konkuk.eleveneleven.config.BaseResponseStatus;
import com.konkuk.eleveneleven.src.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BeforeAuthInterceptor implements HandlerInterceptor {

    private final MemberRepository memberRepository;
    private boolean returnValue;
    private boolean testResultValue;
    private final AES256 aes256;
    private ObjectMapper objectMapper = new ObjectMapper();



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        if(Boolean.parseBoolean(request.getHeader("isTest"))){

            Optional.ofNullable(request.getHeader("memberIdx")).ifPresentOrElse(
                    k -> sendSuccessTest(request, Long.parseLong(k)),
                    () -> {sendFailTest(response, BaseResponseStatus.NO_MEMBER_IDX);}

            );
            return testResultValue;
        }

        // 만약 이 값이 있으면 , 아직 인증을 다 마치지 못한 사용자라고 가정하고 -> 여기서 바로 kakaoId를 attribute로 넣어준다.
        Optional.ofNullable(request.getHeader("encryptedKakaoId")).ifPresentOrElse(
                ek -> sendSuccess(request, response, ek),
                () -> {sendFail(response, BaseResponseStatus.NO_ENCRYPTED_KAKAO_ID);}
        );

        return returnValue;

    }

    private Boolean sendSuccess(HttpServletRequest request, HttpServletResponse response, String encryptedKakaoId){
        Long kakaoId = null;
        try {
            kakaoId = Long.parseLong(aes256.decrypt(encryptedKakaoId));
        } catch (Exception e) {
            sendFail(response, BaseResponseStatus.FAIL_DECRYPT);
        }

        if(!memberRepository.existsByKakaoId(kakaoId)){
            sendFail(response, BaseResponseStatus.INVALID_KAKAO_ID);
            return false;
        }

        request.setAttribute("memberIdx", memberRepository.findByKakaoId(kakaoId).getIdx());
        this.returnValue = true;
        return true;
    }

    private Boolean sendFail(HttpServletResponse response, BaseResponseStatus status){

        //응답의 meta 정보를 setting한 후
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            //실질적인 응답값을 , JSON 형식의 String으로 변환화여 보낸다. (단 BaseResponse라는 공통 응답 형식을 지키면서)
            String result = objectMapper.writeValueAsString(new BaseResponse(status));
            response.getWriter().print(result);
        }
        catch (IOException e){
            log.error("모든 인증을 마치기 전까지 필수 헤더값인 encryptedKakaoId값이 들어오지 않아 or 옳바르지 않은 kakaoId 값이 들어와서 그에따른 응답을 처리하는 과정에서 IOException이 발생하였습니다.");
        }

        log.error("EXCEPTION = {}, message = {}", status, status.getMessage());
        this.returnValue = false;
        return false;
    }

    private Boolean sendSuccessTest(HttpServletRequest request, Long memberIdx){
        request.setAttribute("memberIdx", memberIdx);
        this.testResultValue = true;
        return true;
    }

    private Boolean sendFailTest(HttpServletResponse response, BaseResponseStatus status){

        //응답의 meta 정보를 setting한 후
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            //실질적인 응답값을 , JSON 형식의 String으로 변환화여 보낸다. (단 BaseResponse라는 공통 응답 형식을 지키면서)
            String result = objectMapper.writeValueAsString(new BaseResponse(status));
            response.getWriter().print(result);
        }
        catch (IOException e){
            log.error("테스트 시 필수 헤더값인 memberIdx값이 들어오지 않아, 그에따른 응답을 처리하는 과정에서 IOException이 발생하였습니다.");
        }

        log.error("EXCEPTION = {}, message = {}", status, status.getMessage());
        this.testResultValue = false;
        return false;
    }
}

