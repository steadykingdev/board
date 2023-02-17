package com.study.board.domain;

public class JwtPayload {

    private Long id;
    private String loginId;
    private Role role;

    public JwtPayload(Long id, String loginId, Role role) {
        this.id = id;
        this.loginId = loginId;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getLoginId() {
        return loginId;
    }

    public Role getRole() {
        return role;
    }
}
