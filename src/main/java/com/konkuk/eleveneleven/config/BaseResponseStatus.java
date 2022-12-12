package com.konkuk.eleveneleven.config;

import lombok.Getter;

/**
 * [ 1000단위 ]
 *  1000 : 요청 성공
 *  2 : Request 오류
 *  3 : Reponse 오류
 *  4 : DB, Server 오류
 *
 * [ 100단위 ]
 *  0 : 공통 오류
 *  1 : Member 오류
 *  2 : Room 오류
 *  3 : RoomMember 오류
 *  4 : MatchedRoom 오류
 *  5 : MatchedRoomMember 오류
 *
 *
 * [10단위]
 *  0~19 : Common
 *  20~39 : GET
 *  40~59 : POST
 *  60~79 : PATCH
 *  80~99 : else
 */

@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    /**
     * 2000 : Request 오류
     */
    INVALID_BEAN_VALIDATION(false, 2001, "요청값에 대한 Bean Validation 처리 과정에서 오류가 있습니다."),
    NO_JWT_TOKEN(false, 2002, "인증 인가를 위한 jwt token값이 존재하지 않습니다."),
    INAVALID_JWT_TOKEN(false, 2003, "인증 인가를 위한 jwt token값이 유효하지 않습니다."),
    EXPIRED_JWT_TOKEN(false, 2004, "인증 인가를 위한 jwt token의 만료 시간이 초과되었습니다."),
    INVALID_ROOM_TIME(false, 2005, "방생성, 방참여, 방나가기 기능은 23:12~11:10 까지는 사용할 수 없는 기능입니다."),
    NO_MEMBER_IDX(false, 2006, "테스트 시 인증을 위한 memberIdx 값이 존재하지 않습니다."),
    FAIL_CREATE_AES128(false, 2007, "AES128 인스턴스 생성중 예외가 발생하였습니다."),
    FAIL_ENCRYPT(false, 2008, "AES256 암호화 과정에서 예외가 발생하였습니다."),
    FAIL_DECRYPT(false, 2009, "AES256 복호화 과정에서 예외가 발생하였습니다."),
    NO_ENCRYPTED_KAKAO_ID(false, 2010, "필수 헤더값인 encrpytedKakaoId 값이 없습니다."),
    NO_KAKAO_ID(false, 2011, "인증전 , kakaoId 값이 존재하지 않습니다."),
    INVALID_KAKAO_ID(false, 2012, "유효하지 않은 kakaoId가 들어왔습니다."),
    INVALID_MEMBER(false,2101, "아직 모든 인증 과정을 다 거치지 않은 사용자 입니다."),
    INACTIVE_MEMBER(false, 2102, "회원탈퇴한 회원입니다."),
    INVALID_AUTH_CODE(false,2121, "이메일 인증 코드가 일치하지 않아, 이메일 인증에 실패하였습니다."),
    FAIL_LOGIN(false, 2141, "로그인 시점 : 해당 사용자는 아직 인증이 다 끝나지 않은 사용자 입니다."),
    INVALID_EMAIL_DOMAIN(false, 2142, "서울시에 등록된 대학교의 이메일 도메인이 아닙니다."),
    FAIL_SEND_EMAIL(false,2142, "인증 메일 전송에 실패하였습니다."),
    ALREADY_MEMBER(false,2143, "이미 회원가입된 유저입니다."),
    ONGOING_MEMBER(false,2144, "인증중인 유저입니다."),
    BLACK_MEMBER(false,2144, "가입이 불가능한 유저입니다."),
    INVALID_UNIV(false,2005, "서비스 사용이 불가한 대학입니다."),
    INVALID_USER(false,2006, "서비스 사용이 불가한 유저입니다.(대학원생)"),
    INVALID_ID_CARD(false,2007, "유효하지 않은 학생증입니다."),
    INVALID_IMG_FORMAT(false,2008, "유효하지 않은 이미지 형식입니다."),
    INVALID_OWNER_MEMBER(false, 2141, "유효하지 않은 방장 멤버 idx 값 입니다."),
    INVALID_ROOM(false, 2241, "유효하지 않은 방 idx 값 입니다."),
    INVALID_ROOM_CODE(false, 2261, "유효하지 않은 방 참여코드 입니다."),
    BELONG_TO_ANOTHER_ROOM(false, 2262, "사용자가 이미 방에 소속되어 있습니다."),
    BELONG_TO_THE_ROOM(false,2263, "사용자가 이미 그 참여코드의 방에 소속되어 있습니다."),
    IS_NOT_OWNER(false, 2264, "해당 사용자는 그 방의 방장이 아닙니다."),
    NOT_SUITABLE_SIZE(false, 2265, "매칭 가능한 방의 인원은 최소 2명에서 최대 6명으로 제한됩니다."),
    ALREADY_MATCHING_STATUS(false, 2266, "해당 방은 이미 매칭 준비상태인 방 입니다."),
    BELONG_TO_MATCHED_ROOM(false, 2267, "사용자가 아직 매칭된 방에 소속되어 있습니다."),
    NESTED_BELONT_TO_ROOM(false, 2341, "사용자가 이미 해당 방에 참여한 상태입니다."),
    DELETED_ROOM(false, 2342, "해당 방은 이미 삭제되었습니다."),
    DELETED_ROOM_MEMBER(false, 2343, "해당 사용자는 이미 방에서 나갔습니다."),
    ALREADY_MATCHING_YN_Y(false, 2344, "이미 매칭 준비가 완료된 방입니다."),
    INVALID_GENDER(false, 2345, "참여하려는 방의 성별과 부합하지 않습니다."),
    OVER_NUM_OF_MEMBR(false, 2346, "참여하려는 방의 인원이 이미 6명 이상입니다."),
    INVALID_NUM_OF_MATCHING(false, 2347, "매칭 준비 상태로 변경 시점 : 방 인원수가 2명 이상 6명 이하일 때만 매칭 준비를 할 수 있습니다."),
    NOT_BELONG_TO_ROOM(false, 2461, "사용자는 어느 방에도 속해있지 않습니다."),
    INVALID_MATCHED_ROOM_IDX(false, 2462, "유효하지 않은 matchedRoomIdx 값 입니다."),
    IS_NOT_MATCHED_ROOM_OWNER(false, 2463, "해당 사용자는 MatchedRoom의 방장이 아닙니다."),
    ALREADY_SETTING_URL(false, 2464, "이미 오픈채팅 url이 설정된 MatchedRoom 입니다."),
    IS_NOT_BELEONG_TO_MATCHED_ROOM(false, 2465, "이 MatchedRoom에 속한 사용자가 아닙니다."),
    IS_NOT_SETTING_URL(false, 2466, "이 MatchedRoom에는 아직 오픈채팅 url이 설정되어 있지 않습니다."),
    IS_NOT_OPEN_URL(false, 2467, "이 url은 오픈채팅 url이 아닙니다."),
    ALREADY_DELETE_MATCHED_ROOM(false, 2468, "해당 MatchedRoom은 이미 삭제되었습니다."),
    FAIL_TO_MATCHING(false, 3501, "매칭에 실패한 유저입니다."),

    /**
     * 3000 : Response 오류
     */
    NO_MONTHLY_BADGE(false, 3030, "이번 달에 획득한 뱃지가 없습니다. (PRO, LOVER, MASTER)"),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),
    MODIFY_WKT_FAIL(false, 4002, "WKT 변환을 실패하였습니다."),
    ENCRYPT_FAIL(false, 4003, "암호화를 실패하였습다."),
    MODIFY_OBJECT_FAIL(false, 4004, "Request 객체 변환에 실패하였습니다."),
    FAIL_WEB_SOCKET_CONNECTION(false, 2005, "웹소켓 연결에 실패하였습니다."),
    S3UPLOAD_ERROR(false, 4080, "파일 업로드에 실패하였습니다."),
    DETECT_TEXT_FAIL(false, 4081, "OCR 파일 분석에 실패하였습니다."),
    ROOM_EMPTY_IN_DB(false, 4201, "DB에 Room이 하나도 없습니다."),
    ROOM_MEMBER_EMPTY_IN_DB(false, 4202, "DB에 RoomMember가 하나도 없습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
