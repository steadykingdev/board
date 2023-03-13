package com.study.board.domain.entity;

import com.study.board.domain.Role;
import com.study.board.domain.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MemberTest {

    @DisplayName("멤버 생성 테스트")
    @Test
    public void createMember() throws Exception {
        //given
        Member member = new Member("testMember", "1q2w3e4r!", "테스트", Role.ROLE_USER);

        //when, then
        assertThat(member.getLoginId()).isEqualTo("testMember");
        assertThat(member.getPassword()).isEqualTo("1q2w3e4r!");
        assertThat(member.getNickname()).isEqualTo("테스트");
        assertThat(member.getRole()).isEqualTo(Role.ROLE_USER);
    }

    @DisplayName("프로필 이미지 추가 메서드 테스트")
    @Test
    public void setProfileImgPath() throws Exception {
        //given
        Member member = new Member("testMember", "1q2w3e4r!", "테스트", Role.ROLE_USER);

        String uploadPath = "/path";
        String profileImgName = "profile.png";
        member.setProfileImg(uploadPath, profileImgName);

        //when, then
        assertThat(member.getLoginId()).isEqualTo("testMember");
        assertThat(member.getPassword()).isEqualTo("1q2w3e4r!");
        assertThat(member.getNickname()).isEqualTo("테스트");
        assertThat(member.getRole()).isEqualTo(Role.ROLE_USER);
        assertThat(member.getProfileImgPath()).isEqualTo(uploadPath + "/" + profileImgName);
    }

}