package com.study.board.repository;

import com.study.board.domain.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment save(Comment comment);

    Page<Comment> findAllByPostId(Long postId, Pageable pageable);
}
