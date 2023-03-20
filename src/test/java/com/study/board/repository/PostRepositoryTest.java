package com.study.board.repository;

import com.study.board.domain.Role;
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

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Post savedPost;

    private Member savedMember;

    @BeforeEach
    public void setup() {
        String loginId = "testMember";
        String password = "testPassword12!@";
        String nickname = "test";

        savedMember = new Member(loginId, password, nickname, Role.ROLE_USER);
        memberRepository.save(savedMember);

        String title = "test title";
        String content = "test content";

        savedPost = new Post(title, content, savedMember);
        postRepository.save(savedPost);
    }

    @DisplayName("게시물을 저장 할 때 db에 저장된 게시물을 반환하는 테스트")
    @Test
    public void shouldReturnPostWhenEntitySaved() throws Exception {
        //given
        String title = "post creation title test";
        String content = "post creation title content";

        Post post = new Post(title, content, savedMember);

        //when

        Post testPost = postRepository.save(post);

        //then
        assertThat(testPost.getId()).isEqualTo(savedPost.getId() +1L);
        assertThat(testPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(testPost.getContent()).isEqualTo(post.getContent());
        assertThat(testPost.getCreatedTime()).isEqualTo(post.getCreatedTime());
        assertThat(testPost.getMember().getId()).isEqualTo(post.getMember().getId());
    }

    @DisplayName("게시물을 모두 조회하는 테스트")
    @Test
    public void shouldReturnPostListWhenEntityDoesExist() throws Exception {
        //given
        String title = "post creation title test";
        String content = "post creation title content";

        Post post = new Post(title, content, savedMember);
        postRepository.save(post);
        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<Post> postList = postRepository.findAll(pageable);

        //then
        assertThat(postList.getTotalElements()).isEqualTo(2);
    }

    @DisplayName("게시물을 모두 조회하는 테스트(게시물이 존재하지 않을 때)")
    @Test
    public void shouldReturnEmptyListWhenEntityDoesNotExist() throws Exception {
        //given
        postRepository.delete(savedPost);

        //when
        List<Post> postList = postRepository.findAll();

        //then
        assertThat(postList).isEmpty();
    }

    @DisplayName("게시물 id값(pk값)으로 조회하는 테스트")
    @Test
    public void shouldReturnPostWhenEntityExist() throws Exception {
        //given
        Long id = savedPost.getId();

        //when
        Optional<Post> optionalPost = postRepository.findById(id);

        //then
        assertThat(optionalPost.isPresent()).isNotNull();
        assertThat(optionalPost.get().getId()).isEqualTo(savedPost.getId());
        assertThat(optionalPost.get().getTitle()).isEqualTo(savedPost.getTitle());
        assertThat(optionalPost.get().getContent()).isEqualTo(savedPost.getContent());
        assertThat(optionalPost.get().getCreatedTime()).isEqualTo(savedPost.getCreatedTime());
        assertThat(optionalPost.get().getMember().getId()).isEqualTo(savedPost.getMember().getId());
    }

    @DisplayName("게시물 id값(pk값)으로 조회하는 테스트(존재하지 않을 때)")
    @Test
    public void shouldReturnOptionalEmptyWhenEntityNotExist() throws Exception {
        //given
        Long id = savedPost.getId();
        postRepository.delete(savedPost);

        //when
        Optional<Post> optionalPost = postRepository.findById(id);

        //then
        assertThat(optionalPost.isPresent()).isFalse();
    }
}