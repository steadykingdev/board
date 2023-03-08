package com.study.board.domain.dto;

import com.study.board.domain.Role;

public class MemberInfoResponse {

    private String loginId;
    private String nickname;
    private Role role;
    private byte[] img;

    public MemberInfoResponse(String loginId, String nickname, Role role, byte[] img) {
        this.loginId = loginId;
        this.nickname = nickname;
        this.role = role;
        this.img = img;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getNickname() {
        return nickname;
    }

    public Role getRole() {
        return role;
    }

    public byte[] getImg() {
        return img;
    }
}
