package com.study.board.service;

import com.study.board.domain.Role;
import com.study.board.domain.dto.PostRequest;
import com.study.board.domain.dto.PostResponse;
import com.study.board.domain.entity.Member;
import com.study.board.domain.entity.Post;
import com.study.board.exception.ForbiddenException;
import com.study.board.exception.PostNotFoundException;
import com.study.board.repository.MemberRepository;
import com.study.board.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks    // Mock 객체를 주입해줌
    private PostService postService;

    private Member member;

    private Post post;

    @BeforeEach
    public void setUp() {
        member = new Member();
        ReflectionTestUtils.setField(member, "id", 156345L);
        ReflectionTestUtils.setField(member, "loginId", "testMember");
        ReflectionTestUtils.setField(member, "password", "1q2w3e4r!@");
        ReflectionTestUtils.setField(member, "nickname", "테스터");
        ReflectionTestUtils.setField(member, "role", Role.ROLE_USER);

        post = new Post("testTitle", "testContent", member);
        ReflectionTestUtils.setField(post, "id", 134342L);

    }

    @DisplayName("게시물 작성 성공 테스트")
    @Test
    public void createPostSuccessTest() throws Exception {
        //given
        PostRequest postRequest = new PostRequest();
        ReflectionTestUtils.setField(postRequest, "title", "testTitle");
        ReflectionTestUtils.setField(postRequest, "content", "testContent");

        //when
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post savedPost = postService.createPost(member.getId(), postRequest);

        //then
        assertThat(savedPost.getTitle()).isEqualTo(postRequest.getTitle());
        assertThat(savedPost.getContent()).isEqualTo(postRequest.getContent());
        assertThat(savedPost.getMember().getId()).isEqualTo(member.getId());
        assertThat(savedPost.getMember().getLoginId()).isEqualTo(member.getLoginId());

        verify(memberRepository, times(1)).findById(anyLong());     // 한번만 수행했는지 검증
        verify(postRepository, times(1)).save(any(Post.class));// 한번만 수행했는지 검증
    }

    @DisplayName("게시물 목록 조회 성공 테스트")
    @Test
    public void getPostListSuccessTest() throws Exception {
        //given
        List<Post> postList = new ArrayList<>();
        postList.add(post);

        //when
        when(postRepository.findAll()).thenReturn(postList);
        List<PostResponse> postResponseList = postService.getPostList();

        //then
        assertThat(postResponseList.size()).isEqualTo(1);
    }

    @DisplayName("게시물 조회 성공 테스트")
    @Test
    public void getPostSuccessTest() throws Exception {
        //given
        Long postId = 1L;

        //when
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        PostResponse postResponse = postService.getPost(postId);

        //then
        assertThat(postResponse.getTitle()).isEqualTo(post.getTitle());
        assertThat(postResponse.getContent()).isEqualTo(post.getContent());
    }

    @DisplayName("게시물 조회 실패 테스트(게시물 id로 조회 시 없을 때)")
    @Test
    public void getPostFailTest() throws Exception {
        //given
        Long postId = 2L;

        //when, then
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> postService.getPost(postId))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessage("존재하지 않는 게시물입니다.");
    }

    @DisplayName("게시물 수정 성공 테스트")
    @Test
    public void UpdatePostSuccessTest() throws Exception {
        //given
        Long postId = 1L;

        PostRequest postRequest = new PostRequest();
        ReflectionTestUtils.setField(postRequest, "title", "updateTitle");
        ReflectionTestUtils.setField(postRequest, "content", "updateContent");

        //when
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        postService.updatePost(postId, member.getId(), postRequest);

        //then
        assertThat(post.getTitle()).isEqualTo(postRequest.getTitle());
        assertThat(post.getContent()).isEqualTo(postRequest.getContent());
        verify(postRepository, times(1)).findById(anyLong());// 한번만 수행했는지 검증
    }

    @DisplayName("게시물 수정 실패 테스트(자신의 게시물이 아닐 때)")
    @Test
    public void updatePostForbiddenExceptionTest() throws Exception {
        //given
        Long postId = 1L;

        Member loginMember = new Member();
        ReflectionTestUtils.setField(member, "id", 2L);
        ReflectionTestUtils.setField(member, "loginId", "testMember2");
        ReflectionTestUtils.setField(member, "password", "1q2w3e4r!@");
        ReflectionTestUtils.setField(member, "nickname", "테스터2");
        ReflectionTestUtils.setField(member, "role", Role.ROLE_USER);

        PostRequest postRequest = new PostRequest();
        ReflectionTestUtils.setField(postRequest, "title", "updateTitle");
        ReflectionTestUtils.setField(postRequest, "content", "updateContent");

        //when
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        //then
        assertThatThrownBy(() -> postService.updatePost(postId, loginMember.getId(), postRequest))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("나의 게시물이 아닙니다.");

        verify(postRepository, times(1)).findById(anyLong());// 한번만 수행했는지 검증
    }

    @DisplayName("게시판 삭제 성공 테스트")
    @Test
    public void deletePostSuccessTest() throws Exception {
        //given
        Long postId = 1L;

        //when
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        postService.deletePost(postId, member.getId());

        //then
        verify(postRepository, times(1)).delete(post);
    }

    @DisplayName("게시판 삭제 실패 테스트")
    @Test
    public void deletePostForbiddenExceptionTest() throws Exception {
        //given
        Long postId = 1L;
        Long memberId = 2L;

        //when
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        //then
        assertThatThrownBy(() -> postService.deletePost(postId, memberId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("나의 게시물이 아닙니다.");
    }
}