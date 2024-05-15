package com.ssafy.user.controller;

import com.ssafy.user.dto.request.UserNicknameChangeRequestDto;
import com.ssafy.user.dto.request.UserPasswordChangeRequestDto;
import com.ssafy.user.global.response.code.ResponseCode;
import com.ssafy.user.global.response.code.SuccessCode;
import com.ssafy.user.global.response.structure.SuccessResponse;
import com.ssafy.user.service.UserSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserSettingController {

    private final UserSettingService userSettingService;

    @PutMapping("/{userId}/nickname") //닉네임 변경
    @PreAuthorize("authentication.name == #userId.toString()") // 요청자의 ID가 경로 변수와 일치하는지 확인
    public ResponseEntity<?> changeNickname(@PathVariable("userId") Long userId,
        @RequestBody UserNicknameChangeRequestDto requestDto) {
        ResponseCode responseCode = userSettingService.changeNickname(userId, requestDto);
        return SuccessResponse.createSuccess(SuccessCode.NICKNAME_UPDATE_SUCCESS);
    }

    @PutMapping("/{userId}/password") //비밀번호 변경
    @PreAuthorize("authentication.name == #userId.toString()") // 요청자의 ID가 경로 변수와 일치하는지 확인
    public ResponseEntity<?> changePassword(@PathVariable("userId") Long userId,
        @RequestBody UserPasswordChangeRequestDto requestDto) {
        ResponseCode responseCode = userSettingService.changePassword(userId, requestDto);
        return SuccessResponse.createSuccess(SuccessCode.PASSWORD_UPDATE_SUCCESS);
    }
}