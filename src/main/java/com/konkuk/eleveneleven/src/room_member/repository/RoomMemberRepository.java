package com.konkuk.eleveneleven.src.room_member.repository;

import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.src.room.Room;
import com.konkuk.eleveneleven.src.room_member.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {

    /** FK인 memberIdx로 MemberRoom을 조회하면서 -> 연관된 Room , 연관된 OwnerMember를 함께 조회해옴 */
    @Query("select mr from RoomMember mr join fetch mr.room r join fetch r.ownerMember m where mr.member.idx=:memberIdx and mr.status=:status")
    Optional<RoomMember> findByMemberIdxAndStatus(@Param("memberIdx") Long memberIdx, @Param("status")Status status);

    boolean existsByMemberIdxAndStatus(Long memberIdx, Status status);

    @Query("select rm from RoomMember rm where rm.idx=:idx")
    RoomMember findByIdx(@Param("idx") Long idx);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE RoomMember rm SET rm.room = :newRoom where rm.room = :beforeRoom")
    void updateRoomMemberRoom(@Param("newRoom") Room newRoom, @Param("beforeRoom") Room beforeRoom);


    /** 여기서 약간 이슈가 있었음 */
    @Modifying(clearAutomatically = true)
    @Query("delete from RoomMember rm where rm.idx=:roomMemberIdx")
    void deleteByRoomMemberIdx(@Param("roomMemberIdx") Long roomMemberIdx);

    List<RoomMember> findByRoomIdx(Long roomIdx);

}
