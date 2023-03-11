package com.study.board.domain.dto;

import jakarta.validation.constraints.NotBlank;

public class MemberDeleteRequest {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    String password;

    public String getPassword() {
        return password;
    }
}
