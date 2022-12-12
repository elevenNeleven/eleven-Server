package com.konkuk.eleveneleven.src.member.repository;

import com.konkuk.eleveneleven.common.enums.Status;
import com.konkuk.eleveneleven.src.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByKakaoId(Long kakaoId);

    boolean existsByIdxAndStatus(Long memberIdx, Status status);


    @Query("select m from Member m where m.kakaoId=:kakaoId")
    Member findByKakaoId(@Param("kakaoId") Long kakaoId);

    @Query("select m.kakaoId from Member m where m.kakaoId=:kakaoId")
    Optional<Member> findOptionalByKakaoId(@Param("kakaoId") Long kakaoId);

    @Query("select m from Member m where m.kakaoId=:kakaoId")
    Optional<Member> findByKakaoIdOptional(@Param("kakaoId") Long kakaoId);

    Member findByIdxAndStatus(Long memberIdx, Status status);

    @Query("select m from Member m join fetch m.roomMember rm join fetch rm.room r where m.idx=:memberIdx")
    Member findWithRoomInfo(@Param("memberIdx") Long memberIdx);

    @Query("select m from Member m join fetch m.roomMember rm join fetch rm.room r where m.kakaoId=:kakaoId")
    Optional<Member> findWithRoomOptional(@Param("kakaoId")Long kakaoId);

    @Query("select m from Member m where m.idx =:memberIdx")
    Member findByMemberIdx(@Param("memberIdx") Long memberIdx);

    @Query("select m from Member m join fetch m.roomMember rm join fetch rm.room r where m.idx=:memberIdx")
    Optional<Member> findWithRoomInfoOptional(@Param("memberIdx") Long memberIdx);



}
