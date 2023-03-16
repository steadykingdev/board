package com.study.board.repository;

import com.study.board.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Post save(Post post);

    Page<Post> findAll(Pageable pageable);
}
