package com.study.board.repository;

import com.study.board.domain.Role;
import com.study.board.domain.entity.Comment;
import com.study.board.domain.entity.Member;
import com.study.board.domain.entity.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member savedMember;

    private Post savedPost;

    private Comment savedComment;

    @BeforeEach
    public void setup() {
        String loginId = "testMember";
        String password = "testPassword12!@";
        String nickname = "test";

        savedMember = new Member(loginId, password, nickname, Role.ROLE_USER);
        memberRepository.save(savedMember);

        String title = "test title";
        String postContent = "test content";

        savedPost = new Post(title, postContent, savedMember);
        postRepository.save(savedPost);

        String commentContent = "test comment";
        savedComment = new Comment(savedPost, savedMember, commentContent);
        commentRepository.save(savedComment);
    }

    @DisplayName("댓글 저장 할 때 db에 저장된 댓글을 반환하는 테스트")
    @Test
    public void shouldReturnCommentWhenEntitySaved() throws Exception {
        //given
        String content = "comment creation content";

        Comment comment = new Comment(savedPost, savedMember, content);

        //when

        Comment testComment = commentRepository.save(comment);

        //then
        assertThat(testComment.getId()).isEqualTo(savedComment.getId() +1L);
        assertThat(testComment.getContent()).isEqualTo(comment.getContent());
        assertThat(testComment.getCreatedTime()).isEqualTo(comment.getCreatedTime());
        assertThat(testComment.getPost().getId()).isEqualTo(comment.getPost().getId());
        assertThat(testComment.getMember().getId()).isEqualTo(comment.getMember().getId());
    }

//    @DisplayName("하나의 게시물의 댓글을 모두 조회하는 테스트")
//    @Test
//    public void shouldReturnCommentListByOnePost() throws Exception {
//        //given
//        String content = "comment creation content";
//        Pageable pageable = PageRequest.of(0, 5);
//
//        Comment comment = new Comment(savedPost, savedMember, content);
//        commentRepository.save(comment);
//
//        //when
//        Page<Comment> commentList = commentRepository.findAllByPostId(savedPost.getId(), pageable);
//
//        //then
//        assertThat(commentList.getSize()).isEqualTo(2);
//    }

    @DisplayName("하나의 게시물의 댓글을 모두 조회하는 테스트(댓글이 존재하지 않을 때)")
    @Test
    public void shouldReturnEmptyListWhenEntityDoesNotExist() throws Exception {
        //given
        commentRepository.delete(savedComment);
        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<Comment> commentList = commentRepository.findAllByPostId(savedPost.getId(), pageable);

        //then
        assertThat(commentList).isEmpty();
    }

    @DisplayName("댓글 id값(pk값)으로 조회 테스트")
    @Test
    public void shouldReturnCommentByCommentId() throws Exception {
        //given
        Long commentId = savedComment.getId();

        //when
        Optional<Comment> optionalComment = commentRepository.findById(commentId);

        //then
        assertThat(optionalComment.isPresent()).isTrue();
        assertThat(optionalComment.get().getId()).isEqualTo(commentId);
        assertThat(optionalComment.get().getContent()).isEqualTo(savedComment.getContent());
        assertThat(optionalComment.get().getCreatedTime()).isEqualTo(savedComment.getCreatedTime());
    }

    @DisplayName("댓글 id값(pk값)으로 조회 테스트(댓글이 존재하지 않을 때)")
    @Test
    public void shouldReturnOptionalEmptyByCommentId() throws Exception {
        //given
        Long commentId = savedComment.getId();
        commentRepository.delete(savedComment);

        //when
        Optional<Comment> optionalComment = commentRepository.findById(commentId);

        //then
        assertThat(optionalComment.isPresent()).isFalse();
    }
}