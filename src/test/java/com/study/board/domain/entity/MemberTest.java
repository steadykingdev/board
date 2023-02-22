package com.study.board.domain.entity;

import com.study.board.domain.Role;
import com.study.board.domain.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MemberTest {

    @DisplayName("멤버가 생성되는지 테스트")
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

}