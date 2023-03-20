package com.study.board.service;

import com.study.board.domain.Role;
import com.study.board.domain.entity.Comment;
import com.study.board.domain.entity.Member;
import com.study.board.domain.entity.Post;
import com.study.board.repository.CommentRepository;
import com.study.board.repository.MemberRepository;
import com.study.board.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks    // Mock 객체를 주입해줌
    private CommentService commentService;

    private Member member;

    private Post post;

    private Comment comment;

    @BeforeEach
    public void setUp() {

        String loginId = "testMember";
        String password = "1q2w3e4r!@";
        String nickname = "테스터";
        Role role = Role.ROLE_USER;

        member = new Member(loginId, password, nickname, role);
        ReflectionTestUtils.setField(member, "id", 152345L);

        String title = "testTitle";
        String postContent = "testContent";
        post = new Post(title, postContent, member);
        ReflectionTestUtils.setField(post, "id", 152348L);

        String commentContent = "testCommentContent";
        comment = new Comment(post, member, commentContent);
        ReflectionTestUtils.setField(comment, "id", 152124L);
    }

}