package com.konkuk.eleveneleven.src.auth.service;

import com.konkuk.eleveneleven.common.enums.Gender;
import com.konkuk.eleveneleven.common.enums.Screen;
import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.config.BaseException;
import com.konkuk.eleveneleven.config.BaseResponseStatus;
import com.konkuk.eleveneleven.src.auth.dto.PostAuthMetaReqDto;
import com.konkuk.eleveneleven.src.member.Member;
import com.konkuk.eleveneleven.src.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberRepository memberRepository;

    public Screen postAuth(Long kakaoId){

        Optional<Member> memberByKakaoId = memberRepository.findByKakaoIdOptional(kakaoId);

        if(memberByKakaoId.isEmpty()){
            // member 저장
            Member member = new Member(kakaoId,null,null,null,null,null,null);
            memberRepository.save(member);
            return Screen.AUTH_SCREEN;

        } else {
            // 이미 DB상에 존재하는 member Validation 처리
            /** error com.konkuk.eleveneleven.config.BaseException: null ?? */
            if (memberByKakaoId.get().getStatus().equals(Status.ONGOING)) {
                return Screen.AUTH_SCREEN;
            } else if (memberByKakaoId.get().getStatus().equals(Status.ACTIVE)) {
                return Screen.LOGIN_SCREEN;
            } else {
                throw new BaseException(BaseResponseStatus.BLACK_MEMBER,"DB상에 INACTIVE Member 존재");
            }
        }

    }


    public void postAuthMeta(Long memberIdx, PostAuthMetaReqDto postAuthMetaReqDto){

        Member memberByKakaoId = memberRepository.findByMemberIdx(memberIdx);

        if (memberByKakaoId.getStatus().equals(Status.INACTIVE)) {
            throw new BaseException(BaseResponseStatus.BLACK_MEMBER,"DB상에 INACTIVE Member 존재");
        } else if (memberByKakaoId.getStatus().equals(Status.ACTIVE)) {
            throw new BaseException(BaseResponseStatus.ALREADY_MEMBER,"DB상에 ACTIVE Member 존재");
        }

        memberByKakaoId.setName(postAuthMetaReqDto.getName());
        memberByKakaoId.setGender(postAuthMetaReqDto.getGender());
        memberByKakaoId.setSchoolName(postAuthMetaReqDto.getSchoolName());
        memberByKakaoId.setStudentId(postAuthMetaReqDto.getStudentId());
        memberByKakaoId.setSchoolEmail(postAuthMetaReqDto.getSchoolEmail());
        memberByKakaoId.setMajor(postAuthMetaReqDto.getMajor());
        memberByKakaoId.setStatus(Status.ACTIVE);

        memberRepository.save(memberByKakaoId);
    }


}
