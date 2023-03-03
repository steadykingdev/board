package com.study.board.domain.dto;

import com.study.board.domain.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class SignupRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 8, max = 15, message = "아이디는 8자 이상 15자 이하로 입력해주세요.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$", message = "비밀번호는 8~16자 영문 , 숫자, 특수문자를 모두 포함해야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passwordCheck;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    private String nickname;

    private String role;

    private MultipartFile imgFile;

    public boolean checkPassword() {
        return password.equals(passwordCheck);
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordCheck() {
        return passwordCheck;
    }

    public String getNickname() {
        return nickname;
    }

    public String getRole() {
        return role;
    }

    public MultipartFile getImgFile() {
        return imgFile;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasswordCheck(String passwordCheck) {
        this.passwordCheck = passwordCheck;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setImgFile(MultipartFile imgFile) {
        this.imgFile = imgFile;
    }
}
