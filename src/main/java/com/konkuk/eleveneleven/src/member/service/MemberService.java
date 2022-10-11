package com.konkuk.eleveneleven.src.member.service;

import com.konkuk.eleveneleven.common.enums.Screen;
import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.common.jwt.JwtUtil;
import com.konkuk.eleveneleven.common.mail.MailUtil;
import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.config.BaseResponseStatus;
import com.konkuk.eleveneleven.src.member.Member;
import com.konkuk.eleveneleven.src.member.dto.EmailDto;
import com.konkuk.eleveneleven.src.member.dto.LoginMemberDto;
import com.konkuk.eleveneleven.src.member.repository.MemberRepository;
import com.konkuk.eleveneleven.src.member.request.EmailRequest;
import com.konkuk.eleveneleven.src.member.request.LoginRequest;
import com.konkuk.eleveneleven.src.room_member.RoomMember;
import com.konkuk.eleveneleven.src.room_member.repository.RoomMemberRepository;
import com.konkuk.eleveneleven.src.school.School;
import com.konkuk.eleveneleven.src.school.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final RoomMemberRepository memberRoomRepository;
    private final SchoolRepository schoolRepository;
    private final JwtUtil jwtUtil;
    private final MailUtil mailUtil;


    /**
     * 로그인 서비스
     */
    public LoginMemberDto checkLogin(LoginRequest loginRequest) {

        //0. loginRequest에 있는 kakaoId의 유효성 검사
        if (memberRepository.existsByKakaoId(loginRequest.getKakaoId()) == false) {
            throw new BaseException(BaseResponseStatus.FAIL_LOGIN, "요청으로 들어온 kakaoId가 유효하지 않습니다.");
        }

        //1. 유효한 kakaoId로 JWT를 생성
        String token = jwtUtil.createToken(loginRequest.getKakaoId().toString());

        //2. Member를 조회하여 , 다음의 상황을 판단
        /**
         * ONGOING일 경우 -> 아직 app 사용 인증 절차가 끝나지 않음 -> 인증 화면으로 가도록 응답을 보냄
         * ACTIVE일 경우
         * 1) 어떤 방도 만들지 않았거나 or 어떤 방에도 참여하지 않은 경우 -> 메인화면으로 가도록 응답을 보냄
         * 2) 방을 만들었거나 or 다른사람이 만든 방에 소속된 경우 && 이때 매칭버튼을 누르거나 or 누르지 않았거나 -> 해당 방 화면으로 가도록 해야함
         * */

        Member member = memberRepository.findByKakaoId(loginRequest.getKakaoId());

        if (member.getStatus() == Status.ONGOING) {
            return LoginMemberDto.builder()
                    .token(token)
                    .memberIdx(member.getIdx())
                    .memberName(member.getName())
                    .schoolName(member.getSchoolName())
                    .status(member.getStatus())
                    .screen(Screen.AUTH_SCREEN)
                    .isBelongToRoom(false)
                    .isRoomOwner(false)
                    .roomIdx(-1L)
                    .build();
        }

        // 그렇지 않으면 Member의 status가 ACTIVE인 경우
        LoginMemberDto loginMemberDto = LoginMemberDto.builder()
                .token(token)
                .memberIdx(member.getIdx())
                .memberName(member.getName())
                .schoolName(member.getSchoolName())
                .status(member.getStatus()).build();

        memberRoomRepository.findByMemberIdx(member.getIdx()).ifPresentOrElse(
                mr -> setLoginMemberDtoAtBelongRoom(loginMemberDto, mr),
                () -> setLoginMemberDtoAtNotBelongRoom(loginMemberDto)
        );

        return loginMemberDto;

    }


    //로그인한 Member가 Room을 만들었거나 or 다른사람이 만든 Room에 속한 경우 -> LoginMemberDto를 세팅하는 메소드
    private void setLoginMemberDtoAtBelongRoom(LoginMemberDto loginMemberDto, RoomMember rm){
        loginMemberDto.setScreen(Screen.ROOM_SCREEN);
        loginMemberDto.setIsBelongToRoom(true);
        loginMemberDto.setIsRoomOwner(Optional.ofNullable(rm.getMember().getRoom()).isPresent());
        loginMemberDto.setRoomIdx(rm.getRoom().getIdx());
    }

    //로그인한 Member가 Room에 속하지 않는 경우 -> LoginMemberDto를 세팅하는 메소드
    private void setLoginMemberDtoAtNotBelongRoom(LoginMemberDto loginMemberDto){
        loginMemberDto.setScreen(Screen.MAIN_SCREEN);
        loginMemberDto.setIsBelongToRoom(false);
        loginMemberDto.setIsRoomOwner(false);
        loginMemberDto.setRoomIdx(-1L);
    }


    /** 인증메일 보내는 서비스 */
    @Transactional
    public EmailDto sendAuthMail(Long kakaoId, EmailRequest emailRequest){

        //0. 일단 등록된 서울시 내 대학교 메일 계정인지 확인 후
        checkEmailDomain(emailRequest.getEmail());

        // 1. 일단 해당 메일로 인증코드를 보낸 뒤
        String authCode = mailUtil.sendEmail(emailRequest.getEmail());

        //2. 그 인증 코드를 DB에 저장
        updateAuthCode(kakaoId, authCode);

        return EmailDto.builder().authCode(authCode).build();
    }

    private void checkEmailDomain(String email){

        //1. 전체 이메일에서 , @뒤의 email domain 분리
        String emailDomain = email.split("@")[1];

        //2. 이후 DB에 등록된 서울시 전체 대학교의 이메일 도메인들을 대상으로 , 해당 분리시킨 도메인과 일치하는게 하나라도 있는지 check
        List<School> schoolList = schoolRepository.findAll();
        boolean isValidEmailDomain = schoolList.stream()
                .anyMatch(s -> Pattern.matches(s.getEmailDomain(), emailDomain));

        //3. 만약 전체 도메인들 중, 일치하는게 하나도 없다면 -> 이는 학교 계정이 아닌것으로 판별하고 예외 발생
        if(isValidEmailDomain==false){
            throw new BaseException(BaseResponseStatus.INVALID_EMAIL_DOMAIN, "학교 이메일 계정이 아닙니다.");
        }

    }

    private void updateAuthCode(Long kakaoId, String authCode){
        Member member = memberRepository.findByKakaoId(kakaoId);
        member.setAuthCode(authCode);
    }

    /** 인증메일로 보낸 인증코드의 유효성 검사 서비스 */
    public String checkAuthCode(Long kakaoId, String authCode){
        Member member = memberRepository.findByKakaoId(kakaoId);

        if(member.getAuthCode().equals(authCode)==false){
            throw new BaseException(BaseResponseStatus.INVALID_AUTH_CODE, "인증 코드가 일치하지 않아, 이메일 인증에 실패");

        }

        return "학교 메일 인증이 정상 처리되었습니다.";
    }




}
