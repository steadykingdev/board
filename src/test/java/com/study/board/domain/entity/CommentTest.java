package com.study.board.domain.entity;

import com.study.board.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    @DisplayName("댓글이 생성되는지 테스트")
    @Test
    public void createComment() throws Exception {
        //given
        Member member1 = new Member("testMember1", "testMember12!", "test1", Role.ROLE_USER);
        Member member2 = new Member("testMember2", "testMember12!", "test2", Role.ROLE_USER);
        Post post = new Post("test title", "test content", member1);
        Comment comment = new Comment(post, member2, "test comment");

        //when, then
        assertThat(comment.getContent()).isEqualTo("test comment");
        assertThat(comment.getCreatedTime()).isNotNull();
        assertThat(comment.getPost().getTitle()).isEqualTo(post.getTitle());
        assertThat(comment.getPost().getContent()).isEqualTo(post.getContent());
        assertThat(comment.getPost().getMember().getLoginId()).isEqualTo(member1.getLoginId());
        assertThat(comment.getMember().getLoginId()).isEqualTo(member2.getLoginId());
    }

}