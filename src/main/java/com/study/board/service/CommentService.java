package com.study.board.service;

import com.study.board.domain.dto.CommentRequest;
import com.study.board.domain.dto.CommentResponse;
import com.study.board.domain.entity.Comment;
import com.study.board.domain.entity.Member;
import com.study.board.domain.entity.Post;
import com.study.board.exception.CommentNotFoundException;
import com.study.board.exception.ForbiddenException;
import com.study.board.exception.PostNotFoundException;
import com.study.board.exception.UserNotFoundException;
import com.study.board.repository.CommentRepository;
import com.study.board.repository.MemberRepository;
import com.study.board.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, MemberRepository memberRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void createComment(Long postId, Long memberId, CommentRequest commentRequest) {
        Post targetPost = findPostById(postId);
        Member commenter = findMemberById(memberId);

        Comment comment = new Comment(targetPost, commenter, commentRequest.getContent());

        commentRepository.save(comment);

    }

    public List<CommentResponse> getCommentList() {
        List<Comment> commentList = commentRepository.findAll();
        return commentList.stream().map(p -> CommentResponse.fromEntity(p))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateComment(Long commentId, Long memberId, CommentRequest commentRequest) {
        Comment comment = getCommentById(commentId);

        if (!isMyComment(comment, memberId)) {
            throw new ForbiddenException("나의 댓글이 아닙니다.");
        }

        comment.update(commentRequest.getContent());
    }

    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        Comment comment = getCommentById(commentId);

        if (!isMyComment(comment, memberId)) {
            throw new ForbiddenException("나의 댓글이 아닙니다.");
        }

        commentRepository.delete(comment);
    }

    private boolean isMyComment(Comment comment, Long memberId) {
        Long commenterId = comment.getMember().getId();

        return commenterId.equals(memberId);

    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    throw new CommentNotFoundException("존재하지 않는 댓글입니다.");
                });
    }

    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    throw new PostNotFoundException("존재하지 않는 게시물입니다.");
                });
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException("존재하지 않는 회원입니다.");
                });
    }
}
