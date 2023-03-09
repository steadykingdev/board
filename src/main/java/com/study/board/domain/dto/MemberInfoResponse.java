package com.study.board.domain.dto;

import com.study.board.domain.Role;

public class MemberInfoResponse {

    private String loginId;
    private String nickname;
    private Role role;
    private byte[] profileImg;

    public MemberInfoResponse(String loginId, String nickname, Role role) {
        this.loginId = loginId;
        this.nickname = nickname;
        this.role = role;
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

    public byte[] getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(byte[] profileImg) {
        this.profileImg = profileImg;
    }
}
