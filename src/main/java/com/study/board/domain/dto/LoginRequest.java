package com.study.board.domain.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "로그인 아이디를 작성해주세요.")
    private String loginId;

    @NotBlank(message = "비밀번호를 작성해주세요.")
    private String password;

    public String getLoginId() {
        return loginId;
    }
    public String getPassword() {
        return password;
    }
}
