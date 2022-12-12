package com.konkuk.eleveneleven.src.matched_room_member.repository;

import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.src.matched_room.MatchedRoom;
import com.konkuk.eleveneleven.src.matched_room_member.MatchedRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchedRoomMemberRepository extends JpaRepository<MatchedRoomMember, Long> {

    Optional<MatchedRoomMember> findByMemberIdxAndStatus(Long memberIdx, Status status);

    boolean existsByMemberIdxAndStatus(Long memberIdx, Status status);

    boolean existsByMemberIdxAndMatchedRoomIdxAndStatus(Long memberIdx, Long MatchedRoomIdx, Status status);

    List<MatchedRoomMember> findAllByMatchedRoomAndStatus(MatchedRoom matchedRoom, Status status);


    @Query("select mrm from MatchedRoomMember mrm where mrm.member.idx=:memberIdx and mrm.matchedRoom.idx=:matchedRoomIdx and mrm.status=:status")
    MatchedRoomMember findOne(@Param("memberIdx") Long memberIdx, @Param("matchedRoomIdx") Long matchedRoomIdx,
                              @Param("status") Status status);

}
