package com.konkuk.eleveneleven.src.matched_room_member.controller;

import com.konkuk.eleveneleven.config.BaseResponse;
import com.konkuk.eleveneleven.src.matched_room.MatchedRoom;
import com.konkuk.eleveneleven.src.matched_room_member.dto.DeleteMatchedRoomMemberDto;
import com.konkuk.eleveneleven.src.matched_room_member.dto.GetMatchedRoomMemberRes;
import com.konkuk.eleveneleven.src.matched_room_member.service.MatchedRoomMemberService;
import com.konkuk.eleveneleven.src.matched_room_member.vo.MatchingMember;
import com.konkuk.eleveneleven.src.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "matched/room")
public class MatchedRoomMemberController {
    private final MatchedRoomMemberService matchedRoomMemberService;

    /** [매칭 후 매칭된 방의 멤버들 확인 API] */
    @GetMapping("/member")
    public GetMatchedRoomMemberRes getMatchedRoomMember(@RequestAttribute Long memberIdx) {
        List<Member> matchedRoomMembers = matchedRoomMemberService.getMatchedRoomMember(memberIdx);
        MatchedRoom matchedRoom = matchedRoomMemberService.getMatchedRoom(memberIdx);
        boolean isOwner = matchedRoomMemberService.isOwnerMember(memberIdx);

        return getMatchedRoomMemberDto(matchedRoomMembers, matchedRoom, isOwner);
    }

    private GetMatchedRoomMemberRes getMatchedRoomMemberDto(List<Member> matchedRoomMembers,MatchedRoom matchedRoom, boolean isOwner){
        List<MatchingMember> matchingMembers = new ArrayList<>();
        for (Member matchedRoomMember : matchedRoomMembers){
            boolean isOwnerInRoom = false;
            if(matchedRoomMember.getMatchedRoom() != null) {
                isOwnerInRoom = true;
            }

            matchingMembers.add(MatchingMember.builder()
                    .memberName(matchedRoomMember.getName())
                    .schoolName(matchedRoomMember.getSchoolName())
                    .major(matchedRoomMember.getMajor())
                    .isOwner(isOwnerInRoom)
                    .build());
        }

        return GetMatchedRoomMemberRes.builder()
                .matchedRoomIdx(matchedRoom.getIdx())
                .isOwner(isOwner)
                .matchingMembers(matchingMembers)
                .build();
    }


    /** [MathcedRoom에서 나가는 API ] */
    @DeleteMapping("/member/{matchedRoomIdx}")
    public BaseResponse<DeleteMatchedRoomMemberDto> deleteMatchedRoomMember(@RequestAttribute Long memberIdx,
                                                                            @PathVariable Long matchedRoomIdx){
        return new BaseResponse<>(matchedRoomMemberService.goOutMatchedRoom(memberIdx, matchedRoomIdx));
    }

}
