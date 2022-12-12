package com.konkuk.eleveneleven.src.member.controller;

import com.konkuk.eleveneleven.common.encryption.AES128;
import com.konkuk.eleveneleven.common.encryption.AES256;
import com.konkuk.eleveneleven.common.jwt.JwtUtil;
import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.config.BaseResponse;
import com.konkuk.eleveneleven.config.BaseResponseStatus;
import com.konkuk.eleveneleven.src.member.dto.EmailAuthDto;
import com.konkuk.eleveneleven.src.member.dto.EmailDto;
import com.konkuk.eleveneleven.src.member.dto.LoginMemberDto;
import com.konkuk.eleveneleven.src.member.dto.LogoutDto;
import com.konkuk.eleveneleven.src.member.request.EmailAuthRequest;
import com.konkuk.eleveneleven.src.member.request.EmailRequest;
import com.konkuk.eleveneleven.src.member.service.MemberService;
import com.konkuk.eleveneleven.src.room.dto.MatchingYnDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AES256 aes256;

//    @GetMapping("/aes")
//    public String aes(@RequestParam String str){
//        String encryptedPath = "";
//        try {
//            encryptedPath = aes256.encrypt(str);
//        } catch (Exception e) {
//            throw new BaseException(BaseResponseStatus.ENCRYPT_FAIL, "AES256 암호화 시점에서 에러 터짐");
//        }
//        return encryptedPath;
//    }
//
//    @GetMapping("/aes/decryption")
//    public String decryption(@RequestParam String str){
//        String decryptedPath = null;
//        try {
//            decryptedPath = aes256.decrypt(str);
//        } catch (Exception e) {
//            throw new BaseException(BaseResponseStatus.FAIL_DECRYPT, "AES256 복호화 시점에서 에러 터짐");
//        }
//        return decryptedPath;
//    }

    /**
     * [API. 2] : 로그인 API
     * */
    @PostMapping("/auth/login")
    public BaseResponse<LoginMemberDto> login(@RequestAttribute Long memberIdx){
        return new BaseResponse<>(memberService.checkLogin(memberIdx));
    }

    /**
     * [API. 3] : 학교 이메일을 받아 , 그 이메일로 인증코드 전송
     * */
    @PostMapping("/auth/email")
    public BaseResponse<EmailDto> sendEmail(@RequestAttribute Long memberIdx,
                                            @Validated @RequestBody EmailRequest emailRequest){
        return new BaseResponse<>(memberService.sendAuthMail(memberIdx, emailRequest.getEmail()));
    }


    /**
     * [API. 4] : 학교 이메일로 전송한 인증코드 일치 여부 확인
     * */
    @GetMapping("/auth/email")
    public BaseResponse<EmailAuthDto> checkAuthCode(@RequestAttribute Long memberIdx,
                                                    @Validated @ModelAttribute EmailAuthRequest emailAuthRequest){
        return new BaseResponse<>(memberService.checkAuthCode(memberIdx, emailAuthRequest.getAuthCode()));
    }

    /** [API. 21] : 회원 탈퇴 API*/
    @PatchMapping("/auth/quit")
    public BaseResponse<LogoutDto> quit(@RequestAttribute Long memberIdx){
        return new BaseResponse<>(memberService.checkQuit(memberIdx));
    }

    /** [방 매칭 여부 확인 API] : 알림을 보내기 위해 , 특정 방에 속한 회원이 존재할때, 그 방의 상태가 준비상태인지 여부를 보냄 */
    @GetMapping("/member/status")
    public BaseResponse<MatchingYnDto> getMatchingYn(@RequestAttribute Long memberIdx){
        return new BaseResponse<>(memberService.getMatching(memberIdx));
    }

}
