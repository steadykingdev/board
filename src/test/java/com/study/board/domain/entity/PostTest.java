package com.study.board.domain.entity;

import com.study.board.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PostTest {

    @DisplayName("게시물이 생성되는지 테스트")
    @Test
    public void createPost() throws Exception {
        //given
        Member member = new Member("testMember", "testMember12!", "test", Role.ROLE_USER);
        Post post = new Post("test title", "test content", member);

        //when, then
        assertThat(post.getTitle()).isEqualTo("test title");
        assertThat(post.getContent()).isEqualTo("test content");
        assertThat(post.getMember().getLoginId()).isEqualTo(member.getLoginId());
        assertThat(post.getMember().getPassword()).isEqualTo(member.getPassword());
        assertThat(post.getMember().getNickname()).isEqualTo(member.getNickname());
        assertThat(post.getMember().getRole()).isEqualTo(member.getRole());
    }
}