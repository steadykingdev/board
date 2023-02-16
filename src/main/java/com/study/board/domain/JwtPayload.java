package com.study.board.domain;

public class JwtPayload {

    private String loginId;
    private Role role;

    public JwtPayload(String loginId, Role role) {
        this.loginId = loginId;
        this.role = role;
    }

    public String getLoginId() {
        return loginId;
    }

    public Role getRole() {
        return role;
    }
}
