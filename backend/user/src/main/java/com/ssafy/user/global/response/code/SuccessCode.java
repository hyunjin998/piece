package com.ssafy.user.global.response.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements ResponseCode {
    /**
     * User
     */
    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),

    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃에 성공했습니다."),

    CHECK_EMAIL_GOOD(HttpStatus.OK, "사용 가능한 이메일입니다"),

    CHECK_EMAIL_BAD(HttpStatus.OK, "이미 사용 중인 이메일입니다"),

    JOIN_SUCCESS(HttpStatus.CREATED, "회원가입에 성공했습니다."),

    NICKNAME_UPDATE_SUCCESS(HttpStatus.OK, "닉네임 변경에 성공했습니다."),

    CHECK_PASSWORD_SUCCESS(HttpStatus.OK, "비밀번호 확인에 성공했습니다."),

    PASSWORD_UPDATE_SUCCESS(HttpStatus.OK, "비밀번호 변경에 성공했습니다."),

    CHECK_NICKNAME_GOOD(HttpStatus.OK, "사용 가능한 닉네임입니다."),
    CHECK_NICKNAME_BAD(HttpStatus.OK, "이미 사용 중인 닉네임입니다."),
    TUTORIAL_SUCCESS(HttpStatus.OK, "튜토리얼을 다시 보지 않습니다."),

    SAVE_IMAGE_SUCCESS(HttpStatus.OK, "이미지 경로 저장 성공했습니다."),

    READ_TUTORIAL_SUCCESS(HttpStatus.OK,"튜토리얼 보기 여부를 확인했습니다."),

    /**
     * s3
     */
    GET_PRESIGNEDURL_SUCCESS(HttpStatus.OK, "presigned url을 받았습니다."),
    DOWNLOAD_IMAGE_SUCCESS(HttpStatus.OK, "이미지 다운로드에 성공했습니다."),
    DELETE_IMAGE_SUCCESS(HttpStatus.OK, "이미지 삭제에 성공했습니다."),
    ;

    private final HttpStatus httpStatus;

    private final String message;
}
