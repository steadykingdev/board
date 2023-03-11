package com.study.board.domain.entity;

import com.study.board.domain.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import static jakarta.persistence.GenerationType.*;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String loginId;

    private String password;

    private String nickname;

    private Role role;

    private String profileImgPath;


    public Member() {}
    public Member(String loginId, String password, String nickname, Role role) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public Role getRole() {
        return role;
    }

    public String getProfileImgPath() {
        return profileImgPath;
    }

    public void setProfileImg(String uploadPath, String profileImgName) {
        this.profileImgPath = uploadPath + "/" + profileImgName;
    }
}
